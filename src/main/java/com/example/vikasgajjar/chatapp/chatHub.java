package com.example.vikasgajjar.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class chatHub extends AppCompatActivity {
    private FirebaseStorage mStorage;
    private RecyclerView chatrooms;
    private FirebaseDatabase mFirebaseDatabase;
    private ArrayList<User> list_of_rooms = new ArrayList<User>();
    private Button addRoomButton;
    private DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference();
    private EditText addRoomEditText;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ContactAdapter contactAdapter;
    private LinearLayoutManager mLayoutManager;


    @Override
    protected void onStart() {
        super.onStart();
        setChatrooms();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_hub);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        chatrooms = (RecyclerView) findViewById(R.id.rv_contacts);
        addRoomButton = (Button) findViewById(R.id.addChatroomBtn);
        addRoomEditText = (EditText) findViewById(R.id.chatroomEditText);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        contactAdapter = new ContactAdapter(this, list_of_rooms);
        mLayoutManager = new LinearLayoutManager(this);
        chatrooms.setLayoutManager(mLayoutManager);
        chatrooms.setAdapter(contactAdapter);

        addRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRoom();
            }
        });


    }

    private void addRoom(){
        mDataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userRequested = addRoomEditText.getText().toString().replace(".", ",");
                //check if target user is valid
                if (dataSnapshot.child("userEmail").hasChild(userRequested)){
                    //readDataBase();
                    FirebaseUser current = mAuth.getCurrentUser();
                    // get target information
                    String target = (String)dataSnapshot.child("userEmail").child(userRequested).child("UID").getValue(); // UID of targeted user
                    String targetName = (String)dataSnapshot.child("users").child(target).child("displayName").getValue();
                    String targetPhotoUrl = (String)dataSnapshot.child("userEmail").child(userRequested).child("pic").getValue().toString();
                    //make chatroom on user side. we want targets UID
                    mDataBase.child("users").child(current.getUid()).child("chatrooms").child(targetName);
                    mDataBase.child("users").child(current.getUid()).child("chatrooms").child(targetName).child("messages").setValue("");
                    mDataBase.child("users").child(current.getUid()).child("chatrooms").child(targetName).child("UID").setValue(target);
                    // make chatroom on target side.
                    mDataBase.child("users").child(target).child("chatrooms").child(current.getDisplayName());
                    mDataBase.child("users").child(target).child("chatrooms").child(current.getDisplayName()).child("messages").setValue("");
                    mDataBase.child("users").child(target).child("chatrooms").child(current.getDisplayName()).child("UID").setValue(current.getUid());

                    // add a child to each persons chatroom in the DB called messages which stores the messages
                    DatabaseReference targetChatroom = mDataBase.child("users").child(current.getUid()).child("chatrooms").child(targetName);
                    DatabaseReference currChatroom = mDataBase.child("users").child(target).child("chatrooms").child(current.getDisplayName());

                    targetChatroom.child("info").setValue(new User(current.getDisplayName(), targetPhotoUrl));
                    System.out.println("_---------------------" + current.getPhotoUrl().toString());
                    currChatroom.child("info").setValue(new User(targetName, current.getPhotoUrl().toString()));
                    System.out.println("_---------------------" + targetPhotoUrl);
                    list_of_rooms.add(new User(targetName, targetPhotoUrl));
                    contactAdapter.notifyItemInserted(contactAdapter.getItemCount());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setChatrooms(){
        mDataBase.child("users").child(mAuth.getCurrentUser().getUid()).child("chatrooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> emails = dataSnapshot.getChildren();
                String chatroomName;
                list_of_rooms.clear();
                contactAdapter.notifyDataSetChanged();
                while (emails.iterator().hasNext()){
                    DataSnapshot curr = emails.iterator().next();
                    String uid = (String)curr.child("UID").getValue();
                    String targetPhotoUrl = (String)curr.child("info").child("photoUrl").getValue();
                    System.out.println(targetPhotoUrl + "From the set chatrooms functions");

                    chatroomName = curr.getKey();
                    User temp = new User(chatroomName.toString(), targetPhotoUrl);
                    temp.setUid(uid);
                    list_of_rooms.add(temp);
                    contactAdapter.notifyItemInserted(contactAdapter.getItemCount());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.signoutMenuItem){
            mAuth.signOut();
            startActivity(new Intent(chatHub.this, MainActivity.class));



            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
