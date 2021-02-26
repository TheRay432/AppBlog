package com.example.appblog.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appblog.Adapter.CommentAdapter;
import com.example.appblog.Model.Comment;
import com.example.appblog.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {
    private ImageView imgPost,imgUserPost,imgCurrentUser;
    private TextView txtDes,txtTime,txtTitle,txtUserName;
    private Button post_detail_btn;
    private EditText edtComment;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String postKey;
    FirebaseDatabase firebaseDatabase;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private List<Comment> listComment;
    private CommentAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        init();
    }

    private void init() {
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();

        recyclerView=(RecyclerView)findViewById(R.id.post_detail_recy);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        txtUserName=(TextView)findViewById(R.id.post_detail_userName);
        imgPost=(ImageView)findViewById(R.id.post_detail_postImg);
        imgUserPost=(ImageView)findViewById(R.id.post_detail_auth);
        imgCurrentUser=(ImageView)findViewById(R.id.post_detail_user);
        txtDes=(TextView)findViewById(R.id.post_detail_description);
        txtTitle=(TextView)findViewById(R.id.post_detail_title);
        txtTime=(TextView)findViewById(R.id.post_detail_time);
        edtComment=(EditText)findViewById(R.id.post_detail_talk);

        post_detail_btn=(Button)findViewById(R.id.post_detail_btn);

        String postImage=getIntent().getExtras().getString("postImage");
        Glide.with(this).load(postImage).into(imgPost);
        String postTitle=getIntent().getExtras().getString("title");
        String postDec=getIntent().getExtras().getString("Description");
        String userPhoto=getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(userPhoto).into(imgUserPost);
        txtTitle.setText(postTitle);
        txtDes.setText(postDec);
        Glide.with(this).load(currentUser.getPhotoUrl()).into(imgCurrentUser);
        String date=timeToString(getIntent().getExtras().getLong("postDate"));
        txtTime.setText(date);
        String name=getIntent().getExtras().getString("userName");
        txtUserName.setText(name);
        postKey=getIntent().getExtras().getString("postKey");
        Log.d("TT",postKey);

        post_detail_btn.setOnClickListener(mybtn);

        initRecy();

    }

    private void initRecy() {
        DatabaseReference commentRef=firebaseDatabase.getReference("Comment").child(postKey);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listComment=new ArrayList<>();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    Comment comment=snapshot1.getValue(Comment.class);
                    listComment.add(comment);

                }
                adapter=new CommentAdapter(PostDetailActivity.this,listComment);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private View.OnClickListener mybtn=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatabaseReference databaseReference=firebaseDatabase.getReference("Comment").child(postKey).push();
            String comment_content=edtComment.getText().toString();
            String uid=currentUser.getUid();
            String uname=currentUser.getDisplayName();
            String uimg=currentUser.getPhotoUrl().toString();
            if(edtComment.getText().toString().equals("")){
                showMessage("請輸入留言");
            }
            else {


                Comment comment = new Comment(comment_content, uid, uimg, uname);
                databaseReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        edtComment.setText("");
                        showMessage("留言成功!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage(e.getLocalizedMessage());
                    }
                });
            }
        }
    };

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

    private String timeToString(long time){
        Calendar calendar=Calendar.getInstance(Locale.CHINESE);
        calendar.setTimeInMillis(time);
        String date= DateFormat.format("yyyy-MM-dd hh:mm",calendar).toString();
        return date;
    }
}