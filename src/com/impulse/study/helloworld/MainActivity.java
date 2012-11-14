package com.impulse.study.helloworld;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// ÃÓ≥‰±ÍÃ‚¿∏
		setContentView(R.layout.activity_main);
		Button nextPage = (Button) findViewById(R.id.button1);
		nextPage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent showNextPage_Intent = new Intent();
				showNextPage_Intent.setClass(MainActivity.this,
						NextpageActivity.class);
				startActivity(showNextPage_Intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
