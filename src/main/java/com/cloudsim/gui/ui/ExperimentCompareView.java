package com.cloudsim.gui.ui;

import com.cloudsim.gui.experiment.ComparisonEngine;
import com.cloudsim.gui.experiment.ComparisonEngine.ComparisonRow;
import com.cloudsim.gui.experiment.Experiment;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.function.Consumer;

/**
 * ExperimentCompareView
 * Comparison table + 3 charts for selected experiments.
 */
public class ExperimentCompareView extends VBox {

    private final Consumer<String> nav;
    private VBox contentArea;

    public ExperimentCompareView(Consumer<String> nav) {
        this.nav = nav;
        setSpacing(18);
        setPadding(new Insets(28));
        setStyle("-fx-background-color:" + Styles.BG_CONTENT + ";");
        build();
    }

    private void build() {
        Label title = new Label("Experiment Comparison");
        title.setStyle(Styles.FONT_TITLE + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");

        Button backBtn = new Button("Back to History");
        backBtn.setStyle(Styles.BTN_OUTLINE);
        backBtn.setOnAction(e -> nav.accept("history"));
        HBox hdr = new HBox(backBtn);
        hdr.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        contentArea = new VBox(16);
        contentArea.getChildren().add(new Label(
            "Select experiments from History and click Compare.") {{
            setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_SECONDARY +
                ";-fx-font-style:italic;"); }});

        getChildren().addAll(title, hdr, contentArea);
    }

    @SuppressWarnings("unchecked")
    public void load(List<Experiment> experiments) {
        contentArea.getChildren().clear();

        // ── Experiment name header ────────────────────────────────────
        Label subtitle = new Label("Comparing " + experiments.size() + " experiments:");
        subtitle.setStyle(Styles.FONT_H3 + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");

        HBox nameRow = new HBox(12);
        String[] colours = {"#3b82f6","#f59e0b","#22c55e","#8b5cf6","#ef4444"};
        for (int i = 0; i < experiments.size(); i++) {
            Experiment e = experiments.get(i);
            String col = colours[i % colours.length];
            Label badge = new Label("Exp " + e.getId() + ": " + e.getName());
            badge.setStyle("-fx-background-color:" + col + ";-fx-text-fill:white;" +
                "-fx-font-size:12px;-fx-font-weight:bold;" +
                "-fx-padding:4 12 4 12;-fx-background-radius:5;");
            nameRow.getChildren().add(badge);
        }

        // ── Comparison table ──────────────────────────────────────────
        List<ComparisonRow> rows = ComparisonEngine.compare(experiments);

        TableView<ComparisonRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(Math.min(rows.size() * 32 + 40, 500));
        table.setStyle("-fx-background-color:white;");

        TableColumn<ComparisonRow, String> metricCol = new TableColumn<>("Metric");
        metricCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMetric()));
        metricCol.setStyle("-fx-font-weight:bold;");
        metricCol.setMinWidth(180);
        table.getColumns().add(metricCol);

        for (int i = 0; i < experiments.size(); i++) {
            final int idx = i;
            Experiment exp = experiments.get(i);
            TableColumn<ComparisonRow, String> col =
                new TableColumn<>("Exp " + exp.getId() + "\n" + exp.getName());
            col.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getValue(idx)));
            col.setMinWidth(120);
            table.getColumns().add(col);
        }

        table.getItems().setAll(rows);

        VBox tableCard = new VBox(10,
            new Label("Comparison Table") {{
                setStyle(Styles.FONT_H3 + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";"); }},
            table);
        tableCard.setPadding(new Insets(16));
        tableCard.setStyle(Styles.CARD);

        // ── Chart 1: Avg Execution Time ───────────────────────────────
        VBox chart1 = barChartCard(
            "Average Execution Time (s)",
            "Comparison of average cloudlet execution time across experiments.",
            experiments,
            e -> e.getAvgExecTime(),
            colours);

        // ── Chart 2: Total CPU Time ───────────────────────────────────
        VBox chart2 = barChartCard(
            "Total CPU Time (s)",
            "Total CPU time consumed by all cloudlets in each experiment.",
            experiments,
            e -> e.getTotalCpuTime(),
            colours);

        // ── Chart 3: Success Rate ─────────────────────────────────────
        VBox chart3 = barChartCard(
            "Success Rate (%)",
            "Percentage of cloudlets that completed successfully in each experiment.",
            experiments,
            e -> e.getSuccessRate(),
            colours);

        HBox chartsRow = new HBox(14, chart1, chart2, chart3);
        chartsRow.setFillHeight(true);
        for (int i = 0; i < 3; i++) HBox.setHgrow(chartsRow.getChildren().get(i), Priority.ALWAYS);

        contentArea.getChildren().addAll(subtitle, nameRow, tableCard, chartsRow);
    }

    // ── Helpers ───────────────────────────────────────────────────────

    @FunctionalInterface interface MetricExtractor { double get(Experiment e); }

    private VBox barChartCard(String heading, String desc,
                               List<Experiment> experiments,
                               MetricExtractor extractor,
                               String[] colours) {
        CategoryAxis x = new CategoryAxis();
        NumberAxis   y = new NumberAxis(); y.setForceZeroInRange(true);

        BarChart<String, Number> chart = new BarChart<>(x, y);
        chart.setTitle(heading);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setPrefHeight(220);
        chart.setBarGap(4); chart.setCategoryGap(16);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 0; i < experiments.size(); i++) {
            Experiment e = experiments.get(i);
            XYChart.Data<String, Number> bar =
                new XYChart.Data<>("Exp " + e.getId(), extractor.get(e));
            series.getData().add(bar);
        }
        chart.getData().add(series);

        // Apply colours after adding to chart
        for (int i = 0; i < series.getData().size(); i++) {
            final String col = colours[i % colours.length];
            XYChart.Data<String, Number> bar = series.getData().get(i);
            if (bar.getNode() != null) bar.getNode().setStyle("-fx-bar-fill:" + col + ";");
        }

        Label d = new Label(desc);
        d.setStyle(Styles.FONT_SMALL + "-fx-text-fill:" + Styles.TEXT_SECONDARY + ";");
        d.setWrapText(true);

        VBox card = new VBox(6, d, chart);
        card.setPadding(new Insets(14));
        card.setStyle(Styles.CARD);
        return card;
    }
}