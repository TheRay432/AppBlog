package com.example.appblog.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.style.LocaleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appblog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    private EditText log_mail,log_password;
    private Button log_btn,log_reg;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }
    private void  init(){

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        log_mail=(EditText)findViewById(R.id.login_mail);
        log_password=(EditText)findViewById(R.id.login_password);
        log_btn=(Button)findViewById(R.id.login_btn);
        log_reg=(Button)findViewById(R.id.login_reg);
        log_btn.setOnClickListener(mybtn);
        log_reg.setOnClickListener(mybtn);

    }
    private View.OnClickListener mybtn=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.login_btn:{
                    final String log_Mail=log_mail.getText().toString();
                    final String log_Password=log_password.getText().toString();
                    if(log_Mail.isEmpty() || log_Password.isEmpty()){
                        showMessage("請填寫完整資訊!");
                    }
                    else {
                        progressDialog=new ProgressDialog(LoginActivity.this);
                        progressDialog.setMessage("請稍等...");
                        progressDialog.show();
                        userLogin(log_Mail,log_Password);
                    }
                    break;
                }
                case R.id.login_reg:{
                    Intent i=new Intent(LoginActivity.this,RegisterActivity.class);
                    startActivity(i);
                    finish();
                    break;
                }
            }
        }
    };

    private void userLogin(String log_mail, String log_password) {
        mAuth.signInWithEmailAndPassword(log_mail,log_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent HomeIntent=new Intent(LoginActivity.this,HomeActivity.class);
                    startActivity(HomeIntent);
                    progressDialog.dismiss();
                    finish();
                }
                else {
                    progressDialog.dismiss();
                    showMessage( task.getException().getLocalizedMessage());

                }
            }

        });
    }

    private void showMessage(String s) {
        Toast.makeText(LoginActivity.this,s,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser!=null){
            Intent i=new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(i);
            finish();
        }
    }
}