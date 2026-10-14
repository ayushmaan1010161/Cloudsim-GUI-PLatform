package com.cloudsim.gui.ui;

import com.cloudsim.gui.model.SimulationConfig;
import com.cloudsim.gui.sensitivity.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.List;
import java.util.function.Supplier;

/**
 * SensitivityView
 *
 * Full Sensitivity Analysis page.
 * Sections:
 *   A. Analysis Configuration (baseline reminder + parameter dropdown)
 *   B. Range Configuration (start / end / step)
 *   C. Run Analysis button + progress indicator
 *   D. Results table
 *   E. Visualization (line/bar chart with metric selector)
 *   F. Summary card
 */
public class SensitivityView extends VBox {

    private final Supplier<SimulationConfig> baselineSupplier;

    // ── Section A: parameter selection ───────────────────────────────
    private ComboBox<SensitivityParameter> paramCombo;
    private Label                          baselineInfo;

    // ── Section B: range ──────────────────────────────────────────────
    private TextField tfStart, tfEnd, tfStep;
    private Label     unitLabel, previewLabel;

    // ── Section C: run ────────────────────────────────────────────────
    private Button runBtn;
    private Label  progressLabel;
    private ProgressBar progressBar;

    // ── Section D: results table ──────────────────────────────────────
    private TableView<SensitivityRunResult> resultTable;

    // ── Section E: chart ─────────────────────────────────────────────
    private BarChart<String, Number>        chart;
    private ComboBox<String>                metricCombo;
    private Label                           chartPlaceholder;

    // ── Section F: summary ────────────────────────────────────────────
    private Label sParam, sRange, sRuns, sFirst, sLast, sChange, sBest, sWorst;
    private VBox  summaryCard;

    public SensitivityView(Supplier<SimulationConfig> baselineSupplier) {
        this.baselineSupplier = baselineSupplier;
        setSpacing(18);
        setPadding(new Insets(28));
        setStyle("-fx-background-color:" + Styles.BG_CONTENT + ";");
        build();
    }

    private void build() {
        Label title = new Label("Sensitivity Analysis");
        title.setStyle(Styles.FONT_TITLE + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");

        Label desc = new Label(
            "Automatically sweep a single parameter across a range of values, " +
            "run a real CloudSim simulation for each value, and observe how results change. " +
            "All other parameters remain fixed at the current configuration baseline.");
        desc.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_SECONDARY + ";");
        desc.setWrapText(true);

        getChildren().addAll(
            title, desc,
            buildSectionA(),
            buildSectionB(),
            buildSectionC(),
            buildSectionD(),
            buildSectionE(),
            buildSectionF()
        );
    }

    // ── Section A: Analysis Configuration ────────────────────────────

    private VBox buildSectionA() {
        baselineInfo = new Label("No baseline configuration. Please go to Configuration first.");
        baselineInfo.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_SECONDARY +
            ";-fx-font-style:italic;");
        baselineInfo.setWrapText(true);

        paramCombo = new ComboBox<>(
            FXCollections.observableArrayList(SensitivityParameter.values()));
        paramCombo.setPromptText("Select parameter to vary...");
        paramCombo.setPrefWidth(260);
        paramCombo.setOnAction(e -> onParamChanged());

        Label paramLabel = new Label("Parameter to vary:");
        paramLabel.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");

        Tooltip tt = new Tooltip(
            "Select which CloudSim parameter to sweep.\n" +
            "All other parameters stay fixed at the baseline configuration.");
        tt.setWrapText(true); tt.setMaxWidth(280);
        Tooltip.install(paramCombo, tt);

        HBox paramRow = new HBox(12, paramLabel, paramCombo);
        paramRow.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(10,
            sectionHeading("A. Analysis Configuration",
                "The baseline is taken from your current Configuration page. " +
                "Only the selected parameter will change across runs."),
            baselineInfo, paramRow);
        card.setPadding(new Insets(18));
        card.setStyle(Styles.CARD);
        return card;
    }

    // ── Section B: Range Configuration ───────────────────────────────

    private VBox buildSectionB() {
        tfStart = rangeField("1000");
        tfEnd   = rangeField("5000");
        tfStep  = rangeField("1000");

        unitLabel = new Label("(select a parameter first)");
        unitLabel.setStyle(Styles.FONT_SMALL + "-fx-text-fill:" + Styles.TEXT_SECONDARY + ";");

        previewLabel = new Label("");
        previewLabel.setStyle(Styles.FONT_SMALL + "-fx-text-fill:" + Styles.ACCENT +
            ";-fx-font-weight:bold;");
        previewLabel.setWrapText(true);

        tfStart.textProperty().addListener((o,ov,nv) -> updatePreview());
        tfEnd.textProperty().addListener((o,ov,nv)   -> updatePreview());
        tfStep.textProperty().addListener((o,ov,nv)  -> updatePreview());

        GridPane g = new GridPane();
        g.setHgap(14); g.setVgap(9);
        addRangeRow(g, 0, "Start Value", tfStart,
            "First parameter value to test. Must be within the allowed range for the selected parameter.");
        addRangeRow(g, 1, "End Value",   tfEnd,
            "Last parameter value to test. Must be >= Start Value.");
        addRangeRow(g, 2, "Step Size",   tfStep,
            "Increment between consecutive values. Must be a positive number.");

        VBox card = new VBox(10,
            sectionHeading("B. Range Configuration",
                "Define the range of values to test. The system will generate: " +
                "Start, Start+Step, Start+2×Step, ... up to End."),
            g, unitLabel, previewLabel);
        card.setPadding(new Insets(18));
        card.setStyle(Styles.CARD);
        return card;
    }

    // ── Section C: Run Analysis ───────────────────────────────────────

    private VBox buildSectionC() {
        runBtn = new Button("Run Sensitivity Analysis");
        runBtn.setStyle(Styles.BTN_SUCCESS);
        runBtn.setOnMouseEntered(e -> { if (!runBtn.isDisabled())
            runBtn.setStyle(Styles.BTN_SUCCESS.replace(Styles.SUCCESS, "#16a34a")); });
        runBtn.setOnMouseExited(e  -> { if (!runBtn.isDisabled())
            runBtn.setStyle(Styles.BTN_SUCCESS); });
        runBtn.setOnAction(e -> runAnalysis());

        progressBar   = new ProgressBar(0);
        progressBar.setPrefWidth(320);
        progressBar.setVisible(false);

        progressLabel = new Label("");
        progressLabel.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_SECONDARY + ";");

        VBox card = new VBox(10,
            sectionHeading("C. Run Analysis",
                "Clicking Run will execute one real CloudSim simulation per parameter value. " +
                "The GUI remains responsive during the sweep."),
            new HBox(14, runBtn, progressBar) {{ setAlignment(Pos.CENTER_LEFT); }},
            progressLabel);
        card.setPadding(new Insets(18));
        card.setStyle(Styles.CARD);
        return card;
    }

    // ── Section D: Results Table ──────────────────────────────────────

    @SuppressWarnings("unchecked")
    private VBox buildSectionD() {
        resultTable = new TableView<>();
        resultTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        resultTable.setPrefHeight(240);
        resultTable.setPlaceholder(new Label(
            "Results will appear here after running an analysis."));

        TableColumn<SensitivityRunResult, String> colVal =
            strCol("Parameter Value", r -> String.valueOf(r.getParameterValue()));
        TableColumn<SensitivityRunResult, String> colTotal =
            strCol("Total Cloudlets", r -> String.valueOf(r.getTotalCloudlets()));
        TableColumn<SensitivityRunResult, String> colOk =
            strCol("Successful", r -> String.valueOf(r.getSuccessfulCloudlets()));
        TableColumn<SensitivityRunResult, String> colFail =
            strCol("Failed", r -> String.valueOf(r.getFailedCloudlets()));
        TableColumn<SensitivityRunResult, String> colRate =
            strCol("Success Rate", r -> String.format("%.0f%%", r.getSuccessRate()));
        TableColumn<SensitivityRunResult, String> colAvg =
            strCol("Avg Exec Time (s)", r -> r.isFailed() ? "ERROR" :
                String.format("%.2f", r.getAvgExecTime()));
        TableColumn<SensitivityRunResult, String> colMin =
            strCol("Min (s)", r -> r.isFailed() ? "—" :
                String.format("%.2f", r.getMinExecTime()));
        TableColumn<SensitivityRunResult, String> colMax =
            strCol("Max (s)", r -> r.isFailed() ? "—" :
                String.format("%.2f", r.getMaxExecTime()));
        TableColumn<SensitivityRunResult, String> colTot =
            strCol("Total CPU (s)", r -> r.isFailed() ? "—" :
                String.format("%.2f", r.getTotalExecTime()));

        // Colour-code failed rows
        colAvg.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); setStyle(""); return; }
                setText(v);
                setStyle("ERROR".equals(v)
                    ? "-fx-text-fill:" + Styles.DANGER + ";-fx-font-weight:bold;"
                    : "");
            }
        });

        resultTable.getColumns().addAll(
            colVal, colTotal, colOk, colFail, colRate, colAvg, colMin, colMax, colTot);

        VBox card = new VBox(10,
            sectionHeading("D. Results Table",
                "Each row represents one real CloudSim simulation run with the given parameter value."),
            resultTable);
        card.setPadding(new Insets(18));
        card.setStyle(Styles.CARD);
        return card;
    }

    // ── Section E: Chart ─────────────────────────────────────────────

    private VBox buildSectionE() {
        metricCombo = new ComboBox<>(FXCollections.observableArrayList(
            "Average Execution Time", "Minimum Execution Time",
            "Maximum Execution Time", "Success Rate", "Total CPU Time"));
        metricCombo.setValue("Average Execution Time");
        metricCombo.setOnAction(e -> refreshChart());

        CategoryAxis x = new CategoryAxis(); x.setLabel("Parameter Value");
        NumberAxis   y = new NumberAxis();   y.setLabel("Metric Value");
        y.setForceZeroInRange(true);
        chart = new BarChart<>(x, y);
        chart.setTitle("Sensitivity Analysis");
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setPrefHeight(280);

        chartPlaceholder = new Label("Run an analysis to see the chart.");
        chartPlaceholder.setStyle(Styles.FONT_BODY+"-fx-text-fill:"+Styles.TEXT_SECONDARY+";-fx-font-style:italic;");

        StackPane stack = new StackPane(chart, chartPlaceholder);
        StackPane.setAlignment(chartPlaceholder, Pos.CENTER);

        HBox metricRow = new HBox(10,
            new Label("Metric:") {{ setStyle(Styles.FONT_BODY+"-fx-text-fill:"+Styles.TEXT_PRIMARY+";"); }},
            metricCombo);
        metricRow.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(10,
            sectionHeading("E. Visualization",
                "Chart showing how the selected metric changes as the parameter value increases."),
            metricRow, stack);
        card.setPadding(new Insets(18));
        card.setStyle(Styles.CARD);
        return card;
    }

    // ── Section F: Summary ───────────────────────────────────────────

    private VBox buildSectionF() {
        sParam  = sv(); sRange  = sv(); sRuns   = sv();
        sFirst  = sv(); sLast   = sv(); sChange = sv();
        sBest   = sv(); sWorst  = sv();

        GridPane g = new GridPane(); g.setHgap(20); g.setVgap(8);
        g.setPadding(new Insets(10));
        sumRow(g,0,"Parameter Varied",   sParam);
        sumRow(g,1,"Values Tested",      sRange);
        sumRow(g,2,"Runs Completed",     sRuns);
        sumRow(g,3,"Avg Exec Time (first value)", sFirst);
        sumRow(g,4,"Avg Exec Time (last value)",  sLast);
        sumRow(g,5,"Observed Change",    sChange);
        sumRow(g,6,"Lowest Observed Avg Time at",  sBest);
        sumRow(g,7,"Highest Observed Avg Time at", sWorst);

        summaryCard = new VBox(10,
            sectionHeading("F. Analysis Summary",
                "Descriptive summary of the observed results."),
            g);
        summaryCard.setPadding(new Insets(18));
        summaryCard.setStyle(Styles.CARD);
        summaryCard.setVisible(false);
        summaryCard.setManaged(false);
        return summaryCard;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  RUN ANALYSIS
    // ═══════════════════════════════════════════════════════════════════

    private SensitivityAnalysis lastAnalysis = null;

    private void runAnalysis() {
        // Check baseline
        SimulationConfig baseline = baselineSupplier.get();
        if (baseline == null) {
            showError("No baseline configuration. Go to Configuration page first and save a config.");
            return;
        }
        // Check parameter selected
        SensitivityParameter param = paramCombo.getValue();
        if (param == null) {
            showError("Please select a parameter to vary.");
            return;
        }
        // Parse range
        long start, end, step;
        try {
            start = Long.parseLong(tfStart.getText().trim());
            end   = Long.parseLong(tfEnd.getText().trim());
            step  = Long.parseLong(tfStep.getText().trim());
        } catch (NumberFormatException ex) {
            showError("Start, End and Step must be valid whole numbers.");
            return;
        }
        if (start < 1)    { showError("Start Value must be at least 1."); return; }
        if (end < start)  { showError("End Value must be greater than or equal to Start Value."); return; }
        if (step < 1)     { showError("Step must be a positive whole number."); return; }
        long count = ((end - start) / step) + 1;
        if (count > 50)   { showError("Too many runs (" + count + "). Max 50. Increase the Step value."); return; }

        SensitivityConfig cfg = new SensitivityConfig(baseline, param, start, end, step);

        // Disable UI
        runBtn.setDisable(true);
        progressBar.setProgress(0);
        progressBar.setVisible(true);
        progressLabel.setText("Starting analysis...");

        Thread t = new Thread(() -> {
            SensitivityRunner runner = new SensitivityRunner(cfg, (done, total) -> {
                javafx.application.Platform.runLater(() -> {
                    progressBar.setProgress((double) done / total);
                    progressLabel.setText("Running simulation " + done + " of " + total + "...");
                });
            });

            try {
                SensitivityAnalysis analysis = runner.run();
                lastAnalysis = analysis;

                javafx.application.Platform.runLater(() -> {
                    populateResults(analysis);
                    populateSummary(analysis);
                    refreshChart();
                    progressLabel.setText("Analysis completed. " + analysis.completedRuns()
                        + " of " + analysis.getRunResults().size() + " runs succeeded.");
                    progressLabel.setStyle(Styles.FONT_BODY+"-fx-text-fill:"+Styles.SUCCESS+";-fx-font-weight:bold;");
                    progressBar.setProgress(1.0);
                    runBtn.setDisable(false);
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> {
                    progressLabel.setText("Analysis failed: " + ex.getMessage());
                    progressLabel.setStyle(Styles.FONT_BODY+"-fx-text-fill:"+Styles.DANGER+";-fx-font-weight:bold;");
                    runBtn.setDisable(false);
                });
            }
        });
        t.setDaemon(true);
        t.start();
    }

    // ═══════════════════════════════════════════════════════════════════
    //  POPULATE RESULTS
    // ═══════════════════════════════════════════════════════════════════

    private void populateResults(SensitivityAnalysis analysis) {
        resultTable.setItems(FXCollections.observableArrayList(analysis.getRunResults()));
    }

    private void populateSummary(SensitivityAnalysis a) {
        summaryCard.setVisible(true);
        summaryCard.setManaged(true);

        SensitivityConfig c = a.getConfig();
        sParam.setText(c.getParameter().getDisplayName());
        sRange.setText(c.getStart() + " to " + c.getEnd() + " (step " + c.getStep() + ")");
        sRuns.setText(a.completedRuns() + " completed, " + a.failedRuns() + " failed");
        sFirst.setText(String.format("%.2f s", a.firstSuccessAvgTime()));
        sLast.setText(String.format("%.2f s", a.lastSuccessAvgTime()));

        double pct = a.percentChangeAvgTime();
        String dir = pct > 0 ? "increased" : pct < 0 ? "decreased" : "unchanged";
        sChange.setText(String.format("%.1f%% (%s)", Math.abs(pct), dir));
        sChange.setStyle("-fx-font-size:13px;-fx-font-weight:bold;-fx-text-fill:" +
            (pct < 0 ? Styles.SUCCESS : pct > 0 ? Styles.DANGER : Styles.TEXT_PRIMARY) + ";");

        SensitivityRunResult best = a.bestAvgTimeRun();
        SensitivityRunResult worst = a.worstAvgTimeRun();
        sBest.setText(best == null ? "—" :
            c.getParameter().getDisplayName() + " = " + best.getParameterValue()
            + " (" + String.format("%.2f s", best.getAvgExecTime()) + ")");
        sWorst.setText(worst == null ? "—" :
            c.getParameter().getDisplayName() + " = " + worst.getParameterValue()
            + " (" + String.format("%.2f s", worst.getAvgExecTime()) + ")");
    }

    private void refreshChart() {
        if (lastAnalysis == null) return;
        chart.getData().clear();
        chartPlaceholder.setVisible(false);
        chartPlaceholder.setManaged(false);

        String metric = metricCombo.getValue();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(metric);

        for (SensitivityRunResult r : lastAnalysis.getRunResults()) {
            if (r.isFailed()) continue;
            String label = String.valueOf(r.getParameterValue());
            double val = switch (metric) {
                case "Minimum Execution Time" -> r.getMinExecTime();
                case "Maximum Execution Time" -> r.getMaxExecTime();
                case "Success Rate"           -> r.getSuccessRate();
                case "Total CPU Time"         -> r.getTotalExecTime();
                default                       -> r.getAvgExecTime();
            };
            series.getData().add(new XYChart.Data<>(label, val));
        }
        chart.getData().add(series);

        // Style bars
        String[] colours = {"#3b82f6","#f59e0b","#22c55e","#8b5cf6","#ef4444","#06b6d4"};
        for (int i = 0; i < series.getData().size(); i++) {
            XYChart.Data<String,Number> bar = series.getData().get(i);
            if (bar.getNode() != null)
                bar.getNode().setStyle("-fx-bar-fill:" + colours[i % colours.length] + ";");
        }

        chart.setTitle(lastAnalysis.getConfig().getParameter().getDisplayName()
            + " vs " + metric);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  UI REFRESH
    // ═══════════════════════════════════════════════════════════════════

    /** Called when the page becomes visible to refresh baseline info. */
    public void refreshBaseline() {
        SimulationConfig b = baselineSupplier.get();
        if (b == null) {
            baselineInfo.setText("No baseline configuration. Go to Configuration first.");
        } else {
            baselineInfo.setText("Baseline: " + b.toString());
        }
    }

    private void onParamChanged() {
        SensitivityParameter p = paramCombo.getValue();
        if (p == null) return;
        unitLabel.setText("Unit: " + p.getUnit()
            + "  |  Allowed range: " + p.getMinAllowed() + " – "
            + (p.getMaxAllowed() == Long.MAX_VALUE ? "unlimited" : p.getMaxAllowed()));
        updatePreview();
    }

    private void updatePreview() {
        try {
            long s = Long.parseLong(tfStart.getText().trim());
            long e = Long.parseLong(tfEnd.getText().trim());
            long st = Long.parseLong(tfStep.getText().trim());
            if (s > 0 && e >= s && st > 0) {
                long count = ((e - s) / st) + 1;
                StringBuilder sb = new StringBuilder("Preview: ");
                for (long v = s; v <= e; v += st) {
                    if (sb.length() > 9) sb.append(", ");
                    sb.append(v);
                    if (sb.length() > 120) { sb.append(", ..."); break; }
                }
                sb.append("  (").append(count).append(" runs)");
                previewLabel.setText(sb.toString());
            } else {
                previewLabel.setText("");
            }
        } catch (NumberFormatException ex) {
            previewLabel.setText("");
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  HELPERS
    // ═══════════════════════════════════════════════════════════════════

    private VBox sectionHeading(String heading, String desc) {
        Label h = new Label(heading);
        h.setStyle(Styles.FONT_H3 + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");
        Label d = new Label(desc);
        d.setStyle(Styles.FONT_SMALL + "-fx-text-fill:" + Styles.TEXT_SECONDARY + ";");
        d.setWrapText(true);
        return new VBox(3, h, d);
    }

    private TextField rangeField(String def) {
        TextField tf = new TextField(def);
        tf.setPrefWidth(120);
        tf.setStyle(Styles.INPUT);
        return tf;
    }

    private void addRangeRow(GridPane g, int row, String label, TextField tf, String tip) {
        Label l = new Label(label + ":");
        l.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");
        l.setMinWidth(120);
        Tooltip t = new Tooltip(tip);
        t.setWrapText(true); t.setMaxWidth(260); t.setShowDelay(Duration.millis(250));
        Tooltip.install(tf, t); Tooltip.install(l, t);
        g.add(l, 0, row);
        g.add(tf, 1, row);
    }

    private Label sv() {
        Label l = new Label("—");
        l.setStyle("-fx-font-size:13px;-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");
        return l;
    }

    private void sumRow(GridPane g, int row, String key, Label val) {
        Label k = new Label(key + ":"); k.setMinWidth(220);
        k.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_SECONDARY + ";");
        g.add(k, 0, row); g.add(val, 1, row);
    }

    @FunctionalInterface interface Extractor { String get(SensitivityRunResult r); }
    private TableColumn<SensitivityRunResult, String> strCol(String title, Extractor ex) {
        TableColumn<SensitivityRunResult, String> col = new TableColumn<>(title);
        col.setCellValueFactory(d -> new SimpleStringProperty(ex.get(d.getValue())));
        col.setSortable(true);
        return col;
    }

    private void showError(String msg) {
        progressLabel.setText(msg);
        progressLabel.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.DANGER + ";-fx-font-weight:bold;");
    }
}