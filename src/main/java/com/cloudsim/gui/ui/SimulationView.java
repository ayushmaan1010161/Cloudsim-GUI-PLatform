package com.cloudsim.gui.ui;

import com.cloudsim.gui.model.SimulationConfig;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.*;

import java.util.function.Consumer;

/**
 * SimulationView
 * Shows a summary of the current config and a Run Simulation button.
 * Displays honest step-by-step status during execution.
 */
public class SimulationView extends VBox {

    private final Runnable          onRun;
    private final Consumer<String>  nav;

    private Label            statusLabel;
    private Button           runBtn;
    private ProgressIndicator spinner;
    private VBox             configSummaryBox;

    // Step labels
    private Label stepPrepare, stepInfra, stepVms, stepCloudlets, stepCollect, stepDone;

    public SimulationView(Runnable onRun, Consumer<String> nav) {
        this.onRun = onRun;
        this.nav   = nav;
        setSpacing(20);
        setPadding(new Insets(28));
        setStyle("-fx-background-color:" + Styles.BG_CONTENT + ";");
        build();
    }

    private void build() {
        Label title = new Label("Simulation");
        title.setStyle(Styles.FONT_TITLE + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");

        Label desc = new Label(
            "Review your configuration summary below, then click Run Simulation to start.");
        desc.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_SECONDARY + ";");

        // Config summary card
        Label sumHeading = new Label("Current Configuration");
        sumHeading.setStyle(Styles.FONT_H3 + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");

        configSummaryBox = new VBox(6);
        configSummaryBox.getChildren().add(
            note("No configuration saved yet. Please go to Configuration first."));

        VBox summaryCard = new VBox(10, sumHeading, configSummaryBox);
        summaryCard.setPadding(new Insets(18));
        summaryCard.setStyle(Styles.CARD);

        // Execution steps card
        stepPrepare   = step("Preparing simulation...");
        stepInfra     = step("Creating infrastructure (Datacenter, Hosts)...");
        stepVms       = step("Allocating Virtual Machines...");
        stepCloudlets = step("Executing Cloudlets...");
        stepCollect   = step("Collecting results...");
        stepDone      = step("Simulation completed.");

        VBox stepsCard = new VBox(8,
            new Label("Execution Steps") {{
                setStyle(Styles.FONT_H3 + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";"); }},
            stepPrepare, stepInfra, stepVms, stepCloudlets, stepCollect, stepDone);
        stepsCard.setPadding(new Insets(18));
        stepsCard.setStyle(Styles.CARD);
        resetSteps();

        // Run button + spinner
        runBtn = new Button("Run Simulation");
        runBtn.setStyle(Styles.BTN_SUCCESS);
        runBtn.setOnMouseEntered(e -> { if (!runBtn.isDisabled()) runBtn.setStyle(
            Styles.BTN_SUCCESS.replace(Styles.SUCCESS, "#16a34a")); });
        runBtn.setOnMouseExited(e  -> { if (!runBtn.isDisabled()) runBtn.setStyle(Styles.BTN_SUCCESS); });
        runBtn.setOnAction(e -> {
            resetSteps();
            animateSteps();
            onRun.run();
        });

        spinner = new ProgressIndicator();
        spinner.setPrefSize(28, 28);
        spinner.setVisible(false);

        Button viewResBtn = new Button("View Results");
        viewResBtn.setStyle(Styles.BTN_OUTLINE);
        viewResBtn.setOnAction(e -> nav.accept("results"));

        HBox btnRow = new HBox(12, runBtn, spinner, viewResBtn);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        statusLabel = new Label("");
        statusLabel.setWrapText(true);
        statusLabel.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_SECONDARY + ";");

        getChildren().addAll(title, desc, summaryCard, stepsCard, btnRow, statusLabel);
    }

    // ── Public API ────────────────────────────────────────────────────

    public void refreshConfigSummary(SimulationConfig cfg) {
        configSummaryBox.getChildren().clear();
        if (cfg == null) {
            configSummaryBox.getChildren().add(
                note("No configuration saved yet. Please go to Configuration first."));
            return;
        }
        configSummaryBox.getChildren().addAll(
            summaryRow("Datacenters",  String.valueOf(cfg.getDatacenterCount())),
            summaryRow("Hosts",        cfg.getHostCount() + " per datacenter, " + cfg.getHostPes() + " PEs each"),
            summaryRow("VMs",          cfg.getVmCount() + " × " + cfg.getVmPes() + " cores @ " + cfg.getVmMips() + " MIPS"),
            summaryRow("Cloudlets",    cfg.getCloudletCount() + " × " + String.format("%,d", cfg.getCloudletLength()) + " MI")
        );
    }

    public void setRunning(boolean running) {
        runBtn.setDisable(running);
        spinner.setVisible(running);
    }

    public void setStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        statusLabel.setStyle(Styles.FONT_BODY + (isError
            ? "-fx-text-fill:" + Styles.DANGER + ";-fx-font-weight:bold;"
            : "-fx-text-fill:" + Styles.SUCCESS + ";-fx-font-weight:bold;"));
        if (!isError) markAllStepsDone();
    }

    // ── Step animation ────────────────────────────────────────────────

    private void resetSteps() {
        for (Label s : new Label[]{stepPrepare,stepInfra,stepVms,stepCloudlets,stepCollect,stepDone}) {
            s.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_LIGHT + ";");
        }
    }

    private void markAllStepsDone() {
        for (Label s : new Label[]{stepPrepare,stepInfra,stepVms,stepCloudlets,stepCollect,stepDone}) {
            s.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.SUCCESS + ";-fx-font-weight:bold;");
        }
    }

    /** Honest progress: steps turn active as simulation runs (best-effort timing). */
    private void animateSteps() {
        Label[] steps = {stepPrepare, stepInfra, stepVms, stepCloudlets, stepCollect};
        long[] delays = {0, 200, 400, 600, 1200};
        for (int i = 0; i < steps.length; i++) {
            final Label s = steps[i];
            final long delay = delays[i];
            new Thread(() -> {
                try { Thread.sleep(delay); } catch (InterruptedException ignored) {}
                javafx.application.Platform.runLater(() ->
                    s.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.ACCENT + ";-fx-font-weight:bold;"));
            }).start();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private Label step(String text) {
        Label l = new Label("  ○  " + text);
        l.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_LIGHT + ";");
        return l;
    }

    private HBox summaryRow(String key, String val) {
        Label k = new Label(key + ":");
        k.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_SECONDARY + ";");
        k.setMinWidth(120);
        Label v = new Label(val);
        v.setStyle(Styles.FONT_BODY + "-fx-font-weight:bold;-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");
        return new HBox(10, k, v);
    }

    private Label note(String text) {
        Label l = new Label(text);
        l.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_SECONDARY + ";-fx-font-style:italic;");
        l.setWrapText(true);
        return l;
    }
}