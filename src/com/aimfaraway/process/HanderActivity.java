package com.aimfaraway.process;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class HanderActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("HanderActivity", "---onCreate");
		Toast.makeText(getApplicationContext(), "start HanderActivity...", Toast.LENGTH_SHORT).show();
		setContentView(R.layout.main);

		startService();
		
		//finish();
	}

	void startService() {
		Log.e("HanderActivity", "---startService");
		Intent intent = new Intent(this, MyService.class);
		startService(intent);
	}

}