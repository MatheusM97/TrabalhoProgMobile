package com.example.chat_mobile;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import java.util.List;

import javax.annotation.Nullable;

public class ChatActivity extends AppCompatActivity {

    private GroupAdapter adapter;
    private User user;
    private EditText editChat;
    private User me;

    //pegando a hora do TimeStamp
    private Timestamp tempo = Timestamp.now();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        user = getIntent().getExtras().getParcelable("user");
        getSupportActionBar().setTitle(user.getUsername());

        RecyclerView rv = findViewById(R.id.recycler_chat);
        editChat = findViewById(R.id.edit_chat);
        adapter = new GroupAdapter();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);


        //Puxa do banco de dados as mensagens enviadas pelo usuario logado
        FirebaseFirestore.getInstance().collection("/users")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        me = documentSnapshot.toObject(User.class);
                        mostrarMensagens();

                    }
                });


    }

    //Método que irá exibir as mensagens na tela
    private void mostrarMensagens() {
        if (me != null) {

            String fromId = me.getUuid();
            String toId = user.getUuid();

            FirebaseFirestore.getInstance().collection("/conversations")
                    .document(fromId)
                    .collection(toId)
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();
                            if (documentChanges != null) {

                                for (DocumentChange doc : documentChanges) {

                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                        Message message = doc.getDocument().toObject(Message.class);
                                        adapter.add(new MessageItem(message));


                                    }

                                }
                            }

                        }
                    });


        }


    }

    //Método para enviar mensagem
    private void enviarMensagens() {

        String text = editChat.getText().toString();

        editChat.setText(null);

        final String fromId = FirebaseAuth.getInstance().getUid();
        final String toId = user.getUuid();



        long  timestamp = tempo.getSeconds();;
        
        final Message message = new Message();
        message.setFromId(fromId);
        message.setToId(toId);
        message.setTimestamp(timestamp);
        message.setText(text);

        //registrar mensagem no firebase

        if (!message.getText().isEmpty()) {
            FirebaseFirestore.getInstance().collection("/conversations")
                    .document(fromId)
                    .collection(toId)
                    .add(message)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("Teste", documentReference.getId());

                            Contato contato = new Contato(toId, user.getUsername(), message.getText(), message.getTimestamp(), user.getProfileUrl(),user);

                            FirebaseFirestore.getInstance().collection("ultimas-mensagens")
                                    .document(fromId)
                                    .collection("contatos")
                                    .document(toId)
                                    .set(contato);


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Teste", e.getMessage(), e);
                        }
                    });

            //Duplicando funcionalidade para receber e salvar mensagens do remetente
            FirebaseFirestore.getInstance().collection("/conversations")
                    .document(toId)
                    .collection(fromId)
                    .add(message)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("Teste", documentReference.getId());

                            Contato contato = new Contato(fromId, me.getUsername(), message.getText(), message.getTimestamp(), me.getProfileUrl(),me);

                            FirebaseFirestore.getInstance().collection("ultimas-mensagens")
                                    .document(toId)
                                    .collection("contatos")
                                    .document(fromId)
                                    .set(contato);


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Teste", e.getMessage(), e);
                        }
                    });

        }

    }

    public void enviar(View view) {

        enviarMensagens();
    }


    //Método que irá posicionar a mensagem na posição
    private class MessageItem extends Item<ViewHolder> {


        private final Message message;

        private MessageItem(Message message) {
            this.message = message;
        }


        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {

            TextView txtMsg = viewHolder.itemView.findViewById(R.id.txt_msg); //Definindo Texto
            ImageView imgMessage = viewHolder.itemView.findViewById(R.id.img_message_user); //Definindo Foto

            //Pega a imagem e texto  dos usuários e envia para a tela de chat
            txtMsg.setText(message.getText());
            //--> Verifica se a mensagem é da pessoa que esta logada ou ro remetente para mostrar a foto da pessoa ou rementente
            if (message.getFromId().equals(FirebaseAuth.getInstance().getUid())) {
                if(me.getProfileUrl()==null){
                    Picasso.get().load(R.drawable.avatarpadrao)
                            .into(imgMessage);
                }else{
                    Picasso.get().load(me.getProfileUrl())
                            .into(imgMessage);
                }


            } else {
                if(user.getProfileUrl()==null){
                    Picasso.get().load(R.drawable.avatarpadrao)
                            .into(imgMessage);
                }else{
                    Picasso.get().load(user.getProfileUrl())
                            .into(imgMessage);
                }
            }

        }

        @Override
        public int getLayout() {

            //Válida e retorna a mensagem  na posição de quem enviou (Remetente/Receptor)
            return message.getFromId().equals(FirebaseAuth.getInstance().getUid()) //--> Verifica se a mensagem é da pessoa que esta logada ou ro remetente
                    ? R.layout.item_from_message
                    : R.layout.item_to_message;
        }
    }
}
