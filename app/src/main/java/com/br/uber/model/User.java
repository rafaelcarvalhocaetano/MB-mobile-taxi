package com.br.uber.model;

/**
 * Created by rafael on 28/05/18.
 */

public class User {


    private String nome;
    private String email;
    private String password;
    private String phone;


    public User(){

    }

    public User(String nome, String email, String password, String phone){

        this.nome = nome;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
