package com.example.appblog.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appblog.Activitys.HomeActivity;
import com.example.appblog.Adapter.PostAdapter;
import com.example.appblog.Model.Post;
import com.example.appblog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private FloatingActionButton fab;
    private Dialog dialog;
    ImageView dialogUser,dialogContent;
    private EditText dialog_title,dialog_description;
    private static final int REQ2=2;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private Button dialog_btn;
    private Uri uri=null;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private List<Post> postList;
    private  PostAdapter adapter;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        HomeActivity activity=(HomeActivity)getActivity();
        View v= inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();

        fab=(FloatingActionButton)v.findViewById(R.id.fab);
        fab.setOnClickListener(myfab);
        recyclerView=(RecyclerView)v.findViewById(R.id.home_recyclerview);
        LinearLayoutManager layout=new LinearLayoutManager(activity);
        layout.setStackFromEnd(true);
        layout.setReverseLayout(true);

        recyclerView.setLayoutManager(layout);
       
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Posts");

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            HomeActivity activity=(HomeActivity)getActivity();
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList=new ArrayList<>();
                for(DataSnapshot snap:snapshot.getChildren()){
                    Post post=snap.getValue(Post.class);
                    postList.add(post);
                }
                 adapter=new PostAdapter(activity,postList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private View.OnClickListener myfab=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            pop();
            dialog.show();
        }
    };

    private void pop() {
        HomeActivity activity=(HomeActivity)getActivity();
        dialog=new Dialog(activity);
        dialog.setContentView(R.layout.home_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().gravity=Gravity.CENTER;

        dialogContent=dialog.findViewById(R.id.dialog_imageContent);
        dialogContent.setOnClickListener(myContent);
        dialogUser=dialog.findViewById(R.id.dialog_imageUser);
        dialog_title=dialog.findViewById(R.id.dialog_title);
        dialog_description=dialog.findViewById(R.id.dialog_description);
        dialog_description.setGravity(Gravity.TOP);
        dialog_description.setSingleLine(false);
        dialog_btn=dialog.findViewById(R.id.dialog_btn);
        Glide.with(activity).load(currentUser.getPhotoUrl()).into(dialogUser);
        dialog_btn.setOnClickListener(mydialogbtn);
    }
    private View.OnClickListener mydialogbtn=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            HomeActivity activity=(HomeActivity)getActivity();
            final String Title=dialog_title.getText().toString();
            final String Description=dialog_description.getText().toString();
            if(Title.isEmpty() || Description.isEmpty() || uri==null){
                showMessage("請填寫完整資訊並上傳圖片");
            }
            else {
                progressDialog=new ProgressDialog(activity);
                progressDialog.setMessage("請稍等...");
                progressDialog.show();
                StorageReference storageReference=FirebaseStorage.getInstance().getReference("blog_img");
                StorageReference filePath=storageReference.child(uri.getLastPathSegment());
                filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String Link=uri.toString();
                                Post post=new Post(currentUser.getDisplayName(),dialog_title.getText().toString(),dialog_description.getText().toString(),
                                        Link,currentUser.getUid(),currentUser.getPhotoUrl().toString());
                                addPost(post);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                showMessage(e.getLocalizedMessage());
                            }
                        });
                    }
                });
            }
        }
    };

    private void addPost(Post post) {
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference myRef=database.getReference("Posts").push();
        String key=myRef.getKey();
        post.setUserKey(key);

        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                dialog.dismiss();
            }
        });
    }

    private void showMessage(String s) {
        HomeActivity activity=(HomeActivity)getActivity();
        Toast.makeText(activity,s,Toast.LENGTH_SHORT).show();
    }

    private View.OnClickListener myContent=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(Build.VERSION.SDK_INT>22){
                check();
            }
            else {
                open();
            }
        }
    };

    private void check() {
        HomeActivity activity=(HomeActivity)getActivity();
        if(ContextCompat.checkSelfPermission(activity,Manifest.permission.READ_EXTERNAL_STORAGE)
        !=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQ2);
            }
            else {
                ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQ2);
            }
        }
        else {
            open();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQ2:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    open();
                }
                break;
            }
        }
    }

    private void open() {
        Intent t=new Intent(Intent.ACTION_OPEN_DOCUMENT);
        t.setType("image/*");
        startActivityForResult(t,REQ2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==-1 && requestCode==REQ2 && data!=null){
            uri=data.getData();
            dialogContent.setImageURI(uri);
        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu,menu);
        SearchView searchView=(SearchView)menu.findItem(R.id.mSearch).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);

    }

}