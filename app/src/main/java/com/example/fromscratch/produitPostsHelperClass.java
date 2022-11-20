package com.example.fromscratch;

public class produitPostsHelperClass {

    private String type;
    private String quant;
    private String jour;
    private  String marque;
    private String prix;


    public produitPostsHelperClass() {
    }

    public produitPostsHelperClass(String type, String quant, String jour, String marque, String prix) {
        this.type = type;
        this.quant = quant;
        this.jour = jour;
        this.marque = marque;
        this.prix = prix;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuant() {
        return quant;
    }

    public void setQuant(String quant) {
        this.quant = quant;
    }

    public String getJour() {
        return jour;
    }

    public void setJour(String jour) {
        this.jour = jour;
    }

    public String getPrix() {
        return prix;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }
}