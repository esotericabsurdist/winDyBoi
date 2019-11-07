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
            // TODO: How to get push button behavior?
            if(event.isButtonPressed(v.getId())){
                mWinch.out();
                mWinchOutButton.setBackgroundColor(Color.DKGRAY);
            } else {
                mWinch.brake();
                mWinchOutButton.setBackgroundColor(Color.BLACK);
            }

//            run = !run;
//            if(run) {
//                mWinch.out();
//                mWinchOutButton.setBackgroundColor(Color.DKGRAY);
//            } else {
//                mWinch.mOut = false;
//                mWinch.mIn = false;
//
//            }
            return true;
        }
    }

    private class ConnectClickListener implements View.OnLongClickListener{
        @Override
        public boolean onLongClick(View v) {
             if(mWinch == null){
                 mWinch = new Winch(getApplicationContext());
                 mWinch.start();
             }
             if(mWinch.connect()){
                 mConnectToDeviceButton.setText("Connected");
             } else {
                 mConnectToDeviceButton.setText("Unconnected");
             }
             return true;
        }
    }

    private class WinchInTouchListener implements View.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if(event.isButtonPressed(v.getId())){
                mWinch.in();
                mWinchOutButton.setBackgroundColor(Color.DKGRAY);
            } else {
                mWinch.brake();
                mWinchOutButton.setBackgroundColor(Color.BLACK);
            }

//            if(mWinch != null) {
//                run = !run;
//                if (run) {
//                    mWinch.in();
//                    mWinchInButton.setBackgroundColor(Color.DKGRAY);
//                } else {
//                    mWinch.brake();
//                    mWinchInButton.setBackgroundColor(Color.BLACK);
//                }
//            }
            return true;
        }
    }
}
