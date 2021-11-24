package com.example.policeblinker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    View blinkLamp;
    TextView tv;
    int brightness;
    int speed = 50;
    float xOld=0, yOld=0;
    float x, y;
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    blinkLamp = findViewById(R.id.blinkView);
                    blink(lightOn);
                    lightOn = !lightOn;
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        timer.schedule(task, speed, speed);


        tv = findViewById(R.id.tv);
        tv.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                x = event.getX();
                y = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // нажатие
                        tv.setText("Touched!");
                        tv.performClick();

                        break;
                    case MotionEvent.ACTION_MOVE: // движение
                        if (yOld>y) {
                            tv.setText("MoveUp: " + x + "," + y);
                            speed++;
                        }
                        else {
                            tv.setText("MoveDown: " + x + "," + y);
                            speed--;
                            if (speed<10) speed = 10;
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        tv.setText("Released! " + speed);
                        setTimerInterval(speed);
                        break;// отпускание
                    case MotionEvent.ACTION_CANCEL:
                        tv.setText("Up: " + x + "," + y);
                        break;
                }
                xOld = x;
                yOld = y;


                return true;
            }
        });

    }

    boolean lightOn = true;


    int[] colors = new int[]{Color.parseColor("#FF0029FD"), Color.parseColor("#FFFD0000"), Color.parseColor("#000000")};
    int color = 0;
    int colChange = 0;

    public void blink(boolean isOn) {
        if (isOn) {
            blinkLamp.setBackgroundColor(colors[color]);
            colChange++;
            if (colChange > 2) {
                colChange = 0;
                color++;
                if (color > 1) color = 0;
            }
        } else {
            blinkLamp.setBackgroundColor(colors[2]);
        }

    }

    public void setTimerInterval (int milliseconds) {
        timer.cancel ();
        timer = new Timer ();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        blinkLamp = findViewById(R.id.blinkView);
                        blink(lightOn);
                        lightOn = !lightOn;
                    }
                });
            }
        }, milliseconds, milliseconds);
    }


    private void setBrigthness() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(MainActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

        try {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

            brightness = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("Error", "Cannot access system brightness");
            e.printStackTrace();
        }

        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
        WindowManager.LayoutParams layoutpars = getWindow().getAttributes();
        layoutpars.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        getWindow().setAttributes(layoutpars);
    }

}