package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {
    private String check = "";
    private TextView pageTitle, titleQuestion;
    private EditText username, question1, question2;
    private Button verifyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        check = getIntent().getStringExtra("check");


        pageTitle= findViewById(R.id.page_title);
        titleQuestion= findViewById(R.id.title_questions);
        question1= findViewById(R.id.question_1);
        question2= findViewById(R.id.question_2);
        username= findViewById(R.id.find_username);
        verifyBtn= findViewById(R.id.verify_btn);
    }


    @Override
    protected void onStart() {
        super.onStart();

        username.setVisibility(View.GONE);

        if (check.equals("settings")){
            pageTitle.setText("Set Questions");
            titleQuestion.setText("Please set Answers for the following security questions");

            username.setVisibility(View.GONE);

            verifyBtn.setText("Set");

            displayPreviousAnswer();

            verifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setAnswers();
                }
            });
        }
        else if (check.equals("login")){
            username.setVisibility(View.VISIBLE);

            verifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verifyUser();
                }
            });
        }
    }

    private void setAnswers(){
        String answer1 = question1.getText().toString();
        String answer2 = question2.getText().toString();

        if (question1.equals("") && question2.equals("")) {
            Toast.makeText(ResetPasswordActivity.this, "Please answer both questions.", Toast.LENGTH_SHORT).show();
        }
        else {
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Users")
                    .child(Prevalent.currentOnlineUser.getUsername());

            HashMap<String, Object> userdatamap = new HashMap<>();
            userdatamap.put("answer1", answer1);
            userdatamap.put("answer2", answer2);

            ref.child("Security Questions").updateChildren(userdatamap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ResetPasswordActivity.this, "You have answer the security question successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ResetPasswordActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private void displayPreviousAnswer(){
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .child(Prevalent.currentOnlineUser.getUsername());

        ref.child("Security Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String ans1 = snapshot.child("answer1").getValue().toString();
                    String ans2 = snapshot.child("answer2").getValue().toString();

                    question1.setText(ans1);
                    question2.setText(ans2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void verifyUser(){
        final String userName = username.getText().toString();
        final String answer1 = question1.getText().toString();
        final String answer2 = question2.getText().toString();

        if(!userName.equals("") && !answer1.equals("") && !answer2.equals("")){
            final DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Users")
                    .child(userName);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        String mUsername = snapshot.child("username").getValue().toString();

                        if (userName.equals(mUsername)){
                            if (snapshot.hasChild("Security Questions")){
                                String ans1 = snapshot.child("Security Questions").child("answer1").getValue().toString();
                                String ans2 = snapshot.child("Security Questions").child("answer2").getValue().toString();

                                if (!ans1.equals(answer1)){
                                    Toast.makeText(ResetPasswordActivity.this, "Your 1st answer is wrong.", Toast.LENGTH_SHORT).show();
                                }
                                else if (!ans2.equals(answer2)){
                                    Toast.makeText(ResetPasswordActivity.this, "Your 2nd answer is wrong.", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                                    builder.setTitle("New Password");

                                    final EditText newPassword = new EditText(ResetPasswordActivity.this);
                                    newPassword.setHint("Write new password here...");
                                    builder.setView(newPassword);

                                    builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (!newPassword.getText().toString().equals("")){
                                                ref.child("password")
                                                        .setValue(newPassword.getText().toString())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    Toast.makeText(ResetPasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();

                                                                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                                                    startActivity(intent);
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                                    builder.show();
                                }
                            }
                        }
                        else{
                            Toast.makeText(ResetPasswordActivity.this, "You have not set the security questions.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(ResetPasswordActivity.this, "This user is not exist.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else {
            Toast.makeText(this, "Please complete the form.", Toast.LENGTH_SHORT).show();
        }
    }
}