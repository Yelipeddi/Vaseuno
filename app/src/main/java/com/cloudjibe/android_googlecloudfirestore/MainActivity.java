package com.cloudjibe.android_googlecloudfirestore;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cloudjibe.android_googlecloudfirestore.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loginpack.LoginActivity;
import com.welcome.Welcome;

import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    //VIEWS AND WIDGET FIELDS
    Button createUser, moveToLogin;
    EditText userEmailEdit, userPassWordEdit;

    //FIREBASE AUTHENTICATION FIELDS
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "Abhi";

    DatabaseReference mDatabaseRef, mUserCheckData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.cloudjibe.android_googlecloudfirestore.R.layout.activity_main);

        //ASSIGN ID
        createUser = (Button) findViewById(com.cloudjibe.android_googlecloudfirestore.R.id.createUserBtn);
        moveToLogin = (Button) findViewById(com.cloudjibe.android_googlecloudfirestore.R.id.moveToLogin);
        userEmailEdit = (EditText) findViewById(com.cloudjibe.android_googlecloudfirestore.R.id.emailEditTextCreate);
        userPassWordEdit = (EditText) findViewById(com.cloudjibe.android_googlecloudfirestore.R.id.passEditTextCreate);

        //ASSIGN INSTANCES
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mUserCheckData = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {

                    final String emailForVer = user.getEmail();

                    mUserCheckData.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            checkUserValidation(dataSnapshot, emailForVer);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {


                }


            }
        };

        //ON CLICK LISTENERS

        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String userEmailString, userPassString;

                userEmailString = userEmailEdit.getText().toString().trim();
                userPassString = userPassWordEdit.getText().toString().trim();

                if (!TextUtils.isEmpty(userEmailString) && !TextUtils.isEmpty(userPassString)) {

                    mAuth.createUserWithEmailAndPassword(userEmailString, userPassString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                DatabaseReference mChildDatabase = mDatabaseRef.child("Users").push();

                                String key_user = mChildDatabase.getKey();

                                mChildDatabase.child("isVerified").setValue("unverified");
                                mChildDatabase.child("userKey").setValue(key_user);
                                mChildDatabase.child("emailUser").setValue(userEmailString);
                                mChildDatabase.child("passWordUser").setValue(userPassString);

                                Toast.makeText(MainActivity.this, "User Account Created", Toast.LENGTH_LONG).show();


                                startActivity(new Intent(MainActivity.this, Profile.class));
                                Intent in = new Intent( MainActivity.this, Profile.class);
                                in.putExtra("USER_KEY" , mChildDatabase.child("userKey").getKey().toString());
                                startActivity(in);

                            } else {
                                Log.w( TAG,"createUserWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Failed to create User Account", Toast.LENGTH_LONG).show();

                            }

                        }
                    });

                }


            }
        });

        //MOVE TO LOGIN
        moveToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, LoginActivity.class));

            }
        });


    }

    private void checkUserValidation(DataSnapshot dataSnapshot, String emailForVer) {


        Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext())
        {

            DataSnapshot dataUser = (DataSnapshot) iterator.next();

            if( dataUser.child("emailUser").getValue().toString().equals(emailForVer))
            {

                if (dataUser.child("isVerified").getValue().toString().equals("unverified"))
                {

                    Intent in = new Intent( MainActivity.this, Profile.class);
                    in.putExtra("USER_KEY" , dataUser.child("userKey").getValue().toString());
                    startActivity(in);

                }else
                {

                    startActivity(new Intent(MainActivity.this, Welcome.class));

                }

            }

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        //ON ACTIVITY START CHECK USER AUTHENTICATION
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //REMOVE THE LISTENER ON ACTIVITY STOP
        mAuth.removeAuthStateListener(mAuthListener);
    }
}
