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
import com.example.admin.moments.settings.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ADMIN on 2/25/2018.
 */

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    private List<Messages>messagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference mReference;
    public MessagesAdapter(Context context,List<Messages>messagesList){
        this.messagesList=messagesList;
        this.context=context;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item, parent, false);
                return new UserViewHolder(view);
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item_partner, parent, false);
                return new PartnerViewHolder(view);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case 1:
                UserViewHolder vh1 = (UserViewHolder) holder;
                configureUserHolder(vh1, position);
                break;
            case 2:
                PartnerViewHolder vh2 = (PartnerViewHolder) holder;
                configurePartnerHolder(vh2,position);
                break;


        }
    }
    private void configureUserHolder(final UserViewHolder vh1, int position) {

        mAuth=FirebaseAuth.getInstance();
        String current_user=mAuth.getCurrentUser().getUid();
        Messages message=messagesList.get(position);
        String from=message.getFrom();
        if(from.equals(current_user)) {
            mReference= FirebaseDatabase.getInstance().getReference().child("Users").child(current_user);
            mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                     final String image=dataSnapshot.child("image").getValue().toString();
                    Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.icon).into(vh1.mImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {


                            Picasso.with(context).load(image).placeholder(R.drawable.icon).into(vh1.mImage);
                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        vh1.mTextMessage.setText("      "+message.getMessages());

    }
    private void configurePartnerHolder(final PartnerViewHolder vh2, int position) {

        mAuth=FirebaseAuth.getInstance();
        String current_user=mAuth.getCurrentUser().getUid();
        Messages message=messagesList.get(position);
        String from=message.getFrom();
        if(!from.equals(current_user)) {
            mReference= FirebaseDatabase.getInstance().getReference().child("Users").child(from);
            mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String image=dataSnapshot.child("image").getValue().toString();
                    Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.icon).into(vh2.mImage1, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {


                            Picasso.with(context).load(image).placeholder(R.drawable.icon).into(vh2.mImage1);
                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        vh2.mTextMessage1.setText(message.getMessages()+"      ");

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

    @Override
    public int getItemViewType(int position)
    {
        mAuth=FirebaseAuth.getInstance();
        String current_user=mAuth.getCurrentUser().getUid();
        if(messagesList.get(position).getFrom().equals(current_user) )
        { return 1;}

        else return 2;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextMessage;
        private CircleImageView mImage;
        public UserViewHolder(View itemView) {
            super(itemView);
            mTextMessage=itemView.findViewById(R.id.textmessage);
            mImage=itemView.findViewById(R.id.circleImageMessage);
        }
    }

        public class PartnerViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextMessage1;
        private CircleImageView mImage1;
        public PartnerViewHolder(View itemView) {
            super(itemView);
            mTextMessage1=itemView.findViewById(R.id.textmessage1);
            mImage1=itemView.findViewById(R.id.circleImageMessage1);
        }
    }
}
