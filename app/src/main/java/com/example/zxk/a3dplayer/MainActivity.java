package com.example.zxk.a3dplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    Button start, stop;
    ControlThread mplayThread;
    int maxValue = 80;
    int sleepTime = 140;
    int minValue = 4;
    boolean playStatus = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.exit);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mplayThread = new ControlThread(MainActivity.this, "4.pcm");
                playStatus = true;
                mplayThread.start();
                roomThread.start();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    Thread roomThread = new Thread() {
        @Override
        public void run() {
            while (playStatus) {
                for (int i = maxValue - minValue; i >= minValue; i--) {
                    try {
                        if (i >= minValue && i <= 15){
                            sleep(350);
                        }
                        else {
                            sleep(sleepTime);
                        }
                        mplayThread.setBalance(maxValue, i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (i == minValue) {
                        for (int s = minValue + 1; s < maxValue - minValue + 1; s++) {
                            try {
                                if ((i >= maxValue - minValue + 1) && i <= 15){
                                    sleep(350);
                                }
                                else {
                                    sleep(sleepTime);
                                }
                                mplayThread.setBalance(maxValue, s);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        i = maxValue - minValue;
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mplayThread.stoped();
        mplayThread = null;
        playStatus = false;
    }


}
