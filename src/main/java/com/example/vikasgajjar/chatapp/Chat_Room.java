package com.example.vikasgajjar.chatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Chat_Room extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MessageAdapter mAdapter;
    private ArrayList<Message> data = new ArrayList<Message>();
    private DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private String temp_key;
    private String room_name;
    private String targetUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_messages);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // We have yet to set an adapter
        this.mAdapter = new MessageAdapter(this, data);
        mRecyclerView.setAdapter(mAdapter);
        room_name = getIntent().getExtras().get("name").toString();


        setTitle(room_name);
        Button sendBtn = (Button) findViewById(R.id.button_chatbox_send);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                addTextMessage();
            }
        });
        final DatabaseReference currentChatRoom = mDataBase.child("users").child(mAuth.getCurrentUser().getUid()).child("chatrooms").child(room_name);
        // retrieves all messages from the database
        currentChatRoom.child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable messagesData = dataSnapshot.getChildren();
                while (messagesData.iterator().hasNext()){
                    Message message = ((DataSnapshot)messagesData.iterator().next()).getValue(Message.class);
                    data.add(message);
                    mAdapter.notifyItemInserted(mAdapter.getItemCount());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // event listenter for when an object is added.
        currentChatRoom.child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Message message = dataSnapshot.getValue(Message.class);
                if (!message.isSent()){
                    data.add(message);
                    mAdapter.notifyItemInserted(mAdapter.getItemCount());
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void addTextMessage(){
        final EditText chatbox = (EditText) findViewById(R.id.edittext_chatbox);
        final String userMessage = chatbox.getText().toString();
        final String user = mAuth.getCurrentUser().getDisplayName();
        final String userUID = mAuth.getCurrentUser().getUid();
        final DatabaseReference currentChatRoom = mDataBase.child("users").child(userUID).child("chatrooms").child(room_name);
        final int identifier; // unique identifier for messsage
        chatbox.setText("");


        currentChatRoom.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                targetUID = (String)dataSnapshot.child("UID").getValue();
                Message currentMessage = new Message(userMessage,user, room_name, targetUID, userUID);
                currentMessage.setSent(true);
                data.add(currentMessage);
                mAdapter.notifyItemInserted(mAdapter.getItemCount());
                // first store it on our side
                DatabaseReference newMessage = currentChatRoom.child("messages").push();
                newMessage.setValue(currentMessage);
                Message m = new Message(userMessage,user, room_name, targetUID, userUID);
                m.setSent(false);
                DatabaseReference targetChatRoom = mDataBase.child("users").child(targetUID).child("chatrooms").child(user);
                DatabaseReference targetMessage = targetChatRoom.child("messages").push();
                targetMessage.setValue(m);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}



