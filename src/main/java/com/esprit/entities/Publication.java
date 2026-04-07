package com.esprit.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Publication {
    private int id;
    private String titrePub;
    private String typePub;
    private String lienPub;
    private String contenuPub;
    private LocalDateTime datePub;
    private int likes;
    private int dislikes;
    private Integer reportPub;
    private Integer floue;
    private List<Commentaire> commentaires;

    // Constructeurs
    public Publication() {
        this.commentaires = new ArrayList<>();
        this.likes = 0;
        this.dislikes = 0;
    }

    public Publication(String titrePub, String typePub, String lienPub, String contenuPub, LocalDateTime datePub) {
        this();
        this.titrePub = titrePub;
        this.typePub = typePub;
        this.lienPub = lienPub;
        this.contenuPub = contenuPub;
        this.datePub = datePub;
    }

    public Publication(int id, String titrePub, String typePub, String lienPub, String contenuPub, LocalDateTime datePub, int likes, int dislikes) {
        this();
        this.id = id;
        this.titrePub = titrePub;
        this.typePub = typePub;
        this.lienPub = lienPub;
        this.contenuPub = contenuPub;
        this.datePub = datePub;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitrePub() {
        return titrePub;
    }

    public void setTitrePub(String titrePub) {
        this.titrePub = titrePub;
    }

    public String getTypePub() {
        return typePub;
    }

    public void setTypePub(String typePub) {
        this.typePub = typePub;
    }

    public String getLienPub() {
        return lienPub;
    }

    public void setLienPub(String lienPub) {
        this.lienPub = lienPub;
    }

    public String getContenuPub() {
        return contenuPub;
    }

    public void setContenuPub(String contenuPub) {
        this.contenuPub = contenuPub;
    }

    public LocalDateTime getDatePub() {
        return datePub;
    }

    public void setDatePub(LocalDateTime datePub) {
        this.datePub = datePub;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public Integer getReportPub() {
        return reportPub;
    }

    public void setReportPub(Integer reportPub) {
        this.reportPub = reportPub;
    }

    public Integer getFloue() {
        return floue;
    }

    public void setFloue(Integer floue) {
        this.floue = floue;
    }

    public List<Commentaire> getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(List<Commentaire> commentaires) {
        this.commentaires = commentaires;
    }

    @Override
    public String toString() {
        return "Publication{" +
                "id=" + id +
                ", titrePub='" + titrePub + '\'' +
                ", typePub='" + typePub + '\'' +
                ", contenuPub='" + (contenuPub != null && contenuPub.length() > 50 ? contenuPub.substring(0, 50) + "..." : contenuPub) + '\'' +
                ", datePub=" + datePub +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                '}';
    }
}

