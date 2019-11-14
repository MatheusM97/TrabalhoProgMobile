package com.example.chat_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;

import java.util.List;
//classe responsavel por exibir mensagens na tela
public class MensagensActivity extends AppCompatActivity {
    private GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensagens);
        RecyclerView rv = findViewById(R.id.recycler);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new GroupAdapter();
        rv.setAdapter(adapter);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {

                Intent intent = new Intent(MensagensActivity.this, ChatActivity.class);
               // MainActivity.ContatoItem contatoItem = (MainActivity.ContatoItem) item;


                ContatoItem contatoItem= (ContatoItem)  item;



                Toast.makeText(MensagensActivity.this,contatoItem.contato.getUuid() ,Toast.LENGTH_LONG).show();
                intent.putExtra("user", contatoItem.contato.getUser());
                startActivity(intent);
                /*ContatosActivity.UserItem userItem = (ContatosActivity.UserItem)  item;
                intent.putExtra("user", userItem.user);
                startActivity(intent);*/
            }
        });

        //Método que verifica se o usuário esta logado
        verifyAuthentication();

        buscarUltimaMensagem();
    }

    private void buscarUltimaMensagem() {
        //usuarioAtual
        String uid = FirebaseAuth.getInstance().getUid();

        FirebaseFirestore.getInstance().collection("ultimas-mensagens")
                .document(uid)
                .collection("contatos")
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();

                        if (documentChanges != null) {
                            for (DocumentChange doc : documentChanges
                            ) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    Contato contato = doc.getDocument().toObject(Contato.class);
                                    adapter.add(new ContatoItem(contato));
                                }
                            }
                        }
                    }
                });

    }

    public void verifyAuthentication() {
        if (FirebaseAuth.getInstance().getUid() == null) {
            Intent intent = new Intent(MensagensActivity.this, MainActivity.class);

            //Flags que fazem que as telas sejam movidas
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        }

    }

    //Adciona no menu da tela de mensagens o res menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //Realiza o processo que o usuário define de logout ou acessar os contatos
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            //Chama a tela de contatos
            case R.id.contacts:
                Intent intent = new Intent(MensagensActivity.this, ContatosActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                verifyAuthentication();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ContatoItem extends Item<ViewHolder> {
        private final Contato contato;

        public ContatoItem(Contato c) {
            this.contato = c;
        }


        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
            TextView nomeUsuario = viewHolder.itemView.findViewById(R.id.nomeUsuario);
            TextView mensagem = viewHolder.itemView.findViewById(R.id.mensagem);
            ImageView img = viewHolder.itemView.findViewById(R.id.imageView);

            nomeUsuario.setText(contato.getNomeUsuario());

            mensagem.setText(contato.getUltimaMensagem());

            if(contato.getFotoURL()==null){
                Picasso.get()
                        .load(R.drawable.avatarpadrao)
                        .into(img);
            }else{
                Picasso.get()
                        .load(contato.getFotoURL())
                        .into(img);
            }

        }

        @Override
        public int getLayout() {
            return R.layout.item_mensagem_usuario;
        }
    }
}

