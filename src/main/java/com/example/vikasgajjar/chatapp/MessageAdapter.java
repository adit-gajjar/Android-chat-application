package com.example.vikasgajjar.chatapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by vikasgajjar on 2018-05-11.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    private ArrayList<Message> conversationData = new ArrayList<Message>();
    private LayoutInflater mInflater;
    MessageAdapter(Context context, ArrayList<Message> data) {
        this.mInflater = LayoutInflater.from(context);
        this.conversationData = data;
    }


    @Override
    public int getItemViewType(int position) {
        if (conversationData.get(position).isSent()){
            return 0;
        } else{
            return 1;
        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0){
            view = mInflater.inflate(R.layout.message_item, parent, false);
        } else {
            view = mInflater.inflate(R.layout.message_item_recieved, parent, false);
        }
        return new MessageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = conversationData.get(position);
        String text = message.getMessage();
        holder.itemView.setText(text);

    }

    @Override
    public int getItemCount() {
        return conversationData.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder{
        private TextView itemView;

        public MessageViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView.findViewById(R.id.text_message);
        }


    }









}
