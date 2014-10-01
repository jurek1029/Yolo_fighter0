package com.example.yolo_fighter;

public class YoloMultislayer {

	public float Opponents_x_last[] = new float[4];
	public float Opponents_y_last[] = new float[4];

	public float Opponents_x_change[] = new float[4];
	public float Opponents_y_change[] = new float[4];

	private Boolean newPackage;
	private long sentAt;
	private long receivedAt;

	public void SendData(float x, float y) {
		if (System.currentTimeMillis() - sentAt >= 100) {
			// System.out.println("x: "+x+" y: "+y);
			sentAt = System.currentTimeMillis();

		}
	}

	private void updateData(int playerID, float x, float y) {
		Opponents_x_change[playerID] = ((x-Opponents_x_last[playerID])/(float)5); // de facto trzerba sprawdzi� ile razy odpalany jest DrawOpponnent i jako� to powi�za�
		Opponents_y_change[playerID] = ((y - Opponents_y_last[playerID]) / (float) 5);

		Opponents_x_last[playerID] = x;
		Opponents_y_last[playerID] = y;

		receivedAt = System.currentTimeMillis();
		newPackage = false;
		YoloEngine.changesMade = 0;
	}

	public void DataReceived(final int playerID, final float x, final float y) {
		newPackage = true;
		if (System.currentTimeMillis() - receivedAt >= 100) {
			updateData(playerID, x, y);
		} else { // nie lubimy zbyt cz�stych update�w
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					while (true) {
						if (newPackage) {
							System.out.println("breaking!"
									+ System.currentTimeMillis());
							newPackage = false;
							break;
						}

						if (System.currentTimeMillis() - receivedAt >= 100) {
							updateData(playerID, x, y);
							break;
						}
						try {
							System.out.println("halt in thread");
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
	}
}
