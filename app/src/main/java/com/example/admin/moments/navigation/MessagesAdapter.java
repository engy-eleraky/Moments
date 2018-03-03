package com.example.admin.moments.navigation;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.moments.R;
import com.example.admin.moments.models.Messages;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ADMIN on 2/25/2018.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    Context context;
    private List<Messages>messagesList;
    private FirebaseAuth mAuth;
    public MessagesAdapter(Context context,List<Messages>messagesList){
        this.messagesList=messagesList;
        this.context=context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recycle_item, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mAuth=FirebaseAuth.getInstance();
        String current_user=mAuth.getCurrentUser().getUid();
        Messages message=messagesList.get(position);
        String from=message.getFrom();
        if(from.equals(current_user)){
          holder.mTextMessage.setBackgroundResource(R.drawable.messgae_text);
          holder.mTextMessage.setTextColor(Color.BLACK);
        }else{
           holder.mTextMessage.setBackgroundColor(Color.WHITE);
           holder.mTextMessage.setTextColor(Color.BLACK);
        }
        holder.mTextMessage.setText(message.getMessages());
    }

    @Override
    public int getItemCount() {
        if(null==messagesList)return 0;
        return messagesList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextMessage;
        private CircleImageView mImage;
        public ViewHolder(View itemView) {
            super(itemView);
            mTextMessage=itemView.findViewById(R.id.textmessage);
            mImage=itemView.findViewById(R.id.circleImageMessage);
        }
    }
}
