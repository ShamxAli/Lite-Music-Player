package com.startup.litemusicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.appbar.AppBarLayout;
import com.startup.litemusicplayer.model.ModelMusic;

import java.util.List;

public class MyBoundnStartedService extends Service {

    int position;
    List<ModelMusic> arrayList;
    ModelMusic modelMusic;
    String path;
    private static final String TAG = "MyTagOne";
    Uri uri;
    private MediaPlayer mediaPlayer;
    Binder binder = new MyServiceClass();

    int currentMusicPosition;
    int totalDuration;


    /*Get and Set current and total duration ====================================== */

    public int getCurrentMusicPosition() {
        try {
            currentMusicPosition = mediaPlayer.getCurrentPosition() / 60;
        } catch (NullPointerException | IllegalStateException e) {
            e.printStackTrace();
        }
        return currentMusicPosition;
    }

    public int getTotalDuration() {

        try {
            totalDuration = mediaPlayer.getDuration() / 60;
        } catch (NullPointerException | IllegalStateException e) {
            e.printStackTrace();
        }
        return totalDuration;
    }

    public void setCurrentMusicPosition(int currentMusicPosition) {
        this.currentMusicPosition = currentMusicPosition;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    public MyBoundnStartedService() {
    }

    public class MyServiceClass extends Binder {

        public MyBoundnStartedService getReference() {
            return MyBoundnStartedService.this;
        }
    }



    /*----------------------------------------------------------------------------------------------------------*/


    /*setData ---------------------------------- */
    public void setData(List<ModelMusic> arrayList, int position) {
        this.arrayList = arrayList;
        this.position = position;
    }


    /*playSong ---------------------------------- */
    public void playSong(int position) {

        setCurrentMusicPosition(0);

        modelMusic = arrayList.get(position);
        path = modelMusic.getaPath();
        uri = Uri.parse(path);


        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();


        Intent intent = new Intent("showProgress");
        intent.putExtra("songStartCheck", "start");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

    }


    /*Next Song ---------------------------------- */
    public void nextSongClick(int position) {
        setCurrentMusicPosition(0);
        this.position = position;
        if (position == arrayList.size()) {
            this.position = 0;
        }
        playSong(position);
    }

    /*Previous Song ---------------------------------- */
    public void prevSongClick(int position) {
        setCurrentMusicPosition(0);
        this.position = position;
        Toast.makeText(this, "pos is " + position, Toast.LENGTH_SHORT).show();
        if (position == 0) {
            this.position = arrayList.size();
        }
        playSong(position);

    }

    /*simple play ---  */
    public void play() {
        mediaPlayer.start();
    }

    /*simple pause ---  */
    public void pause() {
        mediaPlayer.pause();
    }


    /*is Playing method --------------------------------  */
    public boolean isPlaying() {
        boolean isPlay = false;
        try {
            isPlay = mediaPlayer.isPlaying();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return isPlay;
    }


    /*on Start Command --------------------------------------*/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    /*IBinder ---------------------------------------------- */

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: called");
        return binder;
    }
}
