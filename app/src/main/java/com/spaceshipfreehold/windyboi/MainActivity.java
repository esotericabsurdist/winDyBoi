package com.spaceshipfreehold.windyboi;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Winch mWinch;
    Button mWinchOutButton;
    Button mConnectToDeviceButton;
    Button mWinchInButton;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWinchOutButton = findViewById(R.id.winch_out_button);
        mConnectToDeviceButton = findViewById(R.id.winch_connect_button);
        mWinchInButton = findViewById(R.id.winch_in_button);

        mWinchOutButton.setOnTouchListener(new WinchOutTouchListener());
        mConnectToDeviceButton.setOnLongClickListener(new ConnectClickListener());
        mWinchInButton.setOnTouchListener(new WinchInTouchListener());

        mWinch = new Winch(getApplicationContext());
        mWinch.start();
    }

    private boolean run = false;

    private class WinchOutTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            run = !run;
            if(run) {
                mWinch.out();
                mWinchOutButton.setBackgroundColor(Color.DKGRAY);
            } else {
                mWinch.out = false;
                mWinch.in = false;
                mWinchOutButton.setBackgroundColor(Color.BLACK);
            }
            return false;
        }
    }

    private class ConnectClickListener implements View.OnLongClickListener{
        @Override
        public boolean onLongClick(View v) {
             if(mWinch == null){
                 mWinch = new Winch(getApplicationContext());
                 mWinch.start();
             }
             mWinch.connect();
             return true;
        }
    }

    private class WinchInTouchListener implements View.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(mWinch != null) {
                run = !run;
                if (run) {
                    mWinch.in();
                    mWinchInButton.setBackgroundColor(Color.DKGRAY);
                } else {
                    mWinch.brake();
                    mWinchInButton.setBackgroundColor(Color.BLACK);
                }
            }
            return false;
        }
    }
}
