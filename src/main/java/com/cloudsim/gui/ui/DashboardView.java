package com.cloudsim.gui.ui;

import com.cloudsim.gui.model.CloudletResult;
import com.cloudsim.gui.model.SimulationConfig;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.List;
import java.util.function.Consumer;

/**
 * DashboardView
 * Landing page. Shows stat cards for the last simulation run
 * and a New Simulation button.
 */
public class DashboardView extends VBox {

    private final Consumer<String> nav;

    // ── Stat card labels ──────────────────────────────────────────────
    private Label valDC, valHosts, valVMs, valCloudlets;
    private Label valCompleted, valAvgTime, valTotalTime, valSuccessRate;
    private Label emptyNote;
    private VBox  resultsRow;

    public DashboardView(Consumer<String> nav) {
        this.nav = nav;
        setSpacing(20);
        setPadding(new Insets(28, 28, 28, 28));
        setStyle("-fx-background-color:" + Styles.BG_CONTENT + ";");
        build();
    }

    private void build() {
        // ── Page header ───────────────────────────────────────────────
        Label title = new Label("Dashboard");
        title.setStyle(Styles.FONT_TITLE + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");

        Label desc = new Label(
            "Configure, execute and analyze cloud computing simulations using CloudSim Plus.");
        desc.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_SECONDARY + ";");
        desc.setWrapText(true);

        // ── New Simulation button ─────────────────────────────────────
        Button newSimBtn = new Button("+ New Simulation");
        newSimBtn.setStyle(Styles.BTN_PRIMARY);
        newSimBtn.setOnMouseEntered(e -> newSimBtn.setStyle(Styles.BTN_PRIMARY_HOVER));
        newSimBtn.setOnMouseExited(e  -> newSimBtn.setStyle(Styles.BTN_PRIMARY));
        newSimBtn.setOnAction(e -> nav.accept("config"));

        HBox headerRow = new HBox(newSimBtn);
        headerRow.setAlignment(Pos.CENTER_RIGHT);

        VBox header = new VBox(6, title, desc, headerRow);

        // ── Config stat cards (always visible) ────────────────────────
        Label configLabel = new Label("CURRENT CONFIGURATION");
        configLabel.setStyle(Styles.SECTION_HEADER);

        valDC        = cardVal("—");
        valHosts     = cardVal("—");
        valVMs       = cardVal("—");
        valCloudlets = cardVal("—");

        HBox configRow = new HBox(12,
            statCard("Datacenters",  valDC,        Styles.ACCENT),
            statCard("Hosts",        valHosts,      "#8b5cf6"),
            statCard("VMs",          valVMs,        Styles.SUCCESS),
            statCard("Cloudlets",    valCloudlets,  Styles.WARNING)
        );
        configRow.setFillHeight(true);
        HBox.setHgrow(configRow.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(configRow.getChildren().get(1), Priority.ALWAYS);
        HBox.setHgrow(configRow.getChildren().get(2), Priority.ALWAYS);
        HBox.setHgrow(configRow.getChildren().get(3), Priority.ALWAYS);

        // ── Results stat cards (shown after simulation) ───────────────
        Label resultsLabel = new Label("LAST SIMULATION RESULTS");
        resultsLabel.setStyle(Styles.SECTION_HEADER);

        valCompleted   = cardVal("—");
        valAvgTime     = cardVal("—");
        valTotalTime   = cardVal("—");
        valSuccessRate = cardVal("—");

        HBox resRow = new HBox(12,
            statCard("Completed Cloudlets",  valCompleted,   Styles.SUCCESS),
            statCard("Avg Execution Time",   valAvgTime,     Styles.ACCENT),
            statCard("Total CPU Time",       valTotalTime,   "#8b5cf6"),
            statCard("Success Rate",         valSuccessRate, Styles.WARNING)
        );
        resRow.setFillHeight(true);
        for (int i = 0; i < 4; i++) HBox.setHgrow(resRow.getChildren().get(i), Priority.ALWAYS);

        emptyNote = new Label(
            "No simulation has been run yet. Configure your parameters and click New Simulation.");
        emptyNote.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_SECONDARY +
            ";-fx-font-style:italic;-fx-padding:8 0 0 0;");

        resultsRow = new VBox(8, resultsLabel, resRow, emptyNote);

        // ── Quick-start guide card ────────────────────────────────────
        VBox guide = buildGuideCard();

        getChildren().addAll(header, configLabel, configRow, resultsRow, guide);
    }

    private VBox buildGuideCard() {
        Label heading = new Label("Getting Started");
        heading.setStyle(Styles.FONT_H3 + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");

        String[] steps = {
            "1. Go to Configuration and set your Datacenter, Host, VM and Cloudlet parameters.",
            "2. Go to Simulation and click Run Simulation.",
            "3. View the Results table to see each cloudlet outcome.",
            "4. Open Visualization to see execution time charts.",
            "5. Open Summary for a complete overview of the run."
        };

        VBox stepsBox = new VBox(6);
        for (String s : steps) {
            Label l = new Label(s);
            l.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_SECONDARY + ";");
            l.setWrapText(true);
            stepsBox.getChildren().add(l);
        }

        VBox card = new VBox(10, heading, stepsBox);
        card.setPadding(new Insets(18));
        card.setStyle(Styles.CARD);
        return card;
    }

    /** Called by AppLayout after each successful simulation run. */
    public void updateStats(SimulationConfig cfg, List<CloudletResult> results) {
        // Config row
        valDC.setText(String.valueOf(cfg.getDatacenterCount()));
        valHosts.setText(String.valueOf(cfg.getHostCount()));
        valVMs.setText(String.valueOf(cfg.getVmCount()));
        valCloudlets.setText(String.valueOf(cfg.getCloudletCount()));

        // Results row
        long ok = results.stream()
            .filter(r -> "SUCCESS".equalsIgnoreCase(r.getStatus())).count();
        double avg   = results.stream().mapToDouble(CloudletResult::getExecutionTime).average().orElse(0);
        double total = results.stream().mapToDouble(CloudletResult::getExecutionTime).sum();
        double rate  = results.isEmpty() ? 0 : (ok * 100.0 / results.size());

        valCompleted.setText(ok + " / " + results.size());
        valAvgTime.setText(String.format("%.2f s", avg));
        valTotalTime.setText(String.format("%.2f s", total));
        valSuccessRate.setText(String.format("%.0f%%", rate));
        emptyNote.setVisible(false);
        emptyNote.setManaged(false);
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private HBox statCard(String title, Label valLabel, String accent) {
        Label titleLbl = new Label(title.toUpperCase());
        titleLbl.setStyle(Styles.FONT_CAPTION + "-fx-text-fill:" + Styles.TEXT_SECONDARY +
            ";-fx-font-weight:bold;");

        valLabel.setStyle("-fx-font-size:22px;-fx-font-weight:bold;-fx-text-fill:" +
            Styles.TEXT_PRIMARY + ";");

        VBox card = new VBox(6, titleLbl, valLabel);
        card.setPadding(new Insets(16, 16, 16, 20));
        card.setStyle(Styles.statCard(accent));
        HBox wrap = new HBox(card);
        HBox.setHgrow(card, Priority.ALWAYS);
        return wrap;
    }

    private Label cardVal(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:22px;-fx-font-weight:bold;-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");
        return l;
    }
}