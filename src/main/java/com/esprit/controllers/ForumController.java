package com.esprit.controllers;

import com.esprit.entities.Commentaire;
import com.esprit.entities.Publication;
import com.esprit.services.CommentaireServices;
import com.esprit.services.PublicationServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;

import java.awt.*;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class ForumController implements Initializable {

    // ── Top-level layout ──────────────────────────────────────────────────────
    @FXML private ScrollPane feedScroll;
    @FXML private VBox      feedWrapper;

    // ── Composer card ─────────────────────────────────────────────────────────
    @FXML private TextField tfPubTitre;
    @FXML private TextField tfPubType;
    @FXML private TextField tfPubLien;
    @FXML private TextArea  taPubContenu;
    @FXML private Label lblPubMessage;

    // ── Hidden edit state ─────────────────────────────────────────────────────
    private Publication editingPublication = null;

    // ── Services / data ───────────────────────────────────────────────────────
    private final PublicationServices  pubServices = new PublicationServices();
    private final CommentaireServices  comServices = new CommentaireServices();
    private final DateTimeFormatter    fmt         = DateTimeFormatter.ofPattern("dd MMM yyyy · HH:mm");

    // =========================================================================
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        feedScroll.setFitToWidth(true);
        feedScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chargerFeed();
    }

    // ── Public handlers (bound by FXML) ──────────────────────────────────────

    @FXML
    private void handlePublier() {
        String titre   = tfPubTitre.getText().trim();
        String type    = tfPubType.getText().trim();
        String contenu = taPubContenu.getText().trim();

        if (titre.isEmpty() || type.isEmpty() || contenu.isEmpty()) {
            msg("Titre, type et contenu sont obligatoires.", lblPubMessage, false);
            return;
        }

        try {
            if (editingPublication == null) {
                // Create new
                Publication p = new Publication(titre, type, tfPubLien.getText().trim(), contenu, LocalDateTime.now());
                pubServices.ajouter(p);
                msg("Publication ajoutée !", lblPubMessage, true);
            } else {
                // Update existing
                Publication p = new Publication(
                        editingPublication.getId(), titre, type,
                        tfPubLien.getText().trim(), contenu,
                        LocalDateTime.now(),
                        editingPublication.getLikes(), editingPublication.getDislikes()
                );
                pubServices.modifier(p);
                msg("Publication modifiée !", lblPubMessage, true);
                editingPublication = null;
            }
            viderComposer();
            chargerFeed();
        } catch (SQLException e) {
            msg("Erreur : " + e.getMessage(), lblPubMessage, false);
        }
    }

    @FXML
    private void handleAnnulerEdit() {
        editingPublication = null;
        viderComposer();
        msg("", lblPubMessage, true);
    }

    // ── Feed builder ──────────────────────────────────────────────────────────

    private void chargerFeed() {
        feedWrapper.getChildren().clear();
        try {
            List<Publication> pubs = pubServices.afficher();
            for (Publication p : pubs) {
                feedWrapper.getChildren().add(buildPostCard(p));
            }
        } catch (SQLException e) {
            msg("Erreur chargement : " + e.getMessage(), lblPubMessage, false);
        }
    }

    // ── Post card ─────────────────────────────────────────────────────────────

    private VBox buildPostCard(Publication pub) {
        VBox card = new VBox();
        card.getStyleClass().add("post-card");
        card.setSpacing(0);

        // ── Header row ───────────────────────────────────────────────────────
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 16, 10, 16));

        // Avatar circle
        StackPane avatar = makeAvatar("LU");

        VBox authorInfo = new VBox(2);
        Label authorName = new Label("LinguaLearn User");
        authorName.getStyleClass().add("post-author");
        Label metaLbl = new Label(
                safe(pub.getTypePub()) + "  ·  " +
                        (pub.getDatePub() == null ? "Date inconnue" : pub.getDatePub().format(fmt))
        );
        metaLbl.getStyleClass().add("post-meta");
        authorInfo.getChildren().addAll(authorName, metaLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // ⋯ menu button
        MenuButton moreBtn = new MenuButton("•••");
        moreBtn.getStyleClass().add("btn-more");
        MenuItem editItem   = new MenuItem("✏  Modifier");
        MenuItem deleteItem = new MenuItem("🗑  Supprimer");
        editItem.setOnAction(e -> startEditPublication(pub));
        deleteItem.setOnAction(e -> supprimerPublication(pub));
        moreBtn.getItems().addAll(editItem, deleteItem);

        header.getChildren().addAll(avatar, authorInfo, spacer, moreBtn);

        // ── Body ─────────────────────────────────────────────────────────────
        VBox body = new VBox(6);
        body.setPadding(new Insets(0, 16, 10, 16));

        Label titleLbl = new Label(safe(pub.getTitrePub()));
        titleLbl.getStyleClass().add("post-title");
        titleLbl.setWrapText(true);

        Label contentLbl = new Label(safe(pub.getContenuPub()));
        contentLbl.getStyleClass().add("post-content");
        contentLbl.setWrapText(true);

        body.getChildren().addAll(titleLbl, contentLbl);

        String url = safe(pub.getLienPub());
        if (!url.isEmpty()) {
            Hyperlink link = new Hyperlink(url);
            link.getStyleClass().add("post-link");
            body.getChildren().add(link);
        }

        // ── Reaction row ─────────────────────────────────────────────────────
        HBox reactions = new HBox(4);
        reactions.setPadding(new Insets(4, 16, 6, 16));
        reactions.setAlignment(Pos.CENTER_LEFT);

        Label likeCountLbl = new Label();
        likeCountLbl.getStyleClass().add("reaction-count");
        refreshReactionCount(likeCountLbl, pub);

        reactions.getChildren().add(likeCountLbl);

        // ── Divider ──────────────────────────────────────────────────────────
        Separator sep = new Separator();
        sep.getStyleClass().add("post-sep");
        sep.setPadding(new Insets(0, 16, 0, 16));

        // ── Action bar ───────────────────────────────────────────────────────
        HBox actions = new HBox();
        actions.setPadding(new Insets(2, 8, 2, 8));
        actions.setAlignment(Pos.CENTER);

        Button likeBtn    = makeActionBtn("👍", "J'aime");
        Button dislikeBtn = makeActionBtn("👎", "Je n'aime pas");

        // Comment toggle
        VBox commentSection = buildCommentSection(pub);
        commentSection.setVisible(false);
        commentSection.setManaged(false);

        Button commentBtn = makeActionBtn("💬", "Commenter");
        commentBtn.setOnAction(e -> {
            boolean nowVisible = !commentSection.isVisible();
            commentSection.setVisible(nowVisible);
            commentSection.setManaged(nowVisible);
            if (nowVisible) reloadComments(pub, commentSection);
        });

        likeBtn.setOnAction(e -> {
            try {
                pub.setLikes(pub.getLikes() + 1);
                pubServices.modifier(pub);
                refreshReactionCount(likeCountLbl, pub);
            } catch (SQLException ex) { /* silent */ }
        });

        dislikeBtn.setOnAction(e -> {
            try {
                pub.setDislikes(pub.getDislikes() + 1);
                pubServices.modifier(pub);
                refreshReactionCount(likeCountLbl, pub);
            } catch (SQLException ex) { /* silent */ }
        });

        for (Button b : new Button[]{likeBtn, dislikeBtn, commentBtn}) {
            HBox.setHgrow(b, Priority.ALWAYS);
            b.setMaxWidth(Double.MAX_VALUE);
        }
        actions.getChildren().addAll(likeBtn, dislikeBtn, commentBtn);

        card.getChildren().addAll(header, body, reactions, sep, actions, commentSection);
        return card;
    }

    // ── Comment section ───────────────────────────────────────────────────────

    private VBox buildCommentSection(Publication pub) {
        VBox section = new VBox(10);
        section.getStyleClass().add("comment-section");
        section.setPadding(new Insets(10, 16, 14, 16));

        // Comment list placeholder
        VBox commentList = new VBox(8);
        commentList.setId("commentList-" + pub.getId());

        // Composer row
        HBox compRow = new HBox(8);
        compRow.setAlignment(Pos.CENTER_LEFT);

        StackPane miniAvatar = makeAvatar("M");
        miniAvatar.setPrefSize(32, 32);
        miniAvatar.setMinSize(32, 32);

        TextField comField = new TextField();
        comField.setPromptText("Ajouter un commentaire…");
        comField.getStyleClass().add("comment-input");
        HBox.setHgrow(comField, Priority.ALWAYS);

        Button sendBtn = new Button("Publier");
        sendBtn.getStyleClass().addAll("btn", "btn-primary");
        sendBtn.setPadding(new Insets(6, 14, 6, 14));

        sendBtn.setOnAction(e -> {
            String text = comField.getText().trim();
            if (text.isEmpty()) return;
            try {
                Commentaire c = new Commentaire(text, LocalDateTime.now(), pub.getId());
                comServices.ajouter(c);
                comField.clear();
                reloadComments(pub, section);
            } catch (SQLException ex) { /* silent */ }
        });

        comField.setOnAction(e -> sendBtn.fire());

        compRow.getChildren().addAll(miniAvatar, comField, sendBtn);
        section.getChildren().addAll(commentList, compRow);
        return section;
    }

    private void reloadComments(Publication pub, VBox section) {
        VBox commentList = null;
        for (javafx.scene.Node n : section.getChildren()) {
            if (n instanceof VBox && (pub.getId() + "").equals(((VBox) n).getId() != null
                    ? ((VBox) n).getId().replace("commentList-", "") : "")) {
                commentList = (VBox) n;
                break;
            }
            // fallback: first VBox child
            if (n instanceof VBox) {
                commentList = (VBox) n;
                break;
            }
        }
        if (commentList == null) return;

        commentList.getChildren().clear();
        try {
            List<Commentaire> coms = comServices.afficherParPublication(pub.getId());
            for (Commentaire c : coms) {
                commentList.getChildren().add(buildCommentRow(c, pub, section));
            }
        } catch (SQLException e) { /* silent */ }
    }

    private HBox buildCommentRow(Commentaire com, Publication pub, VBox section) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.TOP_LEFT);

        StackPane avatar = makeAvatar("U");
        avatar.setPrefSize(32, 32);
        avatar.setMinSize(32, 32);

        VBox bubble = new VBox(3);
        bubble.getStyleClass().add("comment-bubble");
        HBox.setHgrow(bubble, Priority.ALWAYS);

        Label head = new Label("Utilisateur  ·  " +
                (com.getDateCom() == null ? "Date inconnue" : com.getDateCom().format(fmt)));
        head.getStyleClass().add("comment-meta");

        Label body = new Label(safe(com.getContenuC()));
        body.getStyleClass().add("comment-body");
        body.setWrapText(true);

        bubble.getChildren().addAll(head, body);

        // Edit / delete mini-menu
        MenuButton cmenu = new MenuButton("•••");
        cmenu.getStyleClass().add("btn-more-sm");
        MenuItem cedit   = new MenuItem("Modifier");
        MenuItem cdelete = new MenuItem("Supprimer");

        cedit.setOnAction(e -> {
            TextInputDialog dlg = new TextInputDialog(com.getContenuC());
            dlg.setHeaderText("Modifier le commentaire");
            dlg.setContentText("Contenu :");
            dlg.showAndWait().ifPresent(newText -> {
                if (!newText.trim().isEmpty()) {
                    try {
                        com.setContenuC(newText.trim());
                        com.setDateCom(LocalDateTime.now());
                        comServices.modifier(com);
                        reloadComments(pub, section);
                    } catch (SQLException ex) { /* silent */ }
                }
            });
        });

        cdelete.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer ce commentaire ?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(r -> {
                if (r == ButtonType.YES) {
                    try {
                        comServices.supprimer(com.getId());
                        reloadComments(pub, section);
                    } catch (SQLException ex) { /* silent */ }
                }
            });
        });

        cmenu.getItems().addAll(cedit, cdelete);

        row.getChildren().addAll(avatar, bubble, cmenu);
        return row;
    }

    // ── Edit/delete publication ───────────────────────────────────────────────

    private void startEditPublication(Publication pub) {
        editingPublication = pub;
        tfPubTitre.setText(safe(pub.getTitrePub()));
        tfPubType.setText(safe(pub.getTypePub()));
        tfPubLien.setText(safe(pub.getLienPub()));
        taPubContenu.setText(safe(pub.getContenuPub()));
        msg("Mode édition activé — modifiez puis cliquez Publier.", lblPubMessage, true);
        feedScroll.setVvalue(0); // scroll to top so composer is visible
    }

    private void supprimerPublication(Publication pub) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer cette publication ?", ButtonType.YES, ButtonType.NO);
        a.showAndWait().ifPresent(r -> {
            if (r == ButtonType.YES) {
                try {
                    pubServices.supprimer(pub.getId());
                    chargerFeed();
                } catch (SQLException e) {
                    msg("Erreur : " + e.getMessage(), lblPubMessage, false);
                }
            }
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private StackPane makeAvatar(String initials) {
        Circle circle = new Circle(20, Color.web("#0a66c2"));
        Label label = new Label(initials);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: 700;");
        StackPane pane = new StackPane(circle, label);
        pane.setPrefSize(40, 40);
        pane.setMinSize(40, 40);
        return pane;
    }

    private Button makeActionBtn(String icon, String text) {
        Button btn = new Button(icon + "  " + text);
        btn.getStyleClass().addAll("btn", "btn-action");
        return btn;
    }

    private void refreshReactionCount(Label lbl, Publication pub) {
        lbl.setText("👍 " + pub.getLikes() + "   👎 " + pub.getDislikes());
    }

    private void viderComposer() {
        tfPubTitre.clear();
        tfPubType.clear();
        tfPubLien.clear();
        taPubContenu.clear();
        editingPublication = null;
    }

    private void msg(String text, Label lbl, boolean ok) {
        lbl.setText(text);
        lbl.setStyle(ok
                ? "-fx-text-fill: #057642; -fx-font-weight: 700;"
                : "-fx-text-fill: #b42318; -fx-font-weight: 700;");
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
