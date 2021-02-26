package com.example.appblog.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appblog.Activitys.HomeActivity;
import com.example.appblog.Activitys.PostDetailActivity;
import com.example.appblog.Model.Post;
import com.example.appblog.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> implements Filterable {

    private HomeActivity activity;
    private List<Post> mData;
    private List<Post> mDataFilter;
    FirebaseAuth mAuth;

    public PostAdapter() {
    }

    public PostAdapter(HomeActivity activity, List<Post> mData) {
        this.activity = activity;
        this.mData = mData;
        this.mDataFilter=mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(activity).inflate(R.layout.row_post_item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        mAuth=FirebaseAuth.getInstance();
        holder.card_anim.setAnimation(AnimationUtils.loadAnimation(activity,R.anim.scale_animation));
        holder.tvTitle.setText(mDataFilter.get(position).getTitle());
        Glide.with(activity).load(mDataFilter.get(position).getPicture()).into(holder.imgPost);
        holder.tvUserName.setText(mDataFilter.get(position).getUserName());
        Glide.with(activity).load(mDataFilter.get(position).getUserPhoto()).into(holder.imgPostProfile);


    }

    @Override
    public int getItemCount() {
        return mDataFilter.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String key=constraint.toString();
                if(key.isEmpty()){
                    mDataFilter=mData;
                }
                else {
                    List<Post> lstFilter=new ArrayList<>();
                    for(Post row:mData){
                        if(row.getUserName().toLowerCase().contains(key.toLowerCase())){
                            lstFilter.add(row);
                        }
                    }
                    mDataFilter=lstFilter;
                }
                FilterResults results=new FilterResults();
                results.values=mDataFilter;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mDataFilter=(List<Post>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle,tvUserName;
        ImageView imgPost,imgPostProfile;
        CardView card_anim;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName=(TextView)itemView.findViewById(R.id.row__post_userName);
            tvTitle=(TextView)itemView.findViewById(R.id.row_post_title);
            imgPost=(ImageView)itemView.findViewById(R.id.row_post_content_img);
            imgPostProfile=(ImageView)itemView.findViewById(R.id.row_post_user_img);
            card_anim=(CardView)itemView.findViewById(R.id.card_anim);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(activity, PostDetailActivity.class);
                    int postion=getAdapterPosition();
                    i.putExtra("title",mData.get(postion).getTitle());
                    i.putExtra("Description",mData.get(postion).getDescription());
                    i.putExtra("postImage",mData.get(postion).getPicture());
                    i.putExtra("postKey",mData.get(postion).getUserKey());
                    i.putExtra("userPhoto",mData.get(postion).getUserPhoto());
                    i.putExtra("userName",mData.get(postion).getUserName());
                    long time=(long) mData.get(postion).getTimeStamp();
                    i.putExtra("postDate",time);

                    activity.startActivity(i);
                }
            });
        }
    }
}
