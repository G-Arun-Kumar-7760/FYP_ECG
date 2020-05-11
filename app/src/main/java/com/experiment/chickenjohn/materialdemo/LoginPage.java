package com.experiment.chickenjohn.materialdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {
    private EditText User_Name, Pass_word;
    private Button log_in;
    private TextView Sign_IN, forg_pass;
    private TextView attempts;
    private FirebaseAuth firebaseauth;
    private ProgressDialog dialog;
    public byte counter=5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupUIViews();
        attempts.setText("No of Attempt remaning:5");
        firebaseauth=FirebaseAuth.getInstance();
        dialog=new ProgressDialog(this);
        log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  if(counter>0) {
                      validate(User_Name.getText().toString(), Pass_word.getText().toString());
                      }
                      else
                      Toast.makeText(LoginPage.this, "Maximum Change Exceeded, Try later", Toast.LENGTH_SHORT).show();
            }
        });
        Sign_IN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(LoginPage.this, RegistrationActivity.class));
            }
        });
        forg_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(LoginPage.this, PasswordActivity.class));
            }
        });
    }
    private void setupUIViews(){
        User_Name=(EditText)findViewById(R.id.Username);
        Pass_word=(EditText)findViewById(R.id.password);
        log_in=(Button)findViewById(R.id.btnSignin);
        Sign_IN=(TextView)findViewById(R.id.signin);
        attempts=(TextView)findViewById(R.id.attempts);
        forg_pass=(TextView)findViewById(R.id.ForgotPassword);
    }
    private Boolean validate1()
    {
        Boolean result=true;

        String name=User_Name.getText().toString();
        String pass=Pass_word.getText().toString();

        if(name.isEmpty() || pass.isEmpty() )
        {
            Toast.makeText(this,"Please Enter all Details", Toast.LENGTH_SHORT).show();
            result=false;

        }
        else if(pass.contains(" "))
        {
            Toast.makeText(this,"Password should not contain space", Toast.LENGTH_SHORT).show();
            result=false;
        }

        return result;
    }
    private void validate(String username, String pass) {
        if (validate1()) {
            dialog.setMessage("Loading!!!");
            dialog.show();
            firebaseauth.signInWithEmailAndPassword(username, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        dialog.dismiss();
                        checkEmailVerification();
                    } else {
                        if (counter != 0) {
                            counter--;
                        }
                        attempts.setText("No of Attempt remaning:" +
                                "" + counter);
                        Toast.makeText(LoginPage.this, "Login Unsuccessfull!!!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                }
            });
        }
        else
        {
            Toast.makeText(LoginPage.this, "Enter Credentials!!!", Toast.LENGTH_SHORT).show();
        }
    }
    private void checkEmailVerification(){
        FirebaseUser firebaseUser = firebaseauth.getInstance().getCurrentUser();
        Boolean emailflag = firebaseUser.isEmailVerified();


       if(emailflag){
           Toast.makeText(LoginPage.this, "Login Successfull!!!", Toast.LENGTH_SHORT).show();
           finish();
           startActivity(new Intent(LoginPage.this, MainActivity.class));

        }else{
            Toast.makeText(this, "Verify your email", Toast.LENGTH_SHORT).show();
            firebaseauth.signOut();
        }
    }

}
