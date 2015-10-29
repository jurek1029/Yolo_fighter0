package com.example.yolo_fighter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class YoloMultislayerBT extends YoloMultislayerBase {
	private static final int REQUEST_ENABLE_BT_AND_SCAN = 688_44_10;
	private static final int REQUEST_DISCOVERABLE_BT_AND_START_SERVER = 688_44_20;
	private static final int DISCOVERABLE_DURATION = 300; // works as activity result for make discoverable
	private static final UUID mUUID = UUID.fromString("c843a4a7-1e8e-496c-86b4-439cc3936b6d");
	
	private boolean priorityLock = false; // blokada przesylania pozycji na czas wysylania reliable
	
	
	ConnectedThread mConnectedThread;

	private boolean scanFlag = false; // wartoœæ false oznacza, ¿e nie trwa skanowanie
	private BluetoothAdapter mBluetoothAdapter;
	protected List<device> btDevices = new ArrayList<device>(10);
	protected List<BluetoothDevice> btDevicesRaw = new ArrayList<BluetoothDevice>(10);
	private List<BluetoothDevice> alreadyFoundDevices = new ArrayList<BluetoothDevice>();

	protected BluetoothServerSocket mBluetoothServerSocket;
	private BluetoothSocket someSocket;
	BluetoothSocket socket;
	

	@Override
	protected void sendMessageToAllreliable(final byte[] data) {
		this.priorityLock = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ReliableMessageLock.getInstance().getKLock();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mConnectedThread.write(data);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} finally {
					priorityLock = false;
					ReliableMessageLock.getInstance().releaseKLock();
				}
			}
		}).start();
	}

	@Override
	protected void sendMessageToAll(byte[] data) {
		if (!priorityLock)
			mConnectedThread.write(data);
	}

	public void joinGame() {
		koniec();
		cleanupBeforeNewPlay();
		startUp1();
		enableBT(REQUEST_ENABLE_BT_AND_SCAN);
	}

	public void createGame(GameProperties gp) {
		cleanupBeforeNewPlay();
		startUp1();
		isServer = true;
		enableBT(REQUEST_DISCOVERABLE_BT_AND_START_SERVER);		
	}

	public YoloMultislayerBT() {
		YoloEngine.timeOffset = 1000;
	}





	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			// When discovery finds a device

			if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				// clearing any existing list data
				alreadyFoundDevices.clear();
			}

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {

				// Get the BluetoothDevice object from the Intent
				BluetoothDevice newDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (!alreadyFoundDevices.contains(newDevice)) { // we have a new device

					mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
					Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
					ArrayList<BluetoothDevice> btDevicesPairedRaw = new ArrayList<BluetoothDevice>();

					for (BluetoothDevice bluetoothDevice : pairedDevices) {
						btDevicesPairedRaw.add(bluetoothDevice);
					}

					alreadyFoundDevices.add(newDevice);
					Collections.sort(btDevicesPairedRaw, new Comparator<BluetoothDevice>() {
						@Override
						public int compare(BluetoothDevice bt1, BluetoothDevice bt2) {
							return bt1.getAddress().compareTo(bt2.getAddress());
						}
					});

					int foundDevIndex = Collections.binarySearch(btDevicesPairedRaw, newDevice, new Comparator<BluetoothDevice>() {
						@Override
						public int compare(BluetoothDevice bt1, BluetoothDevice bt2) {
							return bt1.getAddress().compareTo(bt2.getAddress());
						}
					});

					if (foundDevIndex > 0) {
						//newDevice = mBluetoothAdapter.getRemoteDevice(btDevicesPairedRaw.get(foundDevIndex).getAddress());
						newDevice = btDevicesPairedRaw.get(foundDevIndex);
						debugLog("BT device already paired");
					}
					final BluetoothDevice dk = newDevice; // must be final

					AlertDialog.Builder mDialog = new AlertDialog.Builder(mActivity).setNegativeButton("Nope", null).setPositiveButton(R.string.yes, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mBluetoothAdapter.cancelDiscovery();
							scanFlag = false;
							connectTo(dk);
						}
					}).setMessage(dk.getName());
					mDialog.show();
				}
			}
		}
	};

	public void enableBT(int action) {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // Checking if BT is available
		if (mBluetoothAdapter == null) {
			debugLog("No BT support :(");
			return;
		}

		if(action == REQUEST_ENABLE_BT_AND_SCAN) {
			if (!mBluetoothAdapter.isEnabled()) { // Checking if BT is already enabled
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			    mActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT_AND_SCAN);			    
			} else
				findDevices();
		} else if(action == REQUEST_DISCOVERABLE_BT_AND_START_SERVER) {
			Intent discoverableIntent = new	Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE); // enables BT automatically
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION);
			mActivity.startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE_BT_AND_START_SERVER);	
		}
		
	}
	
	public void incomingAction(int request, int response, Intent data) {
		if(request == REQUEST_ENABLE_BT_AND_SCAN) 
			if(response == Activity.RESULT_OK) {
				debugLog("BT turned on");				
				findDevices();						
			}
			// TODO what if user clicks deny
				
		
		if(request == REQUEST_DISCOVERABLE_BT_AND_START_SERVER)
			if(response == DISCOVERABLE_DURATION) {
				debugLog("BT discoverable");	
				prepareServer();				
			}
			// TODO what if user clicks deny
		
	}

	public void findDevices() {
		btDevices.clear();
		btDevicesRaw.clear(); // clear the list of devices
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		mActivity.registerReceiver(mReceiver, filter); // TODO Don't forget to unregister during onDestroy

		if (!scanFlag) {
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			mBluetoothAdapter.startDiscovery();
			scanFlag = true;
		} else {
			System.out.println("Scan already in progress");
			return;
		}
		// Alternatywnie mo¿na zatrzymaæ i rozpocz¹æ na nowo

		System.out.println("Scan in progress");
	}

	public void prepareServer() {
		// oczekujemy na po³¹czenia przychodz¹ce
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!(mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON)) { // czekamy na gotowy BT
					try {
						Thread.sleep(10);
					} catch (InterruptedException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
				try {
					//mBluetoothServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("server-socket", mUUID); forces new pairing every connection
					mBluetoothServerSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("server-socket", mUUID);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				while (true) {
					try {
						socket = mBluetoothServerSocket.accept();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						break;
					}
					if (socket != null) {
						debugLog("server: connected succesfully");
						mConnectedThread = new ConnectedThread(socket);
						mConnectedThread.start();												
						onConnectionEstablished();
						
						try {
							mBluetoothServerSocket.close(); // teraz zamykamy, ale chyba póŸniej mo¿na zostawiæ otwarte dopóki nie po³¹czymy
															// wszyskiego
						} catch (IOException e) {
						}
						break;
					}
				}
			}
		}).start();
	}

	public void connectTo(final BluetoothDevice btDev) {
		// ³¹czymy siê z okreœlonym urz¹dzeniem

		if (btDev == null)
			return;

		new Thread(new Runnable() {

			@Override
			public void run() {
				mBluetoothAdapter.cancelDiscovery(); // zatrzymujemy ewentualne wyszukiwanie
				scanFlag = false;

				try {
					System.out.println("connecting to" + " " + btDev.getName());
					//someSocket = btDev.createRfcommSocketToServiceRecord(mUUID); forces new pairing every connection
					someSocket = btDev.createInsecureRfcommSocketToServiceRecord(mUUID);
				} catch (IOException e1) {
					
				}
				try {
					someSocket.connect();
					debugLog("client: connected succesfully");
					mConnectedThread = new ConnectedThread(someSocket);
					mConnectedThread.start();
					onConnectionEstablished();
				} catch (IOException connectException) {

					try {
						someSocket.close();
						debugLog("connection failed");
					} catch (IOException e) {
					}

					return;
				}

				// Also jeœli program nie zdechnie wczeœniej, to pod someSocket mamy pod³¹czone urz¹dzenie
				// manageConnectedSocket(someSocket);
			}
		}).start();

	}

	private void startUp1() {
		/*
		 * mActivity.runOnUiThread(new Runnable() { public void run() { if (mProgressDialog != null)
		 * mProgressDialog.setMessage("Waiting for other players..."); } });
		 */
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mBluetoothAdapter.cancelDiscovery();
		scanFlag = false;
		YoloEngine.playerParticipantID = mBluetoothAdapter.getName() + " " + mBluetoothAdapter.getAddress();		
	}

	String opponentName;
	
	
	private void onConnectionEstablished() {
		YoloEngine.mMultislayer.sendMessageToAllreliable(YoloEngine.mMultislayer.sendPreStartInfo(new int[] { YoloEngine.currentPlayerInfo.getSK1EQ(), YoloEngine.currentPlayerInfo.getSK2EQ(),
				YoloEngine.currentPlayerInfo.getSK3EQ() }));
		
		opponentName = mConnectedThread.devName + " " + mConnectedThread.devMAC; // TODO should be list/array for more players
		YoloEngine.participantsBT.add(opponentName);
		YoloEngine.participantsBT.add(YoloEngine.playerParticipantID);
		
		
		Collections.sort(YoloEngine.participantsBT);

		for (String p : YoloEngine.participantsBT) {
			if (!p.equals(YoloEngine.playerParticipantID)) {
				YoloEngine.opponents.add(p);
			}
		}
		
		if (isServer) {			
			this.notreadyPlayersNumber = YoloEngine.participantsBT.size()-1;
			final String teamAssignPattern = assignTeamsXX();			
			YoloEngine.TeamAB[YoloEngine.MyID].isServer = true;

			new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(600);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					YoloEngine.mMultislayer.sendTeamAssignment(Integer.parseInt(teamAssignPattern, 2));
				}
			}).start();
			
			
			new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(1200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					YoloEngine.mMultislayer.sendGameProperties();
				}
			}).start();
					
			YoloEngine.startTime = (System.currentTimeMillis() + YoloEngine.countdownTime + YoloEngine.timeOffset);
			YoloEngine.mMultislayer.sendMaxLife(); // TODO to powinno byæ póŸniej, ¿eby by³a pwenoœæ, ¿e TeamAB jest dobrze usuzp³enione
		}
	}
	
	
	private void cleanupBeforeNewPlay() {
		YoloEngine.playerParticipantID = "";
		isServer = false;
		opponentName = "";
		for (int i = 0; i < YoloEngine.TeamAB.length; i++) {			
			YoloEngine.TeamAB[i] =  new YoloPlayer(1000f, 1000f, false, 666,i);
		}
		if (YoloEngine.participantsBT != null)
			YoloEngine.participantsBT.clear();
		if (YoloEngine.opponents != null)
			YoloEngine.opponents.clear();		
	}

	public void koniec() {
		try {
			mConnectedThread.cancel();
		} catch (Exception e) {

		}

	}


	

	@Override
	protected void manuallyAssignTeams() {
		// TODO Auto-generated method stub

	}

}

class device {
	String name;
	String address;

	public device(String name, String address) {
		this.name = name;
		this.address = address;
	}

}

class ConnectedThread extends Thread {
	private final BluetoothSocket mmSocket;
	private final InputStream mmInStream;
	private final OutputStream mmOutStream;
	public String devName;
	public String devMAC;

	public ConnectedThread(BluetoothSocket socket) {
		mmSocket = socket;
		InputStream tmpIn = null;
		OutputStream tmpOut = null;
		devName = mmSocket.getRemoteDevice().getName();
		devMAC = mmSocket.getRemoteDevice().getAddress();

		// Get the input and output streams, using temp objects because
		// member streams are final
		try {
			tmpIn = socket.getInputStream();
			tmpOut = socket.getOutputStream();
		} catch (IOException e) {
		}

		mmInStream = tmpIn;
		mmOutStream = tmpOut;
	}

	public void run() {
		byte[] buffer = new byte[1024]; // buffer store for the stream
		int bytes; // bytes returned from read()

		// Keep listening to the InputStream until an exception occurs
		while (true) {
			try {
				bytes = mmInStream.read(buffer);
				// String str = new String(buffer, 0, bytes, "UTF-8");
				ByteBuffer bb = ByteBuffer.wrap(buffer);
				YoloEngine.mMultislayer.processMessage(bb);
			} catch (IOException e) {
				break;
			}
		}
	}

	/* Call this from the main activity to send data to the remote device */
	public void write(byte[] bytes) {
		try {
			mmOutStream.write(bytes);
		} catch (IOException e) {
		}
	}

	/* Call this from the main activity to shutdown the connection */
	public void cancel() {
		try {
			mmSocket.close();
		} catch (IOException e) {
		}
	}
}

class ReliableMessageLock {
	private ReliableMessageLock() {
	}

	private static class SingletonHolder {
		public static final ReliableMessageLock INSTANCE = new ReliableMessageLock();
	}

	public static ReliableMessageLock getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private Semaphore kLock = new Semaphore(1);

	public void getKLock() throws InterruptedException {
		kLock.acquire();
	}

	public void releaseKLock() {
		kLock.release();
	}
}
