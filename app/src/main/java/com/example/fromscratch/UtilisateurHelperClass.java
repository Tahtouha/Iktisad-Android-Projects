package com.example.fromscratch;

import java.util.ArrayList;

public class UtilisateurHelperClass {

  private String password;
  private String username;

  public UtilisateurHelperClass(String password, String username) {
    this.password = password;
    this.username = username;
  }

  public UtilisateurHelperClass() {

  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}