package com.startup.litemusicplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.startup.litemusicplayer.R;
import com.startup.litemusicplayer.model.ModelMusic;

import java.util.List;

public class RecyViewAdapter extends RecyclerView.Adapter<RecyViewAdapter.MyViewHolder> {

    private OnRecyclerListener onRecyclerListener;
    private List<ModelMusic> arrayList;



    /*Constructor ================================*/

    public RecyViewAdapter(List<ModelMusic> arrayList, OnRecyclerListener onRecyclerListener) {
        this.arrayList = arrayList;
        this.onRecyclerListener = onRecyclerListener;
    }


    /*onCreateViewHolder ================================*/
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.songs_ui_rec, parent, false);
        return new MyViewHolder(view, onRecyclerListener);
    }


    /*onBindViewHolder ================================*/
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelMusic modelMusic = arrayList.get(position);
        holder.textView.setText(modelMusic.getaName());


    }


    /*getItemCount ================================*/
    @Override
    public int getItemCount() {
        return arrayList.size();

    }


    /*MyViewHolderClass= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =*/
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView textView;
        OnRecyclerListener onRecyclerListener;

        MyViewHolder(@NonNull View itemView, OnRecyclerListener onRecyclerListener) {
            super(itemView);
            textView = itemView.findViewById(R.id.songName);
            this.onRecyclerListener = onRecyclerListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onRecyclerListener.onRecyclerClick(getAdapterPosition());
        }
    }


    /*onClick Interface*/
    public interface OnRecyclerListener {
        void onRecyclerClick(int position);
    }


}
