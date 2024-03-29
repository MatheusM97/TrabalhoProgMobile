package com.example.chat_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;

import java.util.List;

import javax.annotation.Nullable;

public class ContatosActivity extends AppCompatActivity {

    private GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contatos);

        RecyclerView rv = findViewById(R.id.recycler);

        adapter = new GroupAdapter();

        rv.setAdapter(adapter);

        rv.setLayoutManager(new LinearLayoutManager(this));

        //Chama a tela dos chat ao cliclar nos contatos
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {

               Intent intent = new Intent(ContatosActivity.this, ChatActivity.class);

               UserItem userItem = (UserItem)  item;
               intent.putExtra("user", userItem.user);
               startActivity(intent);
            }
        });


        buscarUsuarios();
    }

    private void buscarUsuarios() {

        FirebaseFirestore.getInstance().collection("/users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.e("Teste", e.getMessage(), e);
                            return;
                        }

                        List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                        adapter.clear();
                        for (DocumentSnapshot doc: docs){

                           User user = doc.toObject(User.class);
                           Log.d("Teste", user.getUsername());
                           //correção mostrando o icone do proprio user logado
                           if (!user.getUuid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                           adapter.add(new UserItem(user));

                        }

                    }
                });
    }

    //Método que irá carregar o layout dos contatos
    private class UserItem extends Item<ViewHolder> {


        private final User user;

        private UserItem(User user) {
            this.user = user;
        }


        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
               ImageView imgPhoto =  viewHolder.itemView.findViewById(R.id.imageView);
               TextView txtUsername = viewHolder.itemView.findViewById(R.id.textView);

                //Carrega o nome dos contatos
               txtUsername.setText(user.getUsername());

               if(user.getProfileUrl()!=null){
                   //Carrega as fotos dos usuários
                   Picasso.get().load(user.getProfileUrl())
                           .into(imgPhoto);
               }
               else{
                   Picasso.get().load(R.drawable.avatarpadrao)
                           .into(imgPhoto);
               }

        }

        @Override
        public int getLayout() {
            return R.layout.item_user;
        }
    }

}
