package com.example.user.httpurlconnection.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.user.httpurlconnection.service.DownloadService;
import com.example.user.httpurlconnection.R;
import com.example.user.httpurlconnection.util.Utilitclass;

public class MainActivity extends AppCompatActivity {

    private static final int INTERNET_PERMISSION_CODE = 1;
    private static final int EXTERNAL_STORAGE_PERMISSION_CODE = 2;
    private static final String TAG = "MainActivity";

    ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * register  the  reciver
         */

        LocalBroadcastManager.getInstance(this).registerReceiver(mProgressUpdateReciver, new IntentFilter(Utilitclass.DOWNLOAD_INTENT));


        ((ImageView) findViewById(R.id.btn_download)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                ensurPermission();
                ensureExternalStoragePermission();
//
            }
        });
    }


    void ensureExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.INTERNET},
                    EXTERNAL_STORAGE_PERMISSION_CODE);
        } else {
            startService(new Intent(MainActivity.this, DownloadService.class));
//            Toast.makeText(this, "downloading", Toast.LENGTH_SHORT).show();
        }
    }

    void progressUpdation() {

//        mProgressDialog.setProgress();

    }

    void StopProgress() {

    }

    BroadcastReceiver mProgressUpdateReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context pContext, Intent pIntent) {

            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(MainActivity.this);
                mProgressDialog.setTitle("file is DownLoading");
                mProgressDialog.setMax(100);
                mProgressDialog.setProgressStyle(mProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.show();
            }
            Log.d(TAG, "onReceive: ");
            if (pIntent.getBooleanExtra(Utilitclass.IS_DOWNLOAD_FINISHED, false)) {
                mProgressDialog.dismiss();
                mProgressDialog=null;
                Toast.makeText(pContext, "download completed", Toast.LENGTH_SHORT).show();
            } else {
//                Log.d(TAG, "onReceive: update");
                int lFileLenght = pIntent.getIntExtra(Utilitclass.FILE_LENGTH, -1);
                int lFilePosition = pIntent.getIntExtra(Utilitclass.PROGRESS_STATE, -1);
                mProgressDialog.setProgress((lFilePosition * 100) / lFileLenght);


            }

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: ");
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CODE) {
            Log.d(TAG, "onRequestPermissionsResult: if");
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: grandted");
                startService(new Intent(MainActivity.this, DownloadService.class));
                Toast.makeText(this, "downloading", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
