package com.example.vikasgajjar.chatapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{
    Button saveInfo;
    FirebaseAuth mAuth;
    EditText displayNameEditText;
    ImageView profilePicture;
    FirebaseAuth.AuthStateListener mAuthListener;
    Uri uriProfileImage;
    private static final int CHOOSE_IMAGE = 1;
    private StorageReference mStorageRef;
    String profileImageUrl;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();




    @Override
    protected void onStart() {
        super.onStart();
        //loadUserInformation();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.profileImageView){
            showImageChooser();
        } else if (v.getId() == R.id.saveInfoBtn){
            signUp();
        }
    }

    private void saveUserInformation(){
        String displayName = displayNameEditText.getText().toString();

        if (displayName.isEmpty()){
            displayNameEditText.setError("Name required");
            displayNameEditText.requestFocus();
            return;
        }

        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            if (profileImageUrl != null) {
                UserProfileChangeRequest profile =
                        new UserProfileChangeRequest.Builder()
                                .setDisplayName(displayName).setPhotoUri(Uri.parse(profileImageUrl)).build();
                user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            mDatabase.child("users").child(currentUser.getUid());
                            String email = currentUser.getEmail();
                            DatabaseReference work = mDatabase.child("users").child(currentUser.getUid());
                            DatabaseReference email_link = mDatabase.child("userEmail");
                            String userEmail = currentUser.getEmail().toString().replace(".", ",");
                            email_link.child(userEmail);
                            email_link.child(userEmail).child("UID").setValue(currentUser.getUid());
                            email_link.child(userEmail).child("pic").setValue(currentUser.getPhotoUrl().toString());
                            work.child("chatrooms").setValue("");
                            work.child("displayName").setValue(currentUser.getDisplayName());

                            startActivity(new Intent(ProfileActivity.this, chatHub.class));
                        }
                    }
                });
            }
        }


        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ProfileActivity.this, "You are verified", Toast.LENGTH_LONG);
            }
        });






    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                profilePicture.setImageBitmap(bitmap);
                uploadImageToFireBaseStorage();
            } catch(IOException e){
                e.printStackTrace();
            }
        }

    }


    private void uploadImageToFireBaseStorage(){
        final StorageReference profileImageReference =
                FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");

        if (uriProfileImage != null){
            profileImageReference.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    profileImageUrl = downloadUri.toString();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profilePicture = (ImageView) findViewById(R.id.profileImageView);
        saveInfo = (Button) findViewById(R.id.saveInfoBtn);
        mAuth = FirebaseAuth.getInstance();
        displayNameEditText = (EditText) findViewById(R.id.displayNameEditText);
        mStorageRef = FirebaseStorage.getInstance().getReference();



        profilePicture.setOnClickListener(this);
        saveInfo.setOnClickListener(this);



    }

    private void showImageChooser() {
        Intent intent = new Intent();
        System.out.println("hello their");
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent, "Select profile image."), CHOOSE_IMAGE);

    }

    private void loadUserInformation(){
        FirebaseUser user = mAuth.getCurrentUser();


        if (user != null){
            if(user.getPhotoUrl() != null){
                Glide.with(this)
                        .load(user.getPhotoUrl().toString()).into(profilePicture);
            }
        }

        if (user.getDisplayName() != null){
            displayNameEditText.setText(user.getDisplayName());
        }

    }

    private void signUp(){
        String emaiAddress, password;
        EditText emailEditText = (EditText) findViewById(R.id.signUpEmailEditText);
        EditText passwordEditText = (EditText) findViewById(R.id.signUpPasswordEditText);

        emaiAddress = emailEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();

        if (emaiAddress.isEmpty()){
            emailEditText.setError("Please enter a valid email.");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emaiAddress).matches()){
            emailEditText.setError("Please enter a valid email.");
            emailEditText.requestFocus();
            return;
        }

        if (password.length() < 6){
            passwordEditText.setError("A password of minimum length 6 is required.");
            passwordEditText.requestFocus();
            return;
        }



        mAuth.createUserWithEmailAndPassword(emaiAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "User registration Successful", Toast.LENGTH_SHORT).show();
                    saveUserInformation();

                } else {

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "This user has already been registered", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }

}
