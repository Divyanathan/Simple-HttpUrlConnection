package com.example.user.httpurlconnection.service;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ServiceCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.user.httpurlconnection.util.Utilitclass;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by user on 15/05/17.
 */

public class DownloadService extends IntentService {

    private static final String TAG = "DownloadService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public DownloadService() {
        super("Download Service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Log.d(TAG, "onHandleIntent: ");
        downLoadFile();
    }


    void downLoadFile() {
        URL lUrl;
        HttpURLConnection lHttpUrlConnection = null;
        int lDownLoadFile = 0;

        byte lDownloadFileByte[] = new byte[1024];
        try {
            /**
             * url
             */
//            lUrl = new URL("http://www.pngmart.com/files/4/Android-PNG-Pic.png");
            lUrl = new URL("http://cineisai.com/4256s5f46ht4he4r6/Anuradha%20Sriram%20Hits/Ennavale.mp3");
            /**
             * giving connection
             */
            lHttpUrlConnection = (HttpURLConnection) lUrl.openConnection();
            lHttpUrlConnection.connect();
            /**
             * input stream --->it contains the downloaded file in byte format
             */
            InputStream lfile = new BufferedInputStream(lHttpUrlConnection.getInputStream());

            /**
             * setting the path where the output file has to store
             */
            File lRootDirectory = Environment.getExternalStorageDirectory();
            /**
             * set the file name for a downloaded file
             */
            File lOutPutFilePath = new File(lRootDirectory, "android.png");

            /**
             * output stream  where we will write the input stream by each single byte
             * to produce a download file
             */
            OutputStream lOutputStream = new FileOutputStream(lOutPutFilePath);

            /**
             * variables to keep the file length and current each byte
             */
            int lData=0;
            int lFileCurerntPosition=0;
            int lFileLenght= lHttpUrlConnection.getContentLength();
            Log.d(TAG, "downLoadFile: "+lFileLenght+" "+lFileCurerntPosition);
            /**
             * here the input stream will produce the output file
             */
            while ((lData = lfile.read(lDownloadFileByte)) != -1) {

                /**
                 * write the outPut file by each character
                 */
                lOutputStream.write(lDownloadFileByte, 0, lData);

                /**
                 * intent to send the broadcast
                 */
                Intent lUpdateProgressIntent = new Intent(Utilitclass.DOWNLOAD_INTENT);

                lFileCurerntPosition=lFileCurerntPosition+lData;
                lUpdateProgressIntent.putExtra(Utilitclass.PROGRESS_STATE,lFileCurerntPosition);
                lUpdateProgressIntent.putExtra(Utilitclass.FILE_LENGTH,lFileLenght);
                /**
                 * send the broadcast
                 */
                LocalBroadcastManager.getInstance(this).sendBroadcast(lUpdateProgressIntent);
//                Log.d(TAG, "downLoadFile: "+lFileLenght+" "+lFileCurerntPosition);
            }
            Intent lDownladCompleteIntent=new Intent(Utilitclass.DOWNLOAD_INTENT);
            lDownladCompleteIntent.putExtra(Utilitclass.IS_DOWNLOAD_FINISHED,true);
            LocalBroadcastManager.getInstance(this).sendBroadcast(lDownladCompleteIntent);


            lOutputStream.flush();
            lOutputStream.close();
            lfile.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lHttpUrlConnection.disconnect();
        }
    }
}
