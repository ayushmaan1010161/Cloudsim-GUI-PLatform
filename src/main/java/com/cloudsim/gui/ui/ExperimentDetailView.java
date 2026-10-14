package com.cloudsim.gui.ui;

import com.cloudsim.gui.experiment.Experiment;
import com.cloudsim.gui.model.CloudletResult;
import com.cloudsim.gui.model.SimulationConfig;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.function.Consumer;

/**
 * ExperimentDetailView
 * Shows the full configuration and results for one selected experiment.
 */
public class ExperimentDetailView extends VBox {

    private final Consumer<String> nav;

    private Label expTitle;
    private Label expTimestamp;

    // Config labels
    private Label cDC, cHosts, cHostPes, cHostMips, cHostRam, cHostStorage, cHostBw;
    private Label cVMs, cVmMips, cVmPes, cVmRam, cVmStorage, cVmBw;
    private Label cCL, cClLen, cClFile, cClOut, cClPes;

    // Performance labels
    private Label pOk, pFail, pAvg, pMin, pMax, pTotal, pRate;

    // Results table
    private TableView<CloudletResult> resultTable;

    public ExperimentDetailView(Consumer<String> nav) {
        this.nav = nav;
        setSpacing(18);
        setPadding(new Insets(28));
        setStyle("-fx-background-color:" + Styles.BG_CONTENT + ";");
        build();
    }

    @SuppressWarnings("unchecked")
    private void build() {
        // Header
        expTitle     = new Label("Experiment Detail");
        expTitle.setStyle(Styles.FONT_TITLE + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");
        expTimestamp = new Label("");
        expTimestamp.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_SECONDARY + ";");

        Button backBtn = new Button("Back to History");
        backBtn.setStyle(Styles.BTN_OUTLINE);
        backBtn.setOnAction(e -> nav.accept("history"));
        HBox headerRow = new HBox(10, backBtn);
        headerRow.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        // Config section
        cDC = sv(); cHosts = sv(); cHostPes = sv(); cHostMips = sv();
        cHostRam = sv(); cHostStorage = sv(); cHostBw = sv();
        cVMs = sv(); cVmMips = sv(); cVmPes = sv(); cVmRam = sv(); cVmStorage = sv(); cVmBw = sv();
        cCL = sv(); cClLen = sv(); cClFile = sv(); cClOut = sv(); cClPes = sv();

        HBox cfgRow = new HBox(16,
            sectionCard("Datacenter & Host",
                row("Datacenters",   cDC),   row("Hosts",         cHosts),
                row("CPU Cores",     cHostPes), row("CPU MIPS",   cHostMips),
                row("RAM (MB)",      cHostRam), row("Storage (MB)", cHostStorage),
                row("Bandwidth",     cHostBw)),
            sectionCard("Virtual Machine",
                row("VMs",           cVMs),   row("VM MIPS",       cVmMips),
                row("VM CPU Cores",  cVmPes), row("VM RAM (MB)",   cVmRam),
                row("VM Storage",    cVmStorage), row("VM BW",     cVmBw)),
            sectionCard("Cloudlet",
                row("Cloudlets",     cCL),   row("Length (MI)",    cClLen),
                row("File Size",     cClFile), row("Output Size",  cClOut),
                row("CPU Cores req", cClPes))
        );
        cfgRow.setFillHeight(true);
        for (int i = 0; i < 3; i++) HBox.setHgrow(cfgRow.getChildren().get(i), Priority.ALWAYS);

        // Performance section
        pOk = sv(); pFail = sv(); pAvg = sv(); pMin = sv(); pMax = sv(); pTotal = sv(); pRate = sv();
        VBox perfCard = sectionCard("Performance",
            row("Successful Cloudlets", pOk),  row("Failed Cloudlets",  pFail),
            row("Success Rate",         pRate), row("Avg Exec Time (s)", pAvg),
            row("Min Exec Time (s)",    pMin),  row("Max Exec Time (s)", pMax),
            row("Total CPU Time (s)",   pTotal));

        // Results table
        resultTable = new TableView<>();
        resultTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        resultTable.setPrefHeight(260);

        TableColumn<CloudletResult, Integer> colId = new TableColumn<>("Cloudlet ID");
        colId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getCloudletId()).asObject());
        TableColumn<CloudletResult, Integer> colVm = new TableColumn<>("VM ID");
        colVm.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getVmId()).asObject());
        TableColumn<CloudletResult, String> colSt = new TableColumn<>("Status");
        colSt.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        TableColumn<CloudletResult, Double> colEx = new TableColumn<>("Exec Time (s)");
        colEx.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().getExecutionTime()).asObject());
        colEx.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                setText((empty || v == null) ? null : String.format("%.2f", v));
            }
        });
        resultTable.getColumns().addAll(colId, colVm, colSt, colEx);

        VBox tableCard = new VBox(8,
            new Label("Cloudlet Results") {{
                setStyle(Styles.FONT_H3 + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";"); }},
            resultTable);
        tableCard.setPadding(new Insets(16));
        tableCard.setStyle(Styles.CARD);

        getChildren().addAll(expTitle, expTimestamp, headerRow, cfgRow,
            new HBox(16, perfCard) {{ setFillHeight(true); }}, tableCard);
    }

    /** Populate all labels from the given experiment. */
    public void load(Experiment exp) {
        SimulationConfig c = exp.getConfig();
        expTitle.setText(exp.getName());
        expTimestamp.setText("Run at: " + exp.getTimestampStr());

        cDC.setText(String.valueOf(c.getDatacenterCount()));
        cHosts.setText(String.valueOf(c.getHostCount()));
        cHostPes.setText(c.getHostPes() + " cores");
        cHostMips.setText(String.format("%,d MIPS", c.getHostMips()));
        cHostRam.setText(String.format("%,d MB", c.getHostRamMb()));
        cHostStorage.setText(String.format("%,d MB", c.getHostStorageMb()));
        cHostBw.setText(String.format("%,d Mbps", c.getHostBwMbps()));

        cVMs.setText(String.valueOf(c.getVmCount()));
        cVmMips.setText(String.format("%,d MIPS", c.getVmMips()));
        cVmPes.setText(c.getVmPes() + " cores");
        cVmRam.setText(String.format("%,d MB", c.getVmRamMb()));
        cVmStorage.setText(String.format("%,d MB", c.getVmStorageMb()));
        cVmBw.setText(String.format("%,d Mbps", c.getVmBwMbps()));

        cCL.setText(String.valueOf(c.getCloudletCount()));
        cClLen.setText(String.format("%,d MI", c.getCloudletLength()));
        cClFile.setText(String.format("%,d MB", c.getCloudletFileSize()));
        cClOut.setText(String.format("%,d MB", c.getCloudletOutputSize()));
        cClPes.setText(c.getCloudletPes() + " core(s)");

        pOk.setText(exp.getSuccessfulCloudlets() + " / " + exp.getResults().size());
        pOk.setStyle("-fx-font-size:13px;-fx-font-weight:bold;-fx-text-fill:" + Styles.SUCCESS + ";");
        pFail.setText(String.valueOf(exp.getFailedCloudlets()));
        pFail.setStyle("-fx-font-size:13px;-fx-text-fill:" +
            (exp.getFailedCloudlets() > 0 ? Styles.DANGER : Styles.TEXT_PRIMARY) + ";");
        pRate.setText(String.format("%.0f%%", exp.getSuccessRate()));
        pAvg.setText(String.format("%.2f s", exp.getAvgExecTime()));
        pMin.setText(String.format("%.2f s", exp.getMinExecTime()));
        pMax.setText(String.format("%.2f s", exp.getMaxExecTime()));
        pTotal.setText(String.format("%.2f s", exp.getTotalCpuTime()));

        resultTable.setItems(FXCollections.observableArrayList(exp.getResults()));
    }

    // ── Helpers ───────────────────────────────────────────────────────
    private Label sv() {
        Label l = new Label("—");
        l.setStyle("-fx-font-size:13px;-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");
        return l;
    }
    private HBox row(String key, Label val) {
        Label k = new Label(key + ":"); k.setMinWidth(140);
        k.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_SECONDARY + ";");
        return new HBox(8, k, val);
    }
    private VBox sectionCard(String heading, HBox... rows) {
        Label h = new Label(heading);
        h.setStyle(Styles.FONT_H3 + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");
        Region sep = new Region(); sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color:" + Styles.BORDER + ";");
        VBox.setMargin(sep, new Insets(4,0,6,0));
        VBox card = new VBox(6); card.getChildren().addAll(h, sep);
        card.getChildren().addAll(rows);
        card.setPadding(new Insets(14));
        card.setStyle(Styles.CARD);
        return card;
    }
}