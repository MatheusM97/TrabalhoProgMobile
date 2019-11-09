package com.example.chat_mobile;


//Clase User que é utilizada para gerenciar os dados do usuário no banco de dados do
//firestore

public class User {


    //Variaveis do usuário
    private final String uuid;
    private  final String username;
    private final String profileUrl;

    //Contrutor do usuário
    public User(String uuid, String username, String profileUrl) {
        this.uuid = uuid;
        this.username = username;
        this.profileUrl = profileUrl;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getProfileUrl() {
        return profileUrl;
    }
}
