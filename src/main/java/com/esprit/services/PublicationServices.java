package com.esprit.services;

import com.esprit.entities.Publication;
import com.esprit.utils.MyDataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PublicationServices implements ICrud<Publication> {
    private Connection con;

    public PublicationServices() {
        con = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Publication publication) throws SQLException {
        String sql = "INSERT INTO publication (titre_pub, type_pub, lien_pub, contenu_pub, date_pub, likes, dislikes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, publication.getTitrePub());
        pst.setString(2, publication.getTypePub());
        pst.setString(3, publication.getLienPub());
        pst.setString(4, publication.getContenuPub());
        pst.setTimestamp(5, Timestamp.valueOf(publication.getDatePub()));
        pst.setInt(6, publication.getLikes());
        pst.setInt(7, publication.getDislikes());
        pst.executeUpdate();
        System.out.println("✅ Publication ajoutée avec succès !");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM publication WHERE id = ?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, id);
        pst.executeUpdate();
        System.out.println("✅ Publication supprimée !");
    }

    @Override
    public List<Publication> afficher() throws SQLException {
        List<Publication> publications = new ArrayList<>();
        String sql = "SELECT * FROM publication ORDER BY date_pub DESC";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            Publication pub = new Publication();
            pub.setId(rs.getInt("id"));
            pub.setTitrePub(rs.getString("titre_pub"));
            pub.setTypePub(rs.getString("type_pub"));
            pub.setLienPub(rs.getString("lien_pub"));
            pub.setContenuPub(rs.getString("contenu_pub"));
            pub.setDatePub(rs.getTimestamp("date_pub").toLocalDateTime());
            pub.setLikes(rs.getInt("likes"));
            pub.setDislikes(rs.getInt("dislikes"));
            publications.add(pub);
        }
        return publications;
    }

    @Override
    public void modifier(Publication publication) throws SQLException {
        String sql = "UPDATE publication SET titre_pub = ?, type_pub = ?, lien_pub = ?, contenu_pub = ?, date_pub = ?, likes = ?, dislikes = ? WHERE id = ?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, publication.getTitrePub());
        pst.setString(2, publication.getTypePub());
        pst.setString(3, publication.getLienPub());
        pst.setString(4, publication.getContenuPub());
        pst.setTimestamp(5, Timestamp.valueOf(publication.getDatePub()));
        pst.setInt(6, publication.getLikes());
        pst.setInt(7, publication.getDislikes());
        pst.setInt(8, publication.getId());
        pst.executeUpdate();
        System.out.println("✅ Publication modifiée !");
    }
}

