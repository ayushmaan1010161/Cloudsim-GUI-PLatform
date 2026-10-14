package com.cloudsim.gui;

import javafx.application.Application;

/**
 * Main
 *
 * Entry point for the CloudSim GUI Platform.
 *
 * JavaFX requires Application.launch() to be called from a class
 * that does NOT extend Application when running without a module-info.
 * So Main stays plain and delegates to SimulationApp.
 */
public class Main {
    public static void main(String[] args) {
        Application.launch(SimulationApp.class, args);
    }
}