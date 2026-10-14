package com.cloudsim.gui;

import com.cloudsim.gui.ui.AppLayout;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * SimulationApp  (Step 6 - Professional GUI Redesign)
 *
 * Now a thin launcher. All UI logic lives in com.cloudsim.gui.ui.*
 * The simulation backend (BasicSimulation, MainController, models)
 * is completely unchanged.
 */
public class SimulationApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        AppLayout root = new AppLayout();
        Scene scene = new Scene(root, 1100, 720);
        primaryStage.setTitle("CloudSim GUI Platform");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(820);
        primaryStage.setMinHeight(580);
        primaryStage.show();
    }
}