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

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

public class YoloMultislayerBT extends YoloMultislayerBase {
					
	private boolean priorityLock = false; // blokada przesylania pozycji na czas wysylania reliable 

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
					kk.write(data);					
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
		if(!priorityLock) kk.write(data);
	}

	public void joinGame() {	
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mBluetoothAdapter.cancelDiscovery();
		scanFlag = false;
		
		startUp1();
		prepareBluetooth();
		findDevices();		
	}
	
	public void createGame() {
		// acting as server, our BT must visible
		startUp1();
		prepareBluetooth();
		prepareServer();
	}
	
	
	public YoloMultislayerBT() {
		mUUID = UUID.fromString("c843a4a7-1e8e-496c-86b4-439cc3936b6d"); // hardcoded UUID :D
		if(YoloMainMenu.btn_invite != null && YoloMainMenu.btn_quick != null) {
			YoloMainMenu.btn_invite.setText("Create");
			YoloMainMenu.btn_quick.setText("Join");	
			
			YoloMainMenu.btn_quick.setEnabled(true);
			YoloMainMenu.btn_invite.setEnabled(true);
			
			debugLog("");
		}
		YoloEngine.timeOffset = 1000;
	}

	boolean flag = false;
	ConnectedThread kk;

	private boolean scanFlag = false; // wartoœæ false oznacza, ¿e nie trwa skanowanie
	private BluetoothAdapter mBluetoothAdapter;
	protected List<device> btDevices = new ArrayList<device>(10);
	protected List<BluetoothDevice> btDevicesRaw = new ArrayList<BluetoothDevice>(10); // de facto wystarczy tylko to, ale tak mi siê spodoba³o zrobienie
																					// klasy na dane (klasa device), wiêc zosta³o te¿
																					// above
	protected BluetoothServerSocket mBluetoothServerSocket;
	private BluetoothSocket someSocket;
	BluetoothSocket socket;
	private UUID mUUID;

	private Handler handler = new Handler();

	private List<BluetoothDevice> alreadyFoundDevices = new ArrayList<BluetoothDevice>();

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

					int kk = Collections.binarySearch(btDevicesPairedRaw, newDevice, new Comparator<BluetoothDevice>() {
						@Override
						public int compare(BluetoothDevice bt1, BluetoothDevice bt2) {
							return bt1.getAddress().compareTo(bt2.getAddress());
						}
					});

					if (kk > 0) {
						newDevice = btDevicesPairedRaw.get(kk);
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

				// btDevicesRaw.add(newDevice);
				// device mDevice = new device(newDevice.getName(), newDevice.getAddress());
				// btDevices.add(mDevice);

				// System.out.println(btDevices.size() + " device(s) found");
				// for (int i = 0; i < btDevices.size(); i++)
				// System.out.println(btDevices.get(i).name + "  " + btDevices.get(i).address);

			}
			// scanFlag = false;
		}
	};


	public void prepareBluetooth() {
		// Sprawdza i w³¹cza BT

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // Czy urz¹dzenie ma BT?
		if (mBluetoothAdapter == null) {
			debugLog("No BT support :(");
			return;
		}

		if (!mBluetoothAdapter.isEnabled()) { // Czy BT jest w³¹czony?
			// FIXME zak³adamy, ¿e wczeœniej zapytaliœmy o zgodê usera!
			mBluetoothAdapter.enable();
		}
	}

	public void findDevices() {
		// Tworzy listê urz¹dzeñ

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
				while (!(mBluetoothAdapter.getState() == mBluetoothAdapter.STATE_ON)) { // czekamy na gotowy BT
					try {
						Thread.sleep(10);
					} catch (InterruptedException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
				try {
					mBluetoothServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("server-socket", mUUID);
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
						// manageConnectedSocket(socket);
						debugLog("server: connected succesfully");
						kk = new ConnectedThread(socket);
						kk.start();
						startUp2();
						try {
							mBluetoothServerSocket.close(); // teraz zamykamy, ale chyba póŸniej mo¿na zostawiæ otwarte dopóki nie po³¹czymy wszyskiego
						} catch (IOException e) {
						}
						// tu trzeba jeszcze trochê dopisaæ...
						break;

					}

				}
			}
		}).start();
	}

	public void connectTo(final BluetoothDevice btDev) {
		// ³¹czymy siê z okreœlonym urz¹dzeniem, i oznacza numer urz¹dzenia na naszej liœcie

		if (btDev == null)
			return;

		new Thread(new Runnable() {

			@Override
			public void run() {
				mBluetoothAdapter.cancelDiscovery(); // zatrzymujemy ewentualne wyszukiwanie
				scanFlag = false;

				try {
					System.out.println("connecting with" + " " + btDev.getName());
					someSocket = btDev.createRfcommSocketToServiceRecord(mUUID);
				} catch (IOException e1) {
				}
				try {
					someSocket.connect();
					debugLog("client: connected succesfully");
					kk = new ConnectedThread(someSocket);
					kk.start();
					startUp2();
				} catch (IOException connectException) {

					try {
						someSocket.close();
						debugLog("connection failed");
					} catch (IOException e) {
					}

					return;
				}

				// Also jeœli program nie zdechnie wczeœniej, to pod someSocket mamy pod³¹czone urz¹dzenie
				// Na razie zadowalamy siê jednym st¹d zostawiamy someSocket :D
				// manageConnectedSocket(someSocket);
			}
		}).start();

	}
/*
	public void sendToAll(String text) {
		// serwer wysy³a wszystkim klientom coœ
		//new ConnectedThread(socket).write(text.getBytes());
		kk.write("test 123".getBytes());
		// @TODO docelowo tutaj pêtla przez wszystkie sockety oznaczaj¹ce pod³¹czonych klientów
	}

	public void sendToServer(String text) {
		// klient wysy³a coœ serwerowi
		new ConnectedThread(someSocket).write(text.getBytes());
	}

	public void closeSocket(BluetoothSocket bSocket) {
		new ConnectedThread(bSocket).cancel();
	}
*/
	
	private void startUp1() {
		/*mActivity.runOnUiThread(new Runnable() {
			public void run() {
				if (mProgressDialog != null)
					mProgressDialog.setMessage("Waiting for other players...");
			}
		});*/
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); 
		YoloEngine.playerParticipantID = mBluetoothAdapter.getName()+" "+mBluetoothAdapter.getAddress();

		YoloEngine.opponents.clear();
		for (YoloPlayer p : YoloEngine.TeamAB)
			p = new YoloPlayer(1000f, 1000f, false, 666);
		if (YoloEngine.participantsBT != null)
			YoloEngine.participantsBT.clear();
		
		
			
	}
	
	//TODO
	String opponentName;
	
	private void startUp2() {
		YoloEngine.MULTI_ACTIVE = true;

		YoloEngine.mMultislayer.sendMessageToAllreliable(YoloEngine.mMultislayer.sendSpriteLoad(new int[] { YoloEngine.currentPlayerInfo.getSK1EQ(), YoloEngine.currentPlayerInfo.getSK2EQ(),
				YoloEngine.currentPlayerInfo.getSK3EQ() }));

		//YoloEngine.participants = mRoom.getParticipants();
		opponentName = kk.devName+" "+kk.devMAC;
	//	YoloEngine.opponentName = opponentName;
		
		YoloEngine.participantsBT.add(opponentName);
		YoloEngine.participantsBT.add(YoloEngine.playerParticipantID);
		
		Collections.sort(YoloEngine.participantsBT);
		
		for (String p : YoloEngine.participantsBT) { // TODO po co to?
			if (!p.equals(YoloEngine.playerParticipantID)) {
				YoloEngine.opponents.add(p);
			}
		}

		if (YoloEngine.playerParticipantID.equals(YoloEngine.participantsBT.get(0))) {					
			// My przydzielamy teamy
			System.out.println("przydzielam team");
			final String teamAssignPattern = assignTeamsXX();
			YoloEngine.TeamAB[YoloEngine.MyID].gameMaster = true;			

			YoloEngine.mMultislayer.sendTeamAssignment(Integer.parseInt(teamAssignPattern, 2));
			new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(800);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					YoloEngine.mMultislayer.sendTeamAssignment(Integer.parseInt(teamAssignPattern, 2));
				}
			}).start();
			YoloEngine.startTime = (System.currentTimeMillis() + YoloEngine.countdownTime + YoloEngine.timeOffset);
			YoloEngine.mMultislayer.sendMaxLife(); // TODO to powinno byæ póŸniej, ¿eby by³a pwenoœæ, ¿e TeamAB jest dobrze usuzp³enione
	}
		

}

	public void koniec() {
		try {
			kk.cancel();
		} catch (Exception e) {
			
		}
		
		
	
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
				//String str = new String(buffer, 0, bytes, "UTF-8");
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
	private ReliableMessageLock(){
		
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
	