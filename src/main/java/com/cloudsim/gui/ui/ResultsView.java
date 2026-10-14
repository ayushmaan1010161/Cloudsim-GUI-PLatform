package com.cloudsim.gui.ui;

import com.cloudsim.gui.model.CloudletResult;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.scene.layout.*;

import java.util.List;

/**
 * ResultsView
 * Displays the results table with sorting and stat cards at the top.
 */
public class ResultsView extends VBox {

    private Label valCompleted, valAvg, valMin, valMax, valTotal;
    private TableView<CloudletResult> table;
    private Label emptyLabel;

    public ResultsView() {
        setSpacing(18);
        setPadding(new Insets(28));
        setStyle("-fx-background-color:" + Styles.BG_CONTENT + ";");
        build();
    }

    @SuppressWarnings("unchecked")
    private void build() {
        Label title = new Label("Results");
        title.setStyle(Styles.FONT_TITLE + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");

        // Stat cards
        valCompleted = sv("—"); valAvg = sv("—"); valMin = sv("—");
        valMax = sv("—"); valTotal = sv("—");

        HBox cards = new HBox(12,
            miniCard("Successful Cloudlets", valCompleted, Styles.SUCCESS),
            miniCard("Avg Exec Time",        valAvg,       Styles.ACCENT),
            miniCard("Min Exec Time",        valMin,       "#8b5cf6"),
            miniCard("Max Exec Time",        valMax,       Styles.WARNING),
            miniCard("Total CPU Time",       valTotal,     Styles.DANGER)
        );
        cards.setFillHeight(true);
        for (int i = 0; i < 5; i++) HBox.setHgrow(cards.getChildren().get(i), Priority.ALWAYS);

        // Table
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setSortPolicy(tv -> { FXCollections.sort(tv.getItems(),
            tv.getComparator()); return true; });

        TableColumn<CloudletResult, Integer> colId = new TableColumn<>("Cloudlet ID");
        colId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getCloudletId()).asObject());
        colId.setSortable(true);

        TableColumn<CloudletResult, Integer> colVm = new TableColumn<>("VM ID");
        colVm.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getVmId()).asObject());

        TableColumn<CloudletResult, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); setStyle(""); return; }
                setText(v);
                setStyle("SUCCESS".equalsIgnoreCase(v)
                    ? "-fx-text-fill:" + Styles.SUCCESS + ";-fx-font-weight:bold;"
                    : "-fx-text-fill:" + Styles.DANGER + ";-fx-font-weight:bold;");
            }
        });

        TableColumn<CloudletResult, Double> colStart  = dblCol("Start (s)",     d -> d.getStartTime());
        TableColumn<CloudletResult, Double> colFinish = dblCol("Finish (s)",    d -> d.getFinishTime());
        TableColumn<CloudletResult, Double> colExec   = dblCol("Exec Time (s)", d -> d.getExecutionTime());
        colExec.setSortable(true);

        table.getColumns().addAll(colId, colVm, colStatus, colStart, colFinish, colExec);
        table.setPrefHeight(340);
        table.setStyle("-fx-background-color:white;-fx-border-color:" + Styles.BORDER + ";");

        emptyLabel = new Label("No results yet. Run a simulation first.");
        emptyLabel.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_SECONDARY + ";-fx-font-style:italic;");
        table.setPlaceholder(emptyLabel);

        VBox tableCard = new VBox(10,
            new Label("Cloudlet Results") {{
                setStyle(Styles.FONT_H3 + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";"); }},
            table);
        tableCard.setPadding(new Insets(18));
        tableCard.setStyle(Styles.CARD);

        getChildren().addAll(title, cards, tableCard);
    }

    public void populate(List<CloudletResult> results) {
        table.getItems().setAll(results);

        long ok = results.stream().filter(r -> "SUCCESS".equalsIgnoreCase(r.getStatus())).count();
        double avg   = results.stream().mapToDouble(CloudletResult::getExecutionTime).average().orElse(0);
        double min   = results.stream().mapToDouble(CloudletResult::getExecutionTime).min().orElse(0);
        double max   = results.stream().mapToDouble(CloudletResult::getExecutionTime).max().orElse(0);
        double total = results.stream().mapToDouble(CloudletResult::getExecutionTime).sum();

        valCompleted.setText(ok + " / " + results.size());
        valAvg.setText(String.format("%.2f s", avg));
        valMin.setText(String.format("%.2f s", min));
        valMax.setText(String.format("%.2f s", max));
        valTotal.setText(String.format("%.2f s", total));
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private HBox miniCard(String title, Label val, String accent) {
        Label t = new Label(title.toUpperCase());
        t.setStyle(Styles.FONT_CAPTION + "-fx-text-fill:" + Styles.TEXT_SECONDARY + ";-fx-font-weight:bold;");
        VBox c = new VBox(5, t, val);
        c.setPadding(new Insets(14, 14, 14, 18));
        c.setStyle(Styles.statCard(accent));
        HBox w = new HBox(c); HBox.setHgrow(c, Priority.ALWAYS); return w;
    }

    private Label sv(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-font-size:18px;-fx-font-weight:bold;-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");
        return l;
    }

    @FunctionalInterface interface Getter { double get(CloudletResult r); }

    private TableColumn<CloudletResult, Double> dblCol(String title, Getter g) {
        TableColumn<CloudletResult, Double> col = new TableColumn<>(title);
        col.setCellValueFactory(d -> new SimpleDoubleProperty(g.get(d.getValue())).asObject());
        col.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                setText((empty || v == null) ? null : String.format("%.2f", v));
            }
        });
        col.setSortable(true);
        return col;
    }
}