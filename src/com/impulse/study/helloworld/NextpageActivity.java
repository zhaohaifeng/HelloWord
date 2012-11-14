package com.impulse.study.helloworld;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class NextpageActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nextpage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_nextpage, menu);
        return true;
    }
}
