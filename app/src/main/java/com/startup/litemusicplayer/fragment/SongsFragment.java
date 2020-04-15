package com.startup.litemusicplayer.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.startup.litemusicplayer.MyBoundnStartedService;
import com.startup.litemusicplayer.R;
import com.startup.litemusicplayer.adapter.RecyViewAdapter;
import com.startup.litemusicplayer.MainActivity;
import com.startup.litemusicplayer.model.ModelMusic;

import java.util.ArrayList;
import java.util.List;


public class SongsFragment extends Fragment implements RecyViewAdapter.OnRecyclerListener {

    private MainActivity mainActivity;

    private List<ModelMusic> arrayList;

    public SongsFragment() {

    }


    /*onCreateView =======================================================*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        /*Init reference of MainActivity*/
        mainActivity = (MainActivity) getActivity();

        getAllSongs();


        /*Bake RecyclerView*/
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new RecyViewAdapter(arrayList, this));

        return view;
    }


    /*Get All Songs = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = */
    private void getAllSongs() {

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST,};


        arrayList = new ArrayList<>();

        Cursor c = getContext().getContentResolver().query(uri,
                projection, null, null, null);

        if (c != null) {
            while (c.moveToNext()) {

                String path = c.getString(0);
                String album = c.getString(1);
                String artist = c.getString(2);

                String name = path.substring(path.lastIndexOf("/") + 1);


                Log.d("Namemodel :" + name, " Album :" + album);
                Log.d("Pathmodel :" + path, " Artist :" + artist);

                arrayList.add(new ModelMusic(path, name, album, artist));
            }
        }
    }


    @Override
    public void onRecyclerClick(int position) {
        mainActivity.setNameAndPosition(arrayList, position);
    }
}



