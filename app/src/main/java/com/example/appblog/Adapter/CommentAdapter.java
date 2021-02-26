package com.example.appblog.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appblog.Model.Comment;
import com.example.appblog.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context mContext;
    private List<Comment> mData;

    public CommentAdapter(Context mContext, List<Comment> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    public CommentAdapter() {
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v= LayoutInflater.from(mContext).inflate(R.layout.row_comment,parent,false);
       return new CommentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.txtName.setText(mData.get(position).getUname());
        holder.txtTalk.setText(mData.get(position).getContent());
        Glide.with(mContext).load(mData.get(position).getUimg()).into(holder.ivUser);
        holder.txtTime.setText((timeToString((long)mData.get(position).getTimestamp())));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUser;
        TextView txtName,txtTalk,txtTime;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUser=(ImageView)itemView.findViewById(R.id.comment_user);
            txtName=(TextView)itemView.findViewById(R.id.comment_name);
            txtTalk=(TextView)itemView.findViewById(R.id.comment_talk);
            txtTime=(TextView)itemView.findViewById(R.id.comment_time);
        }
    }
    private String timeToString(long time){
        Calendar calendar=Calendar.getInstance(Locale.CHINESE);
        calendar.setTimeInMillis(time);
        String date= DateFormat.format("h:mm a ",calendar).toString();
        return date;
    }
}
