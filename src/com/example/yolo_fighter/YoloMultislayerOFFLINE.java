package com.example.yolo_fighter;

import android.app.Activity;
import android.content.Intent;

public class YoloMultislayerOFFLINE extends YoloMultislayerBase {

	public YoloMultislayerOFFLINE(Activity ac) {
		// TODO Auto-generated constructor stub
	}

	public YoloMultislayerOFFLINE() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void sendMessageToAllreliable(byte[] data) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void sendMessageToAll(byte[] data) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createGame(GameProperties gp) {
		for (YoloStartListener l : listeners) {
			l.gameReadyToStart(mActivity.findViewById(android.R.id.content));
		}
	}

	@Override
	protected void manuallyAssignTeams() {
		// TODO Auto-generated method stub
		
	}

	@Override
	void incomingAction(int request, int response, Intent data) {
		// TODO Auto-generated method stub
		
	}



}
