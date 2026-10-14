package com.cloudsim.gui.ui;

import com.cloudsim.gui.model.CloudletResult;
import com.cloudsim.gui.model.SimulationConfig;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.util.List;

/**
 * SummaryView
 * Full structured summary: Datacenter/Host, VM, Cloudlet, Performance.
 */
public class SummaryView extends VBox {

    // DC/Host
    private Label sDc, sHosts, sPes, sMips, sRam, sStorage, sBw;
    // VM
    private Label sVmCount, sVmMips, sVmPes, sVmRam, sVmStorage, sVmBw;
    // Cloudlet
    private Label sClCount, sClLen, sClFile, sClOut, sClPes;
    // Performance
    private Label sPerfOk, sPerfFail, sPerfAvg, sPerfMin, sPerfMax, sPerfTotal;
    private Label emptyNote;

    public SummaryView() {
        setSpacing(0);
        setPadding(new Insets(28));
        setStyle("-fx-background-color:" + Styles.BG_CONTENT + ";");
        build();
    }

    private void build() {
        Label title = new Label("Simulation Summary");
        title.setStyle(Styles.FONT_TITLE + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");
        VBox.setMargin(title, new Insets(0, 0, 18, 0));

        emptyNote = new Label("No simulation has been run yet. Results will appear here after running.");
        emptyNote.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_SECONDARY + ";-fx-font-style:italic;");
        VBox.setMargin(emptyNote, new Insets(0, 0, 12, 0));

        // ── Datacenter & Host ─────────────────────────────────────────
        sDc = sv("—"); sHosts = sv("—"); sPes = sv("—"); sMips = sv("—");
        sRam = sv("—"); sStorage = sv("—"); sBw = sv("—");
        VBox dcCard = sectionCard("Datacenter & Host",
            row("Datacenters",         sDc),
            row("Hosts per Datacenter",sHosts),
            row("CPU Cores per Host",  sPes),
            row("CPU MIPS per Core",   sMips),
            row("Host RAM",            sRam),
            row("Host Storage",        sStorage),
            row("Host Bandwidth",      sBw)
        );

        // ── Virtual Machines ──────────────────────────────────────────
        sVmCount = sv("—"); sVmMips = sv("—"); sVmPes = sv("—");
        sVmRam = sv("—"); sVmStorage = sv("—"); sVmBw = sv("—");
        VBox vmCard = sectionCard("Virtual Machines",
            row("Number of VMs",    sVmCount),
            row("VM MIPS",          sVmMips),
            row("VM CPU Cores",     sVmPes),
            row("VM RAM",           sVmRam),
            row("VM Storage",       sVmStorage),
            row("VM Bandwidth",     sVmBw)
        );

        // ── Cloudlets ─────────────────────────────────────────────────
        sClCount = sv("—"); sClLen = sv("—"); sClFile = sv("—");
        sClOut = sv("—"); sClPes = sv("—");
        VBox clCard = sectionCard("Cloudlets",
            row("Number of Cloudlets",  sClCount),
            row("Cloudlet Length",       sClLen),
            row("File Size",             sClFile),
            row("Output Size",           sClOut),
            row("CPU Cores Required",    sClPes)
        );

        // ── Performance ───────────────────────────────────────────────
        sPerfOk = sv("—"); sPerfFail = sv("—"); sPerfAvg = sv("—");
        sPerfMin = sv("—"); sPerfMax = sv("—"); sPerfTotal = sv("—");
        VBox perfCard = sectionCard("Performance",
            row("Successful Cloudlets",  sPerfOk),
            row("Failed Cloudlets",      sPerfFail),
            row("Average Exec Time",     sPerfAvg),
            row("Minimum Exec Time",     sPerfMin),
            row("Maximum Exec Time",     sPerfMax),
            row("Total CPU Time",        sPerfTotal)
        );

        // Two-column layout
        HBox topRow = new HBox(16, dcCard, vmCard);
        topRow.setFillHeight(true);
        HBox.setHgrow(dcCard, Priority.ALWAYS);
        HBox.setHgrow(vmCard, Priority.ALWAYS);

        HBox botRow = new HBox(16, clCard, perfCard);
        botRow.setFillHeight(true);
        HBox.setHgrow(clCard, Priority.ALWAYS);
        HBox.setHgrow(perfCard, Priority.ALWAYS);
        VBox.setMargin(botRow, new Insets(16, 0, 0, 0));

        getChildren().addAll(title, emptyNote, topRow, botRow);
    }

    public void populate(SimulationConfig c, List<CloudletResult> results) {
        emptyNote.setVisible(false);
        emptyNote.setManaged(false);

        sDc.setText(c.getDatacenterCount() + "");
        sHosts.setText(c.getHostCount() + "");
        sPes.setText(c.getHostPes() + " cores");
        sMips.setText(String.format("%,d MIPS", c.getHostMips()));
        sRam.setText(String.format("%,d MB", c.getHostRamMb()));
        sStorage.setText(String.format("%,d MB", c.getHostStorageMb()));
        sBw.setText(String.format("%,d Mbps", c.getHostBwMbps()));

        sVmCount.setText(c.getVmCount() + "");
        sVmMips.setText(String.format("%,d MIPS", c.getVmMips()));
        sVmPes.setText(c.getVmPes() + " cores");
        sVmRam.setText(String.format("%,d MB", c.getVmRamMb()));
        sVmStorage.setText(String.format("%,d MB", c.getVmStorageMb()));
        sVmBw.setText(String.format("%,d Mbps", c.getVmBwMbps()));

        sClCount.setText(c.getCloudletCount() + "");
        sClLen.setText(String.format("%,d MI", c.getCloudletLength()));
        sClFile.setText(String.format("%,d MB", c.getCloudletFileSize()));
        sClOut.setText(String.format("%,d MB", c.getCloudletOutputSize()));
        sClPes.setText(c.getCloudletPes() + " core(s)");

        long ok   = results.stream().filter(r -> "SUCCESS".equalsIgnoreCase(r.getStatus())).count();
        long fail = results.size() - ok;
        double avg   = results.stream().mapToDouble(CloudletResult::getExecutionTime).average().orElse(0);
        double min   = results.stream().mapToDouble(CloudletResult::getExecutionTime).min().orElse(0);
        double max   = results.stream().mapToDouble(CloudletResult::getExecutionTime).max().orElse(0);
        double total = results.stream().mapToDouble(CloudletResult::getExecutionTime).sum();

        sPerfOk.setText(ok + " / " + results.size());
        sPerfOk.setStyle("-fx-font-size:13px;-fx-font-weight:bold;-fx-text-fill:" + Styles.SUCCESS + ";");
        sPerfFail.setText(String.valueOf(fail));
        sPerfFail.setStyle("-fx-font-size:13px;-fx-font-weight:bold;-fx-text-fill:" +
            (fail > 0 ? Styles.DANGER : Styles.TEXT_PRIMARY) + ";");
        sPerfAvg.setText(String.format("%.2f s", avg));
        sPerfMin.setText(String.format("%.2f s", min));
        sPerfMax.setText(String.format("%.2f s", max));
        sPerfTotal.setText(String.format("%.2f s", total));
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private VBox sectionCard(String heading, HBox... rows) {
        Label h = new Label(heading);
        h.setStyle(Styles.FONT_H3 + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");
        Region sep = new Region(); sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color:" + Styles.BORDER + ";");
        VBox.setMargin(sep, new Insets(4, 0, 8, 0));
        VBox card = new VBox(7);
        card.getChildren().addAll(h, sep);
        card.getChildren().addAll(rows);
        card.setPadding(new Insets(16));
        card.setStyle(Styles.CARD);
        return card;
    }

    private HBox row(String key, Label val) {
        Label k = new Label(key + ":");
        k.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_SECONDARY + ";");
        k.setMinWidth(170);
        HBox r = new HBox(10, k, val);
        return r;
    }

    private Label sv(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:13px;-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");
        return l;
    }
}