package com.example.chat_mobile;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

public class userItem extends Item<ViewHolder> {


    private final   User user;

    public User getUser() {
        return user;
    }

    public userItem(User user) {
        this.user = user;
    }


    @Override
    public void bind(@NonNull ViewHolder viewHolder, int position) {
        ImageView imgPhoto =  viewHolder.itemView.findViewById(R.id.imageView);
        TextView txtUsername = viewHolder.itemView.findViewById(R.id.textView);

        //Carrega o nome dos contatos
        txtUsername.setText(user.getUsername());

        //Carrega as fotos dos usuários
        Picasso.get().load(user.getProfileUrl())
                .into(imgPhoto);
    }

    @Override
    public int getLayout() {
        return R.layout.item_user;
    }
}