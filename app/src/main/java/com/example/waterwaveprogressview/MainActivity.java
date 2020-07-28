package com.example.waterwaveprogressview;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WaveProgressView waveProgressView = findViewById(R.id.waveview);
        waveProgressView.setProgressAnim(100, 2000);

    }
}