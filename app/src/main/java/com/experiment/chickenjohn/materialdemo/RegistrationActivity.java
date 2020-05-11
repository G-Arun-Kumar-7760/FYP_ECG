package com.experiment.chickenjohn.materialdemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationActivity extends AppCompatActivity {
    private EditText UserName, Password;
    private Button signIn;
    private TextView logIn;
    private FirebaseAuth firebaseauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setupUIViews();
        firebaseauth=FirebaseAuth.getInstance();
        signIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                if(validate())
                {
                   String User_Name=UserName.getText().toString().trim();
                   String User_Password=Password.getText().toString().trim();
                   firebaseauth.createUserWithEmailAndPassword(User_Name,User_Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if(task.isSuccessful()) {
                               sendEmailVerification();
                           }
                           else
                           {
                               Toast.makeText(RegistrationActivity.this, "Registration Unsuccessfull!!!", Toast.LENGTH_SHORT).show();
                           }
                       }
                   });
                }
            }
        });
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(RegistrationActivity.this, LoginPage.class));
            }
        });

    }
    private void setupUIViews(){
        UserName=(EditText)findViewById(R.id.regUsername);
        Password=(EditText)findViewById(R.id.regpassword);
        signIn=(Button)findViewById(R.id.btnSignin);
        logIn=(TextView)findViewById(R.id.regsignin);
    }
    private Boolean validate()
    {
        Boolean result=true;

        String name=UserName.getText().toString();
        String pass=Password.getText().toString();

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

    private void sendEmailVerification(){
        FirebaseUser firebaseUser = firebaseauth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(RegistrationActivity.this, "Successfully Registered, Verification mail sent!", Toast.LENGTH_SHORT).show();
                        firebaseauth.signOut();
                        finish();
                        startActivity(new Intent(RegistrationActivity.this, LoginPage.class));
                    }else{
                        Toast.makeText(RegistrationActivity.this, "Verification mail has'nt been sent!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
