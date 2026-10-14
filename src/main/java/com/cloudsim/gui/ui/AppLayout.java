package com.cloudsim.gui.ui;

import com.cloudsim.gui.controller.MainController;
import com.cloudsim.gui.experiment.Experiment;
import com.cloudsim.gui.experiment.ExperimentStore;
import com.cloudsim.gui.model.CloudletResult;
import com.cloudsim.gui.model.SimulationConfig;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.util.List;
import java.util.function.Consumer;

/**
 * AppLayout  (Step 7 - Experiment Management)
 *
 * Added in Step 7:
 *   - ExperimentHistoryView, ExperimentDetailView, ExperimentCompareView
 *   - After each successful simulation, auto-saves an Experiment to ExperimentStore
 *   - 3 new sidebar nav entries: History, Compare, (detail is navigated internally)
 */
public class AppLayout extends BorderPane {

    // ── Shared simulation state ───────────────────────────────────────
    private final MainController       controller = new MainController();
    private       SimulationConfig     lastConfig  = null;
    private       List<CloudletResult> lastResults = null;

    // ── Existing views ────────────────────────────────────────────────
    private final DashboardView     dashboardView;
    private final ConfigView        configView;
    private final SimulationView    simulationView;
    private final ResultsView       resultsView;
    private final VisualizationView vizView;
    private final SummaryView       summaryView;
    private final AboutView         aboutView;

    // ── Step 7: Experiment views ──────────────────────────────────────
    private final ExperimentHistoryView historyView;
    private final ExperimentDetailView  detailView;
    private final ExperimentCompareView compareView;
    private final SensitivityView       sensitivityView;

    // ── Sidebar state ─────────────────────────────────────────────────
    private Button activeNavBtn = null;

    public AppLayout() {
        // Existing views
        dashboardView  = new DashboardView(this::navigateTo);
        configView     = new ConfigView(this::onConfigReady);
        simulationView = new SimulationView(this::onRunRequested, this::navigateTo);
        resultsView    = new ResultsView();
        vizView        = new VisualizationView();
        summaryView    = new SummaryView();
        aboutView      = new AboutView();

        // Step 7 views
        historyView = new ExperimentHistoryView(
            this::navigateTo,
            this::openExperimentDetail,
            this::openComparison);
        detailView  = new ExperimentDetailView(this::navigateTo);
        compareView = new ExperimentCompareView(this::navigateTo);
        sensitivityView = new SensitivityView(() -> lastConfig);

        setLeft(buildSidebar());
        showView(dashboardView);
    }

    // ── Navigation ────────────────────────────────────────────────────

    public void navigateTo(String page) {
        switch (page) {
            case "dashboard"    -> showView(dashboardView);
            case "config"       -> showView(configView);
            case "simulation"   -> {
                simulationView.refreshConfigSummary(lastConfig);
                showView(simulationView);
            }
            case "results"      -> showView(resultsView);
            case "visualization"-> showView(vizView);
            case "summary"      -> showView(summaryView);
            case "history"      -> { historyView.refresh(); showView(historyView); }
            case "compare"      -> showView(compareView);
            case "sensitivity" -> { sensitivityView.refreshBaseline(); showView(sensitivityView); }
            case "about"        -> showView(aboutView);
        }
    }

    private void openExperimentDetail(Experiment exp) {
        detailView.load(exp);
        showView(detailView);
    }

    private void openComparison(List<Experiment> selected) {
        compareView.load(selected);
        showView(compareView);
    }

    // ── Config ready ──────────────────────────────────────────────────

    private void onConfigReady(SimulationConfig config) {
        this.lastConfig = config;
        simulationView.refreshConfigSummary(config);
        showView(simulationView);
    }

    // ── Run simulation + auto-save experiment ─────────────────────────

    private void onRunRequested() {
        if (lastConfig == null) {
            simulationView.setStatus("No configuration found. Please go to Configuration first.", true);
            return;
        }
        simulationView.setRunning(true);

        Thread t = new Thread(() -> {
            try {
                List<CloudletResult> results = controller.runSimulation(lastConfig);
                lastResults = results;
                SimulationConfig cfg = controller.getLastConfig();

                // Step 7: auto-save experiment
                Experiment exp = ExperimentStore.getInstance().save(cfg, results);

                javafx.application.Platform.runLater(() -> {
                    // Update all existing output views
                    resultsView.populate(results);
                    vizView.populate(results);
                    summaryView.populate(cfg, results);
                    dashboardView.updateStats(cfg, results);

                    // Refresh history so new experiment appears
                    historyView.refresh();

                    simulationView.setRunning(false);
                    simulationView.setStatus(
                        "Simulation completed. " + results.size() +
                        " cloudlet(s) finished. Saved as \"" + exp.getName() + "\".", false);
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> {
                    simulationView.setRunning(false);
                    String msg = ex.getMessage();
                    if (msg == null || msg.isBlank())
                        msg = "An unexpected error occurred in the simulation engine.";
                    simulationView.setStatus("Simulation failed: " + msg, true);
                });
            }
        });
        t.setDaemon(true);
        t.start();
    }

    // ── Content area ──────────────────────────────────────────────────

    private void showView(Node view) {
        ScrollPane scroll = new ScrollPane(view);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(false);
        scroll.setStyle("-fx-background-color:" + Styles.BG_CONTENT + ";-fx-background:transparent;");
        setCenter(scroll);
    }

    // ── Sidebar ───────────────────────────────────────────────────────

    private VBox buildSidebar() {
        VBox sidebar = new VBox(4);
        sidebar.setPrefWidth(200);
        sidebar.setMinWidth(180);
        sidebar.setStyle("-fx-background-color:" + Styles.BG_APP + ";");
        sidebar.setPadding(new Insets(0, 0, 16, 0));

        VBox brand = new VBox(2);
        brand.setPadding(new Insets(22, 16, 20, 16));
        Label logo = new Label("CloudSim");
        logo.setStyle("-fx-font-size:18px;-fx-font-weight:bold;-fx-text-fill:white;");
        Label sub = new Label("GUI Platform");
        sub.setStyle("-fx-font-size:11px;-fx-text-fill:" + Styles.TEXT_LIGHT + ";");
        brand.getChildren().addAll(logo, sub);

        Region sep = new Region(); sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color:#2d3748;");
        VBox.setMargin(sep, new Insets(0, 0, 8, 0));

        // Simulation section label
        Label simLabel = sidebarSectionLabel("SIMULATION");
        Button btnDash  = navBtn("Dashboard",     "dashboard");
        Button btnConf  = navBtn("Configuration", "config");
        Button btnSim   = navBtn("Simulation",    "simulation");
        Button btnRes   = navBtn("Results",       "results");
        Button btnViz   = navBtn("Visualization", "visualization");
        Button btnSum   = navBtn("Summary",       "summary");

        // Experiments section label (Step 7)
        Label expLabel  = sidebarSectionLabel("EXPERIMENTS");
        Button btnHist  = navBtn("History",       "history");
        Button btnComp  = navBtn("Compare",       "compare");
        Button btnSens  = navBtn("Sensitivity",    "sensitivity");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnAbout = navBtn("About", "about");

        sidebar.getChildren().addAll(
            brand, sep,
            simLabel, btnDash, btnConf, btnSim, btnRes, btnViz, btnSum,
            expLabel, btnHist, btnComp,
            sidebarSectionLabel("ANALYSIS"), btnSens,
            spacer, btnAbout
        );

        setActive(btnDash);
        return sidebar;
    }

    private Label sidebarSectionLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:10px;-fx-font-weight:bold;" +
            "-fx-text-fill:#4a5568;-fx-padding:12 0 4 20;");
        return l;
    }

    private Button navBtn(String label, String page) {
        Button btn = new Button(label);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(10, 16, 10, 20));
        btn.setStyle(navStyle(false));
        btn.setOnAction(e -> { setActive(btn); navigateTo(page); });
        return btn;
    }

    private void setActive(Button btn) {
        if (activeNavBtn != null) activeNavBtn.setStyle(navStyle(false));
        activeNavBtn = btn;
        btn.setStyle(navStyle(true));
    }

    private String navStyle(boolean active) {
        if (active) return
            "-fx-background-color:#2d3748;-fx-text-fill:white;" +
            "-fx-font-size:13px;-fx-font-weight:bold;" +
            "-fx-border-color:" + Styles.SIDEBAR_ACTIVE + ";-fx-border-width:0 0 0 3;" +
            "-fx-background-radius:0;-fx-cursor:hand;";
        return "-fx-background-color:transparent;-fx-text-fill:" + Styles.SIDEBAR_TEXT + ";" +
               "-fx-font-size:13px;-fx-background-radius:0;-fx-cursor:hand;";
    }
}