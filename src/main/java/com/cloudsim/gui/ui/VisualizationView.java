package com.cloudsim.gui.ui;

import com.cloudsim.gui.model.CloudletResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.*;

/**
 * VisualizationView
 * Three charts — all driven by actual simulation results:
 *   1. Cloudlet Execution Time (BarChart)
 *   2. VM Allocation — which cloudlets ran on which VM (BarChart grouped)
 *   3. Exec Time Comparison across VMs (BarChart by VM)
 */
public class VisualizationView extends VBox {

    private BarChart<String, Number> execChart;
    private BarChart<String, Number> vmAllocChart;
    private BarChart<String, Number> vmCompareChart;
    private Label emptyNote;

    public VisualizationView() {
        setSpacing(18);
        setPadding(new Insets(28));
        setStyle("-fx-background-color:" + Styles.BG_CONTENT + ";");
        build();
    }

    private void build() {
        Label title = new Label("Visualization");
        title.setStyle(Styles.FONT_TITLE + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");

        emptyNote = new Label(
            "No simulation results yet. Run a simulation to see charts.");
        emptyNote.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_SECONDARY +
            ";-fx-font-style:italic;");

        execChart     = buildBarChart("Cloudlet Execution Time",
            "Cloudlet ID", "Execution Time (s)");
        vmAllocChart  = buildBarChart("VM Allocation (Cloudlets per VM)",
            "VM ID", "Number of Cloudlets");
        vmCompareChart = buildBarChart("Average Execution Time per VM",
            "VM ID", "Avg Exec Time (s)");

        VBox chart1 = chartCard("1. Cloudlet Execution Time",
            "Bar chart showing how long each cloudlet took to execute.", execChart);
        VBox chart2 = chartCard("2. VM Allocation",
            "How many cloudlets were assigned to each VM.", vmAllocChart);
        VBox chart3 = chartCard("3. Average Execution Time per VM",
            "Comparison of average execution time across VMs.", vmCompareChart);

        getChildren().addAll(title, emptyNote, chart1, chart2, chart3);
    }

    public void populate(List<CloudletResult> results) {
        if (results == null || results.isEmpty()) return;
        emptyNote.setVisible(false);
        emptyNote.setManaged(false);

        String[] colours = {"#3b82f6","#f59e0b","#22c55e","#8b5cf6","#ef4444","#06b6d4"};

        // ── Chart 1: Execution Time per Cloudlet ─────────────────────
        execChart.getData().clear();
        XYChart.Series<String, Number> s1 = new XYChart.Series<>();
        s1.setName("Exec Time (s)");
        for (CloudletResult r : results) {
            s1.getData().add(new XYChart.Data<>("C-" + r.getCloudletId(), r.getExecutionTime()));
        }
        execChart.getData().add(s1);
        styleBarsByVm(s1, results, colours);

        // ── Chart 2: Cloudlets per VM ─────────────────────────────────
        vmAllocChart.getData().clear();
        Map<Integer, Long> vmCount = new LinkedHashMap<>();
        for (CloudletResult r : results)
            vmCount.merge(r.getVmId(), 1L, Long::sum);
        XYChart.Series<String, Number> s2 = new XYChart.Series<>();
        s2.setName("Cloudlets");
        vmCount.forEach((vmId, cnt) ->
            s2.getData().add(new XYChart.Data<>("VM-" + vmId, cnt)));
        vmAllocChart.getData().add(s2);
        int ci = 0;
        for (XYChart.Data<String, Number> bar : s2.getData()) {
            final String c = colours[ci++ % colours.length];
            if (bar.getNode() != null) bar.getNode().setStyle("-fx-bar-fill:" + c + ";");
        }

        // ── Chart 3: Avg Exec Time per VM ────────────────────────────
        vmCompareChart.getData().clear();
        Map<Integer, List<Double>> vmTimes = new LinkedHashMap<>();
        for (CloudletResult r : results)
            vmTimes.computeIfAbsent(r.getVmId(), k -> new ArrayList<>()).add(r.getExecutionTime());
        XYChart.Series<String, Number> s3 = new XYChart.Series<>();
        s3.setName("Avg Exec Time (s)");
        vmTimes.forEach((vmId, times) -> {
            double avg = times.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            s3.getData().add(new XYChart.Data<>("VM-" + vmId, avg));
        });
        vmCompareChart.getData().add(s3);
        int ci2 = 0;
        for (XYChart.Data<String, Number> bar : s3.getData()) {
            final String c = colours[ci2++ % colours.length];
            if (bar.getNode() != null) bar.getNode().setStyle("-fx-bar-fill:" + c + ";");
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private BarChart<String, Number> buildBarChart(String title, String xLabel, String yLabel) {
        CategoryAxis x = new CategoryAxis(); x.setLabel(xLabel);
        NumberAxis   y = new NumberAxis();   y.setLabel(yLabel); y.setForceZeroInRange(true);
        BarChart<String, Number> chart = new BarChart<>(x, y);
        chart.setTitle(title);
        chart.setLegendVisible(false);
        chart.setAnimated(true);
        chart.setBarGap(3);
        chart.setCategoryGap(10);
        chart.setPrefHeight(260);
        return chart;
    }

    private VBox chartCard(String heading, String desc, BarChart<String, Number> chart) {
        Label h = new Label(heading);
        h.setStyle(Styles.FONT_H3 + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");
        Label d = new Label(desc);
        d.setStyle(Styles.FONT_SMALL + "-fx-text-fill:" + Styles.TEXT_SECONDARY + ";");
        VBox card = new VBox(6, h, d, chart);
        card.setPadding(new Insets(16));
        card.setStyle(Styles.CARD);
        return card;
    }

    private void styleBarsByVm(XYChart.Series<String, Number> series,
                                List<CloudletResult> results, String[] colours) {
        for (int i = 0; i < series.getData().size(); i++) {
            int vmId = results.get(i).getVmId();
            String c = colours[vmId % colours.length];
            XYChart.Data<String, Number> bar = series.getData().get(i);
            if (bar.getNode() != null) bar.getNode().setStyle("-fx-bar-fill:" + c + ";");
        }
    }
}