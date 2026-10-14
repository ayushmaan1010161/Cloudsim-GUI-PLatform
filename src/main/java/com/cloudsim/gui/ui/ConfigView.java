package com.cloudsim.gui.ui;

import com.cloudsim.gui.model.SimulationConfig;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * ConfigView
 * Four-section configuration page. Validates all inputs and calls
 * onReady with a fully populated SimulationConfig.
 */
public class ConfigView extends VBox {

    private final Consumer<SimulationConfig> onReady;

    // ── Datacenter ────────────────────────────────────────────────────
    private TextField tfDcCount;

    // ── Host ──────────────────────────────────────────────────────────
    private TextField tfHostCount, tfHostPes, tfHostMips, tfHostRam, tfHostStorage, tfHostBw;

    // ── VM ────────────────────────────────────────────────────────────
    private TextField tfVmCount, tfVmMips, tfVmPes, tfVmRam, tfVmStorage, tfVmBw;

    // ── Cloudlet ──────────────────────────────────────────────────────
    private TextField tfClCount, tfClLength, tfClFileSize, tfClOutputSize, tfClPes;

    private final List<TextField> allFields = new ArrayList<>();
    private Label statusLabel;

    public ConfigView(Consumer<SimulationConfig> onReady) {
        this.onReady = onReady;
        setSpacing(18);
        setPadding(new Insets(28));
        setStyle("-fx-background-color:" + Styles.BG_CONTENT + ";");
        build();
    }

    private void build() {
        Label title = new Label("Configuration");
        title.setStyle(Styles.FONT_TITLE + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");

        Label desc = new Label(
            "Set the parameters for your cloud simulation. Hover over any field for an explanation.");
        desc.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_SECONDARY + ";");
        desc.setWrapText(true);

        // Hierarchy hint banner
        HBox banner = buildBanner();

        // Two-column card layout
        HBox topRow = new HBox(16, buildDcCard(), buildHostCard());
        topRow.setFillHeight(true);
        HBox.setHgrow(topRow.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(topRow.getChildren().get(1), Priority.ALWAYS);

        HBox botRow = new HBox(16, buildVmCard(), buildCloudletCard());
        botRow.setFillHeight(true);
        HBox.setHgrow(botRow.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(botRow.getChildren().get(1), Priority.ALWAYS);

        // Status + Save button
        statusLabel = new Label("");
        statusLabel.setWrapText(true);
        statusLabel.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.DANGER + ";");

        Button saveBtn = new Button("Save & Go to Simulation");
        saveBtn.setStyle(Styles.BTN_PRIMARY);
        saveBtn.setOnMouseEntered(e -> saveBtn.setStyle(Styles.BTN_PRIMARY_HOVER));
        saveBtn.setOnMouseExited(e  -> saveBtn.setStyle(Styles.BTN_PRIMARY));
        saveBtn.setOnAction(e -> validate());

        HBox btnRow = new HBox(saveBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        getChildren().addAll(title, desc, banner, topRow, botRow, statusLabel, btnRow);
    }

    // ── Section cards ─────────────────────────────────────────────────

    private VBox buildDcCard() {
        GridPane g = grid();
        tfDcCount = row(g, 0, "Number of Datacenters", "1", "",
            "How many independent datacenters to create.\nEach datacenter holds the hosts you configure.\nFor a basic simulation, 1 is sufficient.");
        return card("Datacenter",
            "A virtual data centre that contains physical machines (Hosts).", g);
    }

    private VBox buildHostCard() {
        GridPane g = grid();
        tfHostCount   = row(g, 0, "Number of Hosts",     "1",       "",
            "Physical machines inside each datacenter.\nEach host independently provides CPU, RAM, Storage and Bandwidth.");
        tfHostPes     = row(g, 1, "CPU Cores / PEs",     "4",       "cores",
            "Number of CPU cores on each host.\nVMs share these cores.\nTotal host PEs must cover all VM PE requests.");
        tfHostMips    = row(g, 2, "CPU MIPS per Core",   "10000",   "MIPS",
            "Processing speed of each CPU core in Million Instructions Per Second.\nVM MIPS must not exceed this value.\nExample: 10000 MIPS is a standard benchmark.");
        tfHostRam     = row(g, 3, "RAM",                 "32768",   "MB",
            "Total memory on each host in megabytes.\nVM RAM must not exceed this.\nExample: 32768 MB = 32 GB.");
        tfHostStorage = row(g, 4, "Storage",             "1000000", "MB",
            "Disk storage on each host in megabytes.\nExample: 1000000 MB = 1 TB.");
        tfHostBw      = row(g, 5, "Bandwidth",           "100000",  "Mbps",
            "Network bandwidth of each host in megabits per second.\nExample: 100000 Mbps = 100 Gbps.");
        return card("Host", "Physical machine providing CPU, RAM, Storage and Bandwidth to VMs.", g);
    }

    private VBox buildVmCard() {
        GridPane g = grid();
        tfVmCount   = row(g, 0, "Number of VMs",    "2",     "",
            "How many virtual machines to create.\nCloudlets are distributed across these VMs.");
        tfVmMips    = row(g, 1, "VM MIPS",           "4000",  "MIPS",
            "Processing capacity assigned to each VM.\nMust not exceed Host CPU MIPS per core.\nExample: 4000 MIPS.");
        tfVmPes     = row(g, 2, "VM CPU Cores",      "2",     "cores",
            "CPU cores assigned to each VM.\nMust not exceed Host CPU Cores per host.");
        tfVmRam     = row(g, 3, "VM RAM",             "4096",  "MB",
            "Memory allocated to each VM.\nMust not exceed Host RAM.\nExample: 4096 MB = 4 GB.");
        tfVmStorage = row(g, 4, "VM Storage",         "50000", "MB",
            "Disk space allocated to each VM.\nMust not exceed Host Storage.");
        tfVmBw      = row(g, 5, "VM Bandwidth",       "20000", "Mbps",
            "Network bandwidth allocated to each VM.\nMust not exceed Host Bandwidth.");
        return card("Virtual Machine", "A VM runs on a Host and executes Cloudlets.", g);
    }

    private VBox buildCloudletCard() {
        GridPane g = grid();
        tfClCount      = row(g, 0, "Number of Cloudlets", "4",     "",
            "How many tasks (cloudlets) to submit.\nThey are automatically distributed across available VMs.");
        tfClLength     = row(g, 1, "Cloudlet Length",      "20000", "MI",
            "Instructions each cloudlet must execute (Million Instructions).\nExecution Time ≈ Length / (VM MIPS × 0.5)\nExample: 20000 MI on 4000 MIPS VM ≈ 10 seconds.");
        tfClFileSize   = row(g, 2, "File Size",             "300",   "MB",
            "Input file transferred to the VM before the cloudlet starts.\nSimulates reading data before processing.");
        tfClOutputSize = row(g, 3, "Output Size",           "300",   "MB",
            "Result file produced after the cloudlet finishes.\nSimulates writing results after processing.");
        tfClPes        = row(g, 4, "CPU Cores Required",    "1",     "cores",
            "CPU cores each cloudlet needs to run.\nMust not exceed VM CPU Cores.\nFor simple tasks, 1 core is sufficient.");
        return card("Cloudlet", "A task (job) submitted to a VM for execution.", g);
    }

    // ── Validation ────────────────────────────────────────────────────

    private void validate() {
        allFields.forEach(tf -> tf.setStyle(Styles.INPUT));
        statusLabel.setText("");

        List<String> errors = new ArrayList<>();

        int  dcCount      = pi(tfDcCount,      "Number of Datacenters",  1, 10,    errors);
        int  hostCount    = pi(tfHostCount,     "Number of Hosts",        1, 100,   errors);
        int  hostPes      = pi(tfHostPes,       "Host CPU Cores",         1, 512,   errors);
        long hostMips     = pl(tfHostMips,      "Host CPU MIPS",          100, 1_000_000, errors);
        long hostRam      = pl(tfHostRam,       "Host RAM (MB)",          512, 10_000_000, errors);
        long hostStorage  = pl(tfHostStorage,   "Host Storage (MB)",      1000, Long.MAX_VALUE, errors);
        long hostBw       = pl(tfHostBw,        "Host Bandwidth (Mbps)",  100, Long.MAX_VALUE, errors);
        int  vmCount      = pi(tfVmCount,       "Number of VMs",          1, 10000, errors);
        long vmMips       = pl(tfVmMips,        "VM MIPS",                100, 1_000_000, errors);
        int  vmPes        = pi(tfVmPes,         "VM CPU Cores",           1, 512,   errors);
        long vmRam        = pl(tfVmRam,         "VM RAM (MB)",            128, 10_000_000, errors);
        long vmStorage    = pl(tfVmStorage,     "VM Storage (MB)",        100, Long.MAX_VALUE, errors);
        long vmBw         = pl(tfVmBw,          "VM Bandwidth (Mbps)",    10, Long.MAX_VALUE, errors);
        int  clCount      = pi(tfClCount,       "Number of Cloudlets",    1, 50000, errors);
        long clLength     = pl(tfClLength,      "Cloudlet Length (MI)",   1, Long.MAX_VALUE, errors);
        long clFileSize   = pl(tfClFileSize,    "File Size (MB)",         1, Long.MAX_VALUE, errors);
        long clOutputSize = pl(tfClOutputSize,  "Output Size (MB)",       1, Long.MAX_VALUE, errors);
        int  clPes        = pi(tfClPes,         "Cloudlet CPU Cores",     1, 512,   errors);

        if (errors.isEmpty()) {
            long totalHostPes = (long) dcCount * hostCount * hostPes;
            long totalVmPes   = (long) vmCount * vmPes;
            if (totalVmPes > totalHostPes) {
                markErr(tfVmCount); markErr(tfVmPes);
                errors.add("Total VM CPU cores needed (" + totalVmPes + ") exceeds total host PEs ("
                    + totalHostPes + "). Reduce VMs or VM CPU Cores, or increase Host CPU Cores.");
            }
            if (vmMips > hostMips)    { markErr(tfVmMips);
                errors.add("VM MIPS (" + vmMips + ") cannot exceed Host CPU MIPS (" + hostMips + ")."); }
            if (vmPes > hostPes)      { markErr(tfVmPes);
                errors.add("VM CPU Cores (" + vmPes + ") cannot exceed Host CPU Cores (" + hostPes + ")."); }
            if (vmRam > hostRam)      { markErr(tfVmRam);
                errors.add("VM RAM (" + vmRam + " MB) cannot exceed Host RAM (" + hostRam + " MB)."); }
            if (vmStorage > hostStorage) { markErr(tfVmStorage);
                errors.add("VM Storage (" + vmStorage + " MB) cannot exceed Host Storage (" + hostStorage + " MB)."); }
            if (vmBw > hostBw)        { markErr(tfVmBw);
                errors.add("VM Bandwidth (" + vmBw + " Mbps) cannot exceed Host Bandwidth (" + hostBw + " Mbps)."); }
            if (clPes > vmPes)        { markErr(tfClPes);
                errors.add("Cloudlet CPU Cores (" + clPes + ") cannot exceed VM CPU Cores (" + vmPes + ")."); }
        }

        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            statusLabel.setText("Please correct the highlighted fields.");
            return;
        }

        onReady.accept(new SimulationConfig(
            dcCount, hostCount, hostPes, hostMips, hostRam, hostStorage, hostBw,
            vmCount, vmMips, vmPes, vmRam, vmStorage, vmBw,
            clCount, clLength, clFileSize, clOutputSize, clPes
        ));
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private VBox card(String heading, String description, GridPane content) {
        Label h = new Label(heading);
        h.setStyle(Styles.FONT_H3 + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");
        Label d = new Label(description);
        d.setStyle(Styles.FONT_SMALL + "-fx-text-fill:" + Styles.TEXT_SECONDARY + ";");
        d.setWrapText(true);
        Region sep = new Region(); sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color:" + Styles.BORDER + ";");
        VBox.setMargin(sep, new Insets(4, 0, 8, 0));

        VBox card = new VBox(4, h, d, sep, content);
        card.setPadding(new Insets(18));
        card.setStyle(Styles.CARD);
        VBox.setVgrow(content, Priority.ALWAYS);
        return card;
    }

    private GridPane grid() {
        GridPane g = new GridPane();
        g.setHgap(10); g.setVgap(9);
        ColumnConstraints c0 = new ColumnConstraints(); c0.setMinWidth(155); c0.setHgrow(Priority.SOMETIMES);
        ColumnConstraints c1 = new ColumnConstraints(); c1.setMinWidth(95);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setMinWidth(42);
        ColumnConstraints c3 = new ColumnConstraints(); c3.setMinWidth(20);
        g.getColumnConstraints().addAll(c0, c1, c2, c3);
        return g;
    }

    private TextField row(GridPane g, int row, String label, String def, String unit, String tip) {
        Label lbl = new Label(label + ":");
        lbl.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");

        TextField tf = new TextField(def);
        tf.setStyle(Styles.INPUT);
        tf.textProperty().addListener((o, ov, nv) -> tf.setStyle(Styles.INPUT));

        Label unitLbl = new Label(unit);
        unitLbl.setStyle(Styles.FONT_SMALL + "-fx-text-fill:" + Styles.TEXT_SECONDARY + ";");

        Tooltip tt = new Tooltip(tip);
        tt.setWrapText(true); tt.setMaxWidth(300);
        tt.setShowDelay(Duration.millis(250));
        Tooltip.install(tf, tt); Tooltip.install(lbl, tt);

        Label icon = new Label("?");
        icon.setStyle(
            "-fx-background-color:#94a3b8;-fx-text-fill:white;" +
            "-fx-font-size:9px;-fx-font-weight:bold;" +
            "-fx-padding:1 4 1 4;-fx-background-radius:7;-fx-cursor:hand;");
        Tooltip.install(icon, tt);

        g.add(lbl,     0, row);
        g.add(tf,      1, row);
        g.add(unitLbl, 2, row);
        g.add(icon,    3, row);

        allFields.add(tf);
        return tf;
    }

    private HBox buildBanner() {
        HBox b = new HBox(8);
        b.setPadding(new Insets(10, 14, 10, 14));
        b.setStyle("-fx-background-color:#eff6ff;-fx-border-color:#bfdbfe;" +
                   "-fx-border-radius:7;-fx-background-radius:7;");
        b.setAlignment(Pos.CENTER_LEFT);
        Label intro = new Label("Hierarchy:");
        intro.setStyle("-fx-font-weight:bold;-fx-font-size:12px;-fx-text-fill:#1e40af;");
        b.getChildren().addAll(intro, chip("Datacenter"), arr(), chip("Host"),
            arr(), chip("VM"), arr(), chip("Cloudlet"));
        return b;
    }
    private Label chip(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-background-color:#3b82f6;-fx-text-fill:white;" +
                   "-fx-font-size:11px;-fx-font-weight:bold;" +
                   "-fx-padding:3 9 3 9;-fx-background-radius:4;");
        return l;
    }
    private Label arr() {
        Label l = new Label("→");
        l.setStyle("-fx-font-size:13px;-fx-text-fill:#1e40af;-fx-font-weight:bold;");
        return l;
    }

    private void markErr(TextField tf) {
        tf.setStyle(Styles.INPUT_ERROR);
    }

    private void showErrorDialog(List<String> errors) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Configuration Problem");
        a.setHeaderText("Please fix the following before running the simulation:");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < errors.size(); i++) sb.append(i+1).append(". ").append(errors.get(i)).append("\n\n");
        TextArea ta = new TextArea(sb.toString().trim());
        ta.setEditable(false); ta.setWrapText(true);
        ta.setPrefRowCount(Math.min(errors.size() * 3, 12)); ta.setPrefWidth(500);
        a.getDialogPane().setContent(ta); a.getDialogPane().setPrefWidth(540);
        a.showAndWait();
    }

    private int pi(TextField tf, String name, int min, int max, List<String> errs) {
        String s = tf.getText() == null ? "" : tf.getText().trim();
        if (s.isEmpty()) { markErr(tf); errs.add("\"" + name + "\" is empty. Enter a whole number."); return 0; }
        if (s.contains(".")) { markErr(tf); errs.add("\"" + name + "\" must be a whole number, not a decimal."); return 0; }
        try {
            int v = Integer.parseInt(s);
            if (v <= 0) { markErr(tf); errs.add("\"" + name + "\" must be greater than zero."); return 0; }
            if (v < min) { markErr(tf); errs.add("\"" + name + "\" must be at least " + min + "."); return 0; }
            if (v > max) { markErr(tf); errs.add("\"" + name + "\" must be at most " + max + "."); return 0; }
            return v;
        } catch (NumberFormatException e) {
            markErr(tf); errs.add("\"" + name + "\": \"" + s + "\" is not a valid number."); return 0;
        }
    }

    private long pl(TextField tf, String name, long min, long max, List<String> errs) {
        String s = tf.getText() == null ? "" : tf.getText().trim();
        if (s.isEmpty()) { markErr(tf); errs.add("\"" + name + "\" is empty. Enter a whole number."); return 0; }
        if (s.contains(".")) { markErr(tf); errs.add("\"" + name + "\" must be a whole number, not a decimal."); return 0; }
        try {
            long v = Long.parseLong(s);
            if (v <= 0) { markErr(tf); errs.add("\"" + name + "\" must be greater than zero."); return 0; }
            if (v < min) { markErr(tf); errs.add("\"" + name + "\" must be at least " + min + "."); return 0; }
            if (max != Long.MAX_VALUE && v > max) { markErr(tf); errs.add("\"" + name + "\" must be at most " + max + "."); return 0; }
            return v;
        } catch (NumberFormatException e) {
            markErr(tf); errs.add("\"" + name + "\": \"" + s + "\" is not a valid number."); return 0;
        }
    }
}