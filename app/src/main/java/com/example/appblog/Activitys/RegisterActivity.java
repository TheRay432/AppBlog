package com.example.appblog.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.appblog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {
    private ImageView reg_img;
    private EditText reg_name,reg_mail,reg_password,reg_password2;
    private Button reg_btn;

    Uri pickImageUri=null;
    FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    private void init() {
        mAuth=FirebaseAuth.getInstance();
        reg_img=(ImageView)findViewById(R.id.reg_image);
        reg_name=(EditText)findViewById(R.id.reg_name);
        reg_mail=(EditText)findViewById(R.id.reg_Mail);
        reg_password=(EditText)findViewById(R.id.reg_Password);
        reg_password2=(EditText)findViewById(R.id.reg_Password2);
        reg_btn=(Button)findViewById(R.id.reg_btn);
        reg_img.setOnClickListener(myimage);
        reg_btn.setOnClickListener(mybtn);

    }
    private View.OnClickListener mybtn=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String name=reg_name.getText().toString();
            final String Mail=reg_mail.getText().toString();
            final String password=reg_password.getText().toString();
            final String password2=reg_password2.getText().toString();
            if(name.isEmpty() || Mail.isEmpty() || password.isEmpty() || !password2.equals(password)){
                showMessage("請填寫完整資訊並確認密碼一致!");
            }
            else {
                progressDialog=new ProgressDialog(RegisterActivity.this);
                progressDialog.setMessage("請稍等...");
                progressDialog.show();
                createUser(name,Mail,password);
            }
        }
    };

    private void createUser(String name, String mail, String password) {
        if(pickImageUri==null){
            showMessage("請上傳圖片!");
            progressDialog.dismiss();
            return;
        }

        mAuth.createUserWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    showMessage("帳戶創建完成");
                    createUserInfo(name,pickImageUri,mAuth.getCurrentUser());
                }
                else {
                    progressDialog.dismiss();
                    showMessage("帳戶創建失敗:"+task.getException().getLocalizedMessage());
                }
            }
        });
    }

    private void createUserInfo(String name, Uri pickImageUri, FirebaseUser currentUser) {
        StorageReference mStorage=FirebaseStorage.getInstance().getReference().child("user_image");
        StorageReference imageFilePath=mStorage.child(pickImageUri.getLastPathSegment());
        imageFilePath.putFile(pickImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest mChange=new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();
                        currentUser.updateProfile(mChange).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Intent i=new Intent(RegisterActivity.this,HomeActivity.class);
                                    startActivity(i);
                                    finish();
                                    showMessage("註冊完成");
                                    progressDialog.dismiss();
                                }
                                else {
                                    progressDialog.dismiss();
                                    showMessage(task.getException().getLocalizedMessage());
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void showMessage(String s) {
        Toast.makeText(RegisterActivity.this,s,Toast.LENGTH_SHORT).show();
    }

    private View.OnClickListener myimage=new View.OnClickListener() {
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

    private void open() {
        Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.setType("image/*");
        startActivityForResult(i,1);
    }

    private void check() {
        if(ContextCompat.checkSelfPermission(RegisterActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)
        !=PackageManager.PERMISSION_GRANTED){



                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);






        }
        else {
            open();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null);
            pickImageUri=data.getData();
            reg_img.setImageURI(pickImageUri);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==1){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                open();
            }
            else if(grantResults[0]==PackageManager.PERMISSION_DENIED) {
                if(ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    AlertDialog.Builder builder=new AlertDialog.Builder(this);
                    builder.setTitle("權限需求")
                            .setMessage("需要同意")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(RegisterActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                                }
                            });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}