package com.cloudjibe.android_googlecloudfirestore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.welcome.Welcome;

public class Profile extends AppCompatActivity {

    //DECLARE THE FIELDS
    EditText userName, userContact;
    Button saveBtn;

    //FIREBASE DATAREFERENCE
    DatabaseReference mDataRef;

    //STRING KEY
    String keyUser;

    String userNameString, userContactString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.cloudjibe.android_googlecloudfirestore.R.layout.activity_profile);

        //GET USER KEY FROM INTENT
        keyUser = getIntent().getStringExtra("USER_KEY");



        //FIREBASE DATABASE REFERENCE
        mDataRef = FirebaseDatabase.getInstance().getReference().child("Users").child(keyUser);

        //ASSIGN ID'S
        userName = (EditText) findViewById(com.cloudjibe.android_googlecloudfirestore.R.id.userNameEditText);
        userContact = (EditText) findViewById(com.cloudjibe.android_googlecloudfirestore.R.id.userPhnoEditText);
        saveBtn = (Button) findViewById(com.cloudjibe.android_googlecloudfirestore.R.id.userProfileBtn);


        //SAVE BTN LOGIC
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userNameString = userName.getText().toString();
                userContactString = userContact.getText().toString();


                Log.d("adf" , userNameString + ":" + userContactString);

                if(!TextUtils.isEmpty(userNameString) && !TextUtils.isEmpty(userContactString))
                {

                    mDataRef.child("userName").setValue(userNameString);
                    mDataRef.child("userContact").setValue(userContactString);
                    mDataRef.child("isVerified").setValue("verfied");

                    Toast.makeText(Profile.this, "User profile added", Toast.LENGTH_LONG).show();

                    startActivity(new Intent(Profile.this, Welcome.class));

                }

            }
        });



    }
}
