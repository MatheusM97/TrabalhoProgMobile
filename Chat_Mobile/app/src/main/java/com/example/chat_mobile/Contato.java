package com.example.chat_mobile;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Contato {
    private String uuid;
    private String nomeUsuario;
    private String ultimaMensagem;
    private long timeStamp;
    private String fotoURL;
    private User user;
    public Contato() {
            //atualizarUser();
    }
    public Contato(String uuid, String nomeUsuario, String ultimaMensagem, long timeStamp, String fotoURL,User user) {
        this.uuid = uuid;
        this.nomeUsuario = nomeUsuario;
        this.ultimaMensagem = ultimaMensagem;
        this.timeStamp = timeStamp;
        this.fotoURL = fotoURL;
        this.user = user;
    }

    public Contato(String uuid, String nomeUsuario, String ultimaMensagem, long timeStamp, String fotoURL) {
        this.uuid = uuid;
        this.nomeUsuario = nomeUsuario;
        this.ultimaMensagem = ultimaMensagem;
        this.timeStamp = timeStamp;
        this.fotoURL = fotoURL;
    }

    public Contato(String uuid, String ultimaMensagem, long timeStamp, String fotoURL) {
        this.uuid = uuid;
        this.ultimaMensagem = ultimaMensagem;
        this.timeStamp = timeStamp;
        this.fotoURL = fotoURL;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getUltimaMensagem() {
        return ultimaMensagem;
    }

    public void setUltimaMensagem(String ultimaMensagem) {
        this.ultimaMensagem = ultimaMensagem;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getFotoURL() {
        return fotoURL;
    }

    public void setFotoURL(String fotoURL) {
        this.fotoURL = fotoURL;
    }

    public void atualizarUser(){

        FirebaseFirestore.getInstance().collection("users")
                .document(uuid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {


                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                    user = documentSnapshot.toObject(User.class);
                        System.out.println(user.getProfileUrl());

                    }
                });
        System.out.println(user.getProfileUrl());

        //return user;
    }
}
