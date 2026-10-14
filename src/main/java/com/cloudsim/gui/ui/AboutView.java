package com.cloudsim.gui.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * AboutView - Project information page.
 */
public class AboutView extends VBox {

    public AboutView() {
        setSpacing(18);
        setPadding(new Insets(28));
        setStyle("-fx-background-color:" + Styles.BG_CONTENT + ";");
        build();
    }

    private void build() {
        Label title = new Label("About");
        title.setStyle(Styles.FONT_TITLE + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");

        VBox card1 = infoCard("CloudSim GUI Platform",
            "A beginner-oriented desktop application for configuring and running CloudSim Plus " +
            "cloud computing simulations without writing Java code.\n\n" +
            "Built as a B.Tech Capstone Project.");

        VBox card2 = infoCard("Technology Stack",
            "Simulation Engine : CloudSim Plus 8.0.0\n" +
            "GUI Framework     : JavaFX 21.0.2\n" +
            "Build Tool        : Apache Maven 3.9.6\n" +
            "Language          : Java 22\n" +
            "IDE               : Antigravity IDE");

        VBox card3 = infoCard("Architecture",
            "model/           SimulationConfig, CloudletResult\n" +
            "simulation/      BasicSimulation (CloudSim Plus engine)\n" +
            "controller/      MainController (bridge)\n" +
            "ui/              AppLayout, views, Styles\n\n" +
            "The GUI and simulation engine are fully separated.\n" +
            "The backend can be tested independently of the UI.");

        VBox card4 = infoCard("How to Use",
            "1. Go to Configuration and set your parameters.\n" +
            "2. Click Save & Go to Simulation.\n" +
            "3. Click Run Simulation.\n" +
            "4. View Results for the cloudlet table.\n" +
            "5. View Visualization for execution charts.\n" +
            "6. View Summary for a complete overview.");

        getChildren().addAll(title, card1, card2, card3, card4);
    }

    private VBox infoCard(String heading, String body) {
        Label h = new Label(heading);
        h.setStyle(Styles.FONT_H3 + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");
        Label b = new Label(body);
        b.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_SECONDARY + ";");
        b.setWrapText(true);
        VBox card = new VBox(8, h, b);
        card.setPadding(new Insets(18));
        card.setStyle(Styles.CARD);
        return card;
    }
}