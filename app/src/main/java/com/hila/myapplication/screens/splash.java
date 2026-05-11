package com.hila.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.hila.myapplication.R;

public class splash extends AppCompatActivity {

        ImageView iv;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_splash);


            iv = findViewById(R.id.imageView);
            Thread mSplashThread = new Thread() {
                @Override
                public void run() {
                    try {
                        synchronized (this) {


                            Animation myAnim = AnimationUtils.loadAnimation(splash.this, R.anim.tween);
                            iv.startAnimation(myAnim);
                            wait(1500);
                        }

                    } catch (InterruptedException ex) {
                    }
                    finish();

                    Intent intent = new Intent(splash.this, MainActivity.class);
                    startActivity(intent);
                }
            };
            mSplashThread.start();
        }
    }
