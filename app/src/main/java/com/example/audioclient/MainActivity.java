package com.example.audioclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.clipservercommon.MusicPlayerInterface;

public class MainActivity extends AppCompatActivity {
    private MusicPlayerInterface musicPlayerInterface;
    private boolean clientIsBound = false;

    boolean startServiceButtonStatus;
    boolean stopServiceButtonStatus;
    boolean playButtonStatus;
    boolean pauseButtonStatus;
    boolean resumeButtonStatus;
    boolean stopButtonStatus;
    boolean serviceIsStarted = false;
    Intent musicServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startServiceButton = findViewById(R.id.start_service);
        Button stopServiceButton = findViewById(R.id.stop_service);
        Button playButton = findViewById(R.id.play);
        Button resumeButton = findViewById(R.id.resume);
        Button pauseButton = findViewById(R.id.pause);
        Button stopButton = findViewById(R.id.stop);
        EditText clipNumberField = findViewById(R.id.editTextNumber);

            stopServiceButton.setEnabled(false);
            playButton.setEnabled(false);
            resumeButton.setEnabled(false);
            pauseButton.setEnabled(false);
            stopButton.setEnabled(false);


        clipNumberField.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == EditorInfo.IME_ACTION_DONE){
                String clipNumber = clipNumberField.getText().toString();
                if(Integer.parseInt(clipNumber)<1 && Integer.parseInt(clipNumber)>5){
                    Toast.makeText(MainActivity.this, "Error! Please enter a number between 1-5", Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        });
//        Intent musicServiceIntent = new Intent(MusicPlayerInterface.class.getName());
//        ResolveInfo resolveInfo = getPackageManager().resolveService(musicServiceIntent, PackageManager.MATCH_ALL);
//        musicServiceIntent.setComponent(new ComponentName(resolveInfo.serviceInfo.packageName,resolveInfo.serviceInfo.name));

        musicServiceIntent = new Intent();
        musicServiceIntent.setComponent(new ComponentName("com.example.clipserver","com.example.clipserver.ClipServerService"));

        startServiceButton.setOnClickListener(view -> {
            serviceIsStarted =true;
            if(serviceIsStarted)
           startForegroundService(musicServiceIntent);

            stopServiceButtonStatus = true;
            playButtonStatus = true;
            startServiceButtonStatus = false;
            resumeButtonStatus = false;
            pauseButtonStatus = false;
            stopButtonStatus = false;

            stopServiceButton.setEnabled(stopServiceButtonStatus);
            playButton.setEnabled(playButtonStatus);
            startServiceButton.setEnabled(startServiceButtonStatus);
            resumeButton.setEnabled(resumeButtonStatus);
            pauseButton.setEnabled(pauseButtonStatus);
            stopButton.setEnabled(stopButtonStatus);
        });

        stopServiceButton.setOnClickListener(view -> {
            serviceIsStarted =false;
            if(!serviceIsStarted)
            stopService(musicServiceIntent);
//            getApplicationContext().stopService(musicServiceIntent);
//            try {
////                musicPlayerInterface.stopClipService();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }

            stopServiceButtonStatus = false;
            playButtonStatus = false;
            startServiceButtonStatus = true;
            resumeButtonStatus = false;
            pauseButtonStatus = false;
            stopButtonStatus = false;

            playButton.setEnabled(playButtonStatus);
            resumeButton.setEnabled(resumeButtonStatus);
            pauseButton.setEnabled(pauseButtonStatus);
            stopButton.setEnabled(stopButtonStatus);
            stopServiceButton.setEnabled(stopServiceButtonStatus);
            startServiceButton.setEnabled(startServiceButtonStatus);
        });

        playButton.setOnClickListener(view -> {
            try {
                doBindService();
                if(clientIsBound){
                    if(Integer.parseInt(clipNumberField.getText().toString())>=1 && Integer.parseInt(clipNumberField.getText().toString())<=5){
                       Log.i("ClientPlay",""+clipNumberField.getText().toString());
                        musicPlayerInterface.play(Integer.parseInt(clipNumberField.getText().toString())-1);

                        stopServiceButtonStatus = true;
                        playButtonStatus = false;
                        startServiceButtonStatus = false;
                        resumeButtonStatus = false;
                        pauseButtonStatus = true;
                        stopButtonStatus = true;
                        }
                    else{
                        Toast.makeText(MainActivity.this, "Error! Please enter a number between 1-5", Toast.LENGTH_SHORT).show();

                        stopServiceButtonStatus = true;
                        playButtonStatus = true;
                        startServiceButtonStatus = false;
                        resumeButtonStatus = false;
                        pauseButtonStatus = false;
                        stopButtonStatus = false;
                    }
                    pauseButton.setEnabled(pauseButtonStatus);
                    stopButton.setEnabled(stopButtonStatus);
                    startServiceButton.setEnabled(startServiceButtonStatus);
                    resumeButton.setEnabled(resumeButtonStatus);
                    playButton.setEnabled(playButtonStatus);
                    stopServiceButton.setEnabled(stopServiceButtonStatus);
                }
                else{
                    Log.i("Client","Client not bound");
                }
            }catch (Exception e){
                    Log.i("Client",e.toString());
            }
        });

        resumeButton.setOnClickListener(view -> {
            try {
                if(clientIsBound){
                    musicPlayerInterface.resume();
                }
                else{
                    Log.i("Client","Client not bound");
                }
            }catch (RemoteException e){
                Log.i("Client",e.toString());
            }
            stopServiceButtonStatus = true;
            playButtonStatus = false;
            startServiceButtonStatus = false;
            resumeButtonStatus = false;
            pauseButtonStatus = true;
            stopButtonStatus = true;

            resumeButton.setEnabled(resumeButtonStatus);
            stopServiceButton.setEnabled(stopServiceButtonStatus);
            startServiceButton.setEnabled(startServiceButtonStatus);
            playButton.setEnabled(playButtonStatus);
            stopButton.setEnabled(stopButtonStatus);
            pauseButton.setEnabled(pauseButtonStatus);
        });

        pauseButton.setOnClickListener(view -> {
            try {
                if(clientIsBound){
                    musicPlayerInterface.pause();
                }
                else{
                    Log.i("Client","Client not bound");
                }
            }catch (RemoteException e){
                Log.i("Client",e.toString());
            }
            stopServiceButtonStatus = true;
            playButtonStatus = false;
            startServiceButtonStatus = false;
            resumeButtonStatus = true;
            pauseButtonStatus = false;
            stopButtonStatus = true;

            resumeButton.setEnabled(resumeButtonStatus);
            pauseButton.setEnabled(pauseButtonStatus);
            stopButton.setEnabled(stopButtonStatus);
            playButton.setEnabled(playButtonStatus);
            startServiceButton.setEnabled(startServiceButtonStatus);
            stopServiceButton.setEnabled(stopServiceButtonStatus);
        });

        stopButton.setOnClickListener(view -> {
            try {
                if(clientIsBound){
                    musicPlayerInterface.stop();
//                    /getApplicationContext().unbindService(this.serviceConnection);
                    doUnBindService();
                    Toast.makeText(this, "Playback is stopped!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.i("Client","Client not bound");
                }
            }catch (RemoteException e){
                Log.i("Client",e.toString());
            }

            stopServiceButtonStatus = true;
            playButtonStatus = true;
            startServiceButtonStatus = false;
            resumeButtonStatus = false;
            pauseButtonStatus = false;
            stopButtonStatus = false;

            pauseButton.setEnabled(pauseButtonStatus);
            resumeButton.setEnabled(resumeButtonStatus);
            stopButton.setEnabled(stopButtonStatus);
            playButton.setEnabled(playButtonStatus);
            startServiceButton.setEnabled(startServiceButtonStatus);
            stopServiceButton.setEnabled(stopServiceButtonStatus);
        });
    }

//    void doStartService(){
//
//        if(serviceIsStarted){
//            getApplicationContext().startForegroundService(musicServiceIntent);
//
//        }
//    }
//
//    void doStopService(){
//        if(!serviceIsStarted){
//            getApplicationContext().stopService(musicServiceIntent);
//        }
//    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(
                this,"android.permission.POST_NOTIFICATIONS")
                == PackageManager.PERMISSION_DENIED)
        {
            requestPermissions(new String[] {"android.permission.POST_NOTIFICATIONS"}, 0) ;
        }
        doBindService();
    }

    protected void checkBindingAndBind(){


            Intent i = new Intent(MusicPlayerInterface.class.getName());
            ResolveInfo resolveInfo = getPackageManager().resolveService(i, PackageManager.MATCH_ALL);
            i.setComponent(new ComponentName(resolveInfo.serviceInfo.packageName,resolveInfo.serviceInfo.name));

            clientIsBound = getApplicationContext().bindService(i, this.serviceConnection, Context.BIND_AUTO_CREATE);

    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            musicPlayerInterface = MusicPlayerInterface.Stub.asInterface(iBinder);
//            clientIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicPlayerInterface = null;
//            clientIsBound = false;
        }
    };


    void doBindService(){
        checkBindingAndBind();
    }

    void doUnBindService(){
        if(clientIsBound){
            getApplicationContext().unbindService(this.serviceConnection);
            clientIsBound = false;
        }
    }

    public void onRequestPermissionsResult(int code, @NonNull String[] permissions, @NonNull int[] result) {
        super.onRequestPermissionsResult(code, permissions, result) ;
        if (result.length >0) {
            if (result[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "App will not show notifications", Toast.LENGTH_SHORT).show() ;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(clientIsBound){
            getApplicationContext().unbindService(this.serviceConnection);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(clientIsBound){
            getApplicationContext().unbindService(this.serviceConnection);
        }
    }
}