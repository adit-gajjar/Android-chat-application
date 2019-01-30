package com.example.vikasgajjar.chatapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.vikasgajjar.chatapp.R;
import com.example.vikasgajjar.chatapp.User;

import java.util.ArrayList;

/**
 * Created by vikasgajjar on 2018-07-20.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>{
    private ArrayList<User> contacts_list = new ArrayList<User>();
    private LayoutInflater mInflater;
    private Context mContext;

    public ContactAdapter(Context context, ArrayList<User> data){
        this.mInflater = LayoutInflater.from(context);
        this.contacts_list = data;
        this.mContext = context;
    }



    @Override
    public int getItemCount() {
        return contacts_list.size();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactViewHolder(mInflater.inflate(R.layout.chat_hub_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.itemView.setText(contacts_list.get(position).getName());
        //System.out.println(contacts_list.get(position).getPhotoUrl());

        Glide.with(mInflater.getContext()).load(contacts_list.get(position).getPhotoUrl()).into(holder.imageView);
        holder.setContactClickListener(new ContactClickListener() {
            @Override
            public void onClick(View view, int position) {
                // use this to create a new chatroom create a new intent by sending the target user object;
                Intent chathub_to_chatroom = new Intent(mContext, Chat_Room.class);
                User target = contacts_list.get(position);
                chathub_to_chatroom.putExtra("name", target.getName());
                chathub_to_chatroom.putExtra("uid", target.getUid());
                mContext.startActivity(chathub_to_chatroom);




            }
        });
    }

    class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView itemView;
        private ImageView imageView;
        private ContactClickListener contactClickListener;

        public ContactViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.Contact_Picture);
            this.itemView = itemView.findViewById(R.id.Contact_Name);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            contactClickListener.onClick(v, getAdapterPosition());
        }

        public void setContactClickListener(ContactClickListener contactClickListener){
            this.contactClickListener = contactClickListener;
        }
    }
}
