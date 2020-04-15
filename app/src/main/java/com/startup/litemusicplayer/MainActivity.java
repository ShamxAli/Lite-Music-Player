package com.startup.litemusicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.strictmode.ServiceConnectionLeakedViolation;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.startup.litemusicplayer.model.ModelMusic;
import com.startup.litemusicplayer.fragment.SongsFragment;
import com.startup.litemusicplayer.adapter.ViewPageAdapter;
import com.startup.litemusicplayer.fragment.FavoriteFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyTagOne";
    ViewPager viewPager;
    TabLayout tabLayout;
    LinearLayout bottomPhase;
    int position;
    List<ModelMusic> arraylist;
    TextView bottomSongName;
    ImageView imgPlayPause;
    ProgressBar progressBar;
    MyBoundnStartedService myBoundnStartedService;
    private boolean isBound;
    int currentPostion, totalDuration;


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String name = intent.getStringExtra("songStartCheck");
            assert name != null;
            if (name.equals("start")) {
                showProgrerssBar();
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();


        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());

        viewPageAdapter.addFragment(new SongsFragment(), "Songs");
        viewPageAdapter.addFragment(new FavoriteFragment(), "Favorites");


        viewPager.setAdapter(viewPageAdapter);
        tabLayout.setupWithViewPager(viewPager);


        isAllowed();

    }


    /*Getting and passing position and arraylist from Fragment ====================================== */
    public void setNameAndPosition(List<ModelMusic> arraylist, int position) {

        /*Started Service*/
        Intent intent = new Intent(MainActivity.this, MyBoundnStartedService.class);
        startService(intent);

        this.arraylist = arraylist;
        this.position = position;
        myBoundnStartedService.setData(arraylist, position);
        // Setting out bottom layout...

        bottomSongName.setText(arraylist.get(position).getaName());
        imgPlayPause.setImageResource(R.drawable.ic_pause_black_24dp);
        myBoundnStartedService.playSong(position);
    }


    /*onStart ================================================================================== */
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(MainActivity.this, MyBoundnStartedService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);


        /*Register Broadcast*/

        IntentFilter intentFilter = new IntentFilter("showProgress");
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver, intentFilter);


    }

    /*onStop ================================================================================== */
    @Override
    protected void onStop() {
        super.onStop();


        if (isBound) {
            unbindService(serviceConnection);
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
    }

    /*Service Connection ================================================================================== */
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            isBound = true;
            Log.d(TAG, "onServiceConnected: Connection Established ");
            MyBoundnStartedService.MyServiceClass myServiceClass = (MyBoundnStartedService.MyServiceClass) iBinder;
            myBoundnStartedService = myServiceClass.getReference();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };


    /*PlayPause Song ===================================================================== */
    public void playPauseOnClick(View view) {

        if (isBound) {
            if (myBoundnStartedService.isPlaying()) {
                myBoundnStartedService.pause();
                imgPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            } else {
                imgPlayPause.setImageResource(R.drawable.ic_pause_black_24dp);
                myBoundnStartedService.play();
            }
        }

    }


    /*Next Song Click ========================================================*/
    public void nextSongClick(View view) {
        Log.d(TAG, "nextSongClick: position" + position);
        position++;
        if (position == arraylist.size()) {
            position = 0;
        }

        myBoundnStartedService.nextSongClick(position);
        bottomSongName.setText(arraylist.get(position).getaName());


    }

    /*Previous Song Click =======================================================*/
    public void prevSongClick(View view) {
        Log.d(TAG, "prevSongClick: position" + position);


        if (position == 0) {
            position = arraylist.size();
        }
        position--;


        myBoundnStartedService.prevSongClick(position);
        bottomSongName.setText(arraylist.get(position).getaName());
    }


    /*Progress Bar ==================================================================*/
    public void showProgrerssBar() {
        totalDuration = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    totalDuration = myBoundnStartedService.getTotalDuration();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                progressBar.setMax(totalDuration);


                while (currentPostion < totalDuration) {

//                    if (!myBoundnStartedService.isPlaying()) {
//                        break;
//                    } else {
//                        progressBar.setProgress(currentPostion);
//                    }

                    if (myBoundnStartedService.isPlaying()) {
                        try {
                            Thread.sleep(1000);

                            currentPostion = myBoundnStartedService.getCurrentMusicPosition();

                            progressBar.setProgress(currentPostion);


                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (currentPostion == totalDuration) {
                            imgPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                            myBoundnStartedService.setCurrentMusicPosition(0);
                            myBoundnStartedService.setTotalDuration(0);
                            progressBar.setProgress(0);
                            break;
                        }

                    }


                }


            }
        }).start();
    }

//    /*Bottom Phase Handling ========================================================*/
//    public void setBottomPhaseName() {
//        bottomPhase.setVisibility(View.VISIBLE);
//        bottomSongName.setText(name);
//        imgPlayPause.setImageResource(R.drawable.ic_pause_black_24dp);
//    }


    /*Permissions Code = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = */
    private void isAllowed() {

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            askForPermission();

        }
    }

    private void askForPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

    }

    /*Permissions Results ------------------------------------ */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }

    }


    /*Views Initialization ============================================================================*/
    public void initViews() {
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tablayout);
        bottomPhase = findViewById(R.id.bottom_phase);
        bottomSongName = findViewById(R.id.bottom_songName);
        imgPlayPause = findViewById(R.id.play_pause);
        progressBar = findViewById(R.id.progressBar);
    }


}
