package com.example.braille_sync.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.braille_sync.Classes.History;
import com.example.braille_sync.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
//    private final HistoryRecyclerviewInterface historyRecyclerviewInterface;

    Context context;
    ArrayList<History> HistoryList;
    int lastposition = -1;
//    , HistoryRecyclerviewInterface historyRecyclerviewInterface

    public HistoryAdapter(Context context, ArrayList<History> HistoryList) {
        this.context = context;
        this.HistoryList = HistoryList;
//        this.historyRecyclerviewInterface = historyRecyclerviewInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_of_history, parent, false);
        return new ViewHolder(view );
//        , historyRecyclerviewInterface
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        History history = HistoryList.get(position);
        holder.text.setText(history.getText());

        if (history.getTime() != null) {
            long timestampInMillis = history.getTime().toDate().getTime();
            long currentTimeInMillis = System.currentTimeMillis();
            long timeDifference = currentTimeInMillis - timestampInMillis;

            holder.time.setText(humanizeTime(timeDifference, timestampInMillis));
        } else {

            holder.time.setText("Time unavailable");
        }
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return HistoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView text,time;

        public ViewHolder(View itemView) {
//            , HistoryRecyclerviewInterface historyRecyclerviewInterface
            super(itemView);
            time = itemView.findViewById(R.id.time);
            text = itemView.findViewById(R.id.text);

//            itemView.setOnClickListener(v -> {
//
//                if (historyRecyclerviewInterface != null) {
//                    int position = getBindingAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION){
//                        historyRecyclerviewInterface.toHome(position);
//                    }
//
//                }
//            });
        }
    }

    public String humanizeTime(long timeDifference , long timestampInMillis) {
        // Humanize the timestamp

        String humanizedTime;
        if (timeDifference < 60000) { // Less than a minute
            humanizedTime = "Just now";
        } else if (timeDifference < 3600000) { // Less than an hour
            int minutes = (int) (timeDifference / 60000);
            humanizedTime = minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
        } else if (timeDifference < 86400000) { // Less than a day
            int hours = (int) (timeDifference / 3600000);
            humanizedTime = hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else if (timeDifference < 604800000) { // Less than a week
            int days = (int) (timeDifference / 86400000);
            humanizedTime = days + " day" + (days > 1 ? "s" : "") + " ago";
        } else { // More than a week
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            humanizedTime = dateFormat.format(new Date(timestampInMillis));
        }
        return humanizedTime;
    }

    public void setAnimation(View viewToAnimate, int position)
    {
        if (position > lastposition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.history_slide_animation);
            viewToAnimate.startAnimation(animation);
            lastposition = position;
        }
    }

}



