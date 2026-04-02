package com.example.examsystemproject;

import java.io.IOException;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AboutUsController {

    @FXML
    private VBox contentCard;

    @FXML
    private VBox valuesBox;

    @FXML
    private Circle glowOne;

    @FXML
    private Circle glowTwo;

    @FXML
    private Circle glowThree;

    @FXML
    public void initialize() {
        playEntranceAnimation();
        playBackgroundFloatAnimation();
    }

    private void playEntranceAnimation() {
        contentCard.setOpacity(0);
        contentCard.setTranslateY(35);

        valuesBox.setOpacity(0);
        valuesBox.setTranslateY(25);

        FadeTransition cardFade = new FadeTransition(Duration.millis(600), contentCard);
        cardFade.setFromValue(0);
        cardFade.setToValue(1);

        TranslateTransition cardRise = new TranslateTransition(Duration.millis(600), contentCard);
        cardRise.setFromY(35);
        cardRise.setToY(0);

        ScaleTransition cardPop = new ScaleTransition(Duration.millis(600), contentCard);
        cardPop.setFromX(0.97);
        cardPop.setFromY(0.97);
        cardPop.setToX(1.0);
        cardPop.setToY(1.0);

        ParallelTransition cardIntro = new ParallelTransition(cardFade, cardRise, cardPop);

        FadeTransition valuesFade = new FadeTransition(Duration.millis(550), valuesBox);
        valuesFade.setFromValue(0);
        valuesFade.setToValue(1);

        TranslateTransition valuesRise = new TranslateTransition(Duration.millis(550), valuesBox);
        valuesRise.setFromY(25);
        valuesRise.setToY(0);

        new SequentialTransition(cardIntro, new ParallelTransition(valuesFade, valuesRise)).play();
    }

    private void playBackgroundFloatAnimation() {
        animateGlow(glowOne, -10, 9, 6200, 1.0, 1.06);
        animateGlow(glowTwo, 12, -8, 7100, 0.96, 1.03);
        animateGlow(glowThree, -7, -11, 6800, 0.98, 1.05);
    }

    private void animateGlow(Circle glow, double toX, double toY, int durationMs, double fromScale, double toScale) {
        TranslateTransition drift = new TranslateTransition(Duration.millis(durationMs), glow);
        drift.setFromX(0);
        drift.setFromY(0);
        drift.setToX(toX);
        drift.setToY(toY);
        drift.setCycleCount(TranslateTransition.INDEFINITE);
        drift.setAutoReverse(true);

        ScaleTransition breathe = new ScaleTransition(Duration.millis(durationMs), glow);
        breathe.setFromX(fromScale);
        breathe.setFromY(fromScale);
        breathe.setToX(toScale);
        breathe.setToY(toScale);
        breathe.setCycleCount(ScaleTransition.INDEFINITE);
        breathe.setAutoReverse(true);

        FadeTransition shimmer = new FadeTransition(Duration.millis(durationMs), glow);
        shimmer.setFromValue(0.26);
        shimmer.setToValue(0.42);
        shimmer.setCycleCount(FadeTransition.INDEFINITE);
        shimmer.setAutoReverse(true);

        new ParallelTransition(drift, breathe, shimmer).play();
    }

    @FXML
    public void onBackToDashboard(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DashBoard.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
