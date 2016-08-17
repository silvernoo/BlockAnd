package com.github.blockand;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by saikou on 2016/8/17 0017.
 * Email uedeck@gmail.com .
 */
public class LoggerActivity extends AppCompatActivity {

    public static final String LOGGER_FILE = "log";
    private TextView mTextView;
    private BufferedReader mBufferedReader;
    private Timer timer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logger);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTextView = (TextView) findViewById(R.id.logger);
        File logFile = new File(getFilesDir(), LOGGER_FILE);
        if (!logFile.exists())
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        try {
            final InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(logFile));
            mBufferedReader = new BufferedReader(inputStreamReader);
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String msg = null;
                            do {
                                try {
                                    msg = mBufferedReader.readLine();
                                    if (!TextUtils.isEmpty(msg)) {
                                        mTextView.append(msg + "\n");
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } while (!TextUtils.isEmpty(msg));
                        }
                    });
                }
            }, 1000, 1000);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBufferedReader != null)
            try {
                mBufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (timer != null) timer.cancel();
    }
}
