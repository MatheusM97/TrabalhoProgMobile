package com.example.chat_mobile;


public class Contato {
    private String uuid;
    private String nomeUsuario;
    private String ultimaMensagem;
    private long timeStamp;
    private String fotoURL;

    public Contato() {

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
}
