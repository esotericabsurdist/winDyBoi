package com.spaceshipfreehold.windyboi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Winch mWinch;
    Button mWinchOutButton;
    Button mConnectToDeviceButton;
    Button mWinchInButton;

    int mControlButtonDepressedColor = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mControlButtonDepressedColor = ContextCompat.getColor(getApplicationContext(), R.color.darkLavender);

        mWinchOutButton = findViewById(R.id.winch_out_button);
        mConnectToDeviceButton = findViewById(R.id.winch_connect_button);
        mWinchInButton = findViewById(R.id.winch_in_button);

        mWinchOutButton.setOnTouchListener(new WinchOutTouchListener());
        mConnectToDeviceButton.setOnLongClickListener(new ConnectClickListener());
        mWinchInButton.setOnTouchListener(new WinchInTouchListener());

        mWinch = Winch.getInstance(getApplicationContext());
        mWinch.setListener(new WinchConnectionListener());
        mWinch.start();
    }

    private void showToast(final String message, final int length){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, length).show();
            }
        });
    }

    private class WinchOutTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    mWinch.out();
                    mWinchOutButton.setBackgroundColor(mControlButtonDepressedColor);
                    return true;

                case MotionEvent.ACTION_UP:
                    mWinch.brake();
                    mWinchOutButton.setBackgroundColor(Color.BLACK);
                    return false;
            }

            return false;
        }
    }

    private class ConnectClickListener implements View.OnLongClickListener{
        @Override
        public boolean onLongClick(View v) {
             if(mWinch == null){
                 mWinch = Winch.getInstance(getApplicationContext());
                 mWinch.setListener(new WinchConnectionListener());
                 mWinch.start();
             }

             mConnectToDeviceButton.setBackgroundColor(mControlButtonDepressedColor);

             // Dew it, dew it
             mWinch.connect();

             return true;
        }
    }

    private class WinchInTouchListener implements View.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mWinch.in();
                    mWinchInButton.setBackgroundColor(mControlButtonDepressedColor);
                    return true;

                case MotionEvent.ACTION_UP:
                    mWinch.brake();
                    mWinchInButton.setBackgroundColor(Color.BLACK);
                    return true;
            }
            return false;
        }
    }

    private class WinchConnectionListener implements IWinchConnectionListener{
        @Override
        public void onConnected() {
            showToast("Connected!", Toast.LENGTH_SHORT);
            mConnectToDeviceButton.setText("Connected");
            mConnectToDeviceButton.setBackgroundColor(Color.BLUE);
        }

        @Override
        public void onDisconnected() {
            showToast("Disconnected!", Toast.LENGTH_SHORT);
            mConnectToDeviceButton.setBackgroundColor(mControlButtonDepressedColor);
            mConnectToDeviceButton.setText("Connect");
        }

        @Override
        public void onSocketConnectionFailed() {
            showToast("Connection Failed", Toast.LENGTH_SHORT);
            mConnectToDeviceButton.setBackgroundColor(mControlButtonDepressedColor);
            mConnectToDeviceButton.setText("Connect");
        }

        @Override
        public void onNoBluetoothAdapterFound() {
            showToast("Does this device support bluetooth?", Toast.LENGTH_LONG);
            mConnectToDeviceButton.setBackgroundColor(mControlButtonDepressedColor);
            mConnectToDeviceButton.setText("Connect");
        }

        @Override
        public void onBluetoothNotEnabled() {
            showToast("Enable Bluetooth!", Toast.LENGTH_SHORT);
            mConnectToDeviceButton.setBackgroundColor(mControlButtonDepressedColor);
            mConnectToDeviceButton.setText("Connect");
        }

        @Override
        public void onWinchNotFound() {
            showToast("Winch is not found", Toast.LENGTH_LONG);
            mConnectToDeviceButton.setBackgroundColor(mControlButtonDepressedColor);
            mConnectToDeviceButton.setText("Connect");
        }
    }
}
