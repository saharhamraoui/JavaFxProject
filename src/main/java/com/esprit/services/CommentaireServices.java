package com.esprit.services;

import com.esprit.entities.Commentaire;
import com.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentaireServices implements ICrud<Commentaire> {
    private Connection con;

    public CommentaireServices() {
        con = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Commentaire commentaire) throws SQLException {
        String sql = "INSERT INTO commentaire (contenu_c, date_com, publication_id) VALUES (?, ?, ?)";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, commentaire.getContenuC());
        pst.setTimestamp(2, Timestamp.valueOf(commentaire.getDateCom()));
        pst.setInt(3, commentaire.getPublicationId());
        pst.executeUpdate();
        System.out.println("✅ Commentaire ajouté avec succès !");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM commentaire WHERE id = ?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, id);
        pst.executeUpdate();
        System.out.println("✅ Commentaire supprimé !");
    }

    @Override
    public List<Commentaire> afficher() throws SQLException {
        List<Commentaire> commentaires = new ArrayList<>();
        String sql = "SELECT * FROM commentaire ORDER BY date_com DESC";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            Commentaire com = new Commentaire();
            com.setId(rs.getInt("id"));
            com.setContenuC(rs.getString("contenu_c"));
            com.setDateCom(rs.getTimestamp("date_com").toLocalDateTime());
            com.setPublicationId(rs.getInt("publication_id"));
            commentaires.add(com);
        }
        return commentaires;
    }

    public List<Commentaire> afficherParPublication(int publicationId) throws SQLException {
        List<Commentaire> commentaires = new ArrayList<>();
        String sql = "SELECT * FROM commentaire WHERE publication_id = ? ORDER BY date_com DESC";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, publicationId);
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            Commentaire com = new Commentaire();
            com.setId(rs.getInt("id"));
            com.setContenuC(rs.getString("contenu_c"));
            com.setDateCom(rs.getTimestamp("date_com").toLocalDateTime());
            com.setPublicationId(rs.getInt("publication_id"));
            commentaires.add(com);
        }
        return commentaires;
    }

    @Override
    public void modifier(Commentaire commentaire) throws SQLException {
        String sql = "UPDATE commentaire SET contenu_c = ?, date_com = ? WHERE id = ?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, commentaire.getContenuC());
        pst.setTimestamp(2, Timestamp.valueOf(commentaire.getDateCom()));
        pst.setInt(3, commentaire.getId());
        pst.executeUpdate();
        System.out.println("✅ Commentaire modifié !");
    }
}

