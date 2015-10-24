package com.example.yolo_fighter;


import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class YoloActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		YoloEngine.display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yolo);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		new Handler().postDelayed(new Thread() 
		{
			@Override
			public void run()
			{	
				Intent mainMenu = new Intent(YoloActivity.this,YoloMainMenu.class);
				
				YoloActivity.this.startActivity(mainMenu);
				YoloActivity.this.finish();
				overridePendingTransition(R.layout.fadein, R.layout.fadeout);
			}
			
		},600);
		
	}
	
}
