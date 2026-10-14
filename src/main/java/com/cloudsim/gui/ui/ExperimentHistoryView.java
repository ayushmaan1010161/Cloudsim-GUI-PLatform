package com.cloudsim.gui.ui;

import com.cloudsim.gui.experiment.Experiment;
import com.cloudsim.gui.experiment.ExperimentStore;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.function.Consumer;

/**
 * ExperimentHistoryView
 *
 * Shows a table of all saved experiments.
 * Supports: view detail, rename, select for comparison.
 */
public class ExperimentHistoryView extends VBox {

    private final Consumer<String>     nav;
    private final Consumer<Experiment> onOpenDetail;
    private final Consumer<List<Experiment>> onCompare;

    private TableView<Experiment> table;
    private Label emptyNote;

    public ExperimentHistoryView(Consumer<String> nav,
                                  Consumer<Experiment> onOpenDetail,
                                  Consumer<List<Experiment>> onCompare) {
        this.nav          = nav;
        this.onOpenDetail = onOpenDetail;
        this.onCompare    = onCompare;
        setSpacing(18);
        setPadding(new Insets(28));
        setStyle("-fx-background-color:" + Styles.BG_CONTENT + ";");
        build();
    }

    @SuppressWarnings("unchecked")
    private void build() {
        Label title = new Label("Experiment History");
        title.setStyle(Styles.FONT_TITLE + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";");

        Label desc = new Label(
            "Every simulation run is saved as an experiment. " +
            "Select one to view details, rename it, or select multiple to compare.");
        desc.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_SECONDARY + ";");
        desc.setWrapText(true);

        // Action buttons
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle(Styles.BTN_OUTLINE);
        refreshBtn.setOnAction(e -> refresh());

        Button compareBtn = new Button("Compare Selected");
        compareBtn.setStyle(Styles.BTN_PRIMARY);
        compareBtn.setOnMouseEntered(e -> compareBtn.setStyle(Styles.BTN_PRIMARY_HOVER));
        compareBtn.setOnMouseExited(e  -> compareBtn.setStyle(Styles.BTN_PRIMARY));
        compareBtn.setOnAction(e -> compareSelected());

        Button newExpBtn = new Button("+ New Experiment");
        newExpBtn.setStyle(Styles.BTN_SUCCESS);
        newExpBtn.setOnAction(e -> nav.accept("config"));

        HBox btnRow = new HBox(10, refreshBtn, compareBtn, newExpBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        // Table
        table = new TableView<>();
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(360);

        TableColumn<Experiment, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getId())));
        colId.setMaxWidth(50);

        TableColumn<Experiment, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));

        TableColumn<Experiment, String> colTime = new TableColumn<>("Date / Time");
        colTime.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTimestampStr()));

        TableColumn<Experiment, String> colDC = new TableColumn<>("DCs");
        colDC.setCellValueFactory(d -> new SimpleStringProperty(
            String.valueOf(d.getValue().getConfig().getDatacenterCount())));
        colDC.setMaxWidth(50);

        TableColumn<Experiment, String> colHosts = new TableColumn<>("Hosts");
        colHosts.setCellValueFactory(d -> new SimpleStringProperty(
            String.valueOf(d.getValue().getConfig().getHostCount())));
        colHosts.setMaxWidth(55);

        TableColumn<Experiment, String> colVMs = new TableColumn<>("VMs");
        colVMs.setCellValueFactory(d -> new SimpleStringProperty(
            String.valueOf(d.getValue().getConfig().getVmCount())));
        colVMs.setMaxWidth(50);

        TableColumn<Experiment, String> colCL = new TableColumn<>("Cloudlets");
        colCL.setCellValueFactory(d -> new SimpleStringProperty(
            String.valueOf(d.getValue().getConfig().getCloudletCount())));
        colCL.setMaxWidth(70);

        TableColumn<Experiment, String> colAvg = new TableColumn<>("Avg Exec (s)");
        colAvg.setCellValueFactory(d -> new SimpleStringProperty(
            String.format("%.2f", d.getValue().getAvgExecTime())));

        TableColumn<Experiment, String> colOk = new TableColumn<>("Successful");
        colOk.setCellValueFactory(d -> new SimpleStringProperty(
            d.getValue().getSuccessfulCloudlets() + " / " + d.getValue().getResults().size()));

        TableColumn<Experiment, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(
            d.getValue().getFailedCloudlets() == 0 ? "Complete" : "Partial"));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); setStyle(""); return; }
                setText(v);
                setStyle("Complete".equals(v)
                    ? "-fx-text-fill:" + Styles.SUCCESS + ";-fx-font-weight:bold;"
                    : "-fx-text-fill:" + Styles.WARNING + ";-fx-font-weight:bold;");
            }
        });

        // Actions column: View + Rename
        TableColumn<Experiment, Void> colActions = new TableColumn<>("Actions");
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn   = new Button("View");
            private final Button renameBtn = new Button("Rename");
            {
                viewBtn.setStyle(Styles.BTN_OUTLINE + "-fx-font-size:11px;-fx-padding:4 10 4 10;");
                renameBtn.setStyle("-fx-background-color:transparent;-fx-text-fill:#64748b;" +
                    "-fx-font-size:11px;-fx-cursor:hand;-fx-padding:4 8 4 8;");
                viewBtn.setOnAction(e -> {
                    Experiment exp = getTableView().getItems().get(getIndex());
                    onOpenDetail.accept(exp);
                });
                renameBtn.setOnAction(e -> {
                    Experiment exp = getTableView().getItems().get(getIndex());
                    TextInputDialog dlg = new TextInputDialog(exp.getName());
                    dlg.setTitle("Rename Experiment");
                    dlg.setHeaderText("Enter a new name for this experiment:");
                    dlg.setContentText("Name:");
                    dlg.showAndWait().ifPresent(name -> {
                        exp.setName(name);
                        refresh();
                    });
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                HBox box = new HBox(6, viewBtn, renameBtn);
                box.setAlignment(Pos.CENTER_LEFT);
                setGraphic(box);
            }
        });

        table.getColumns().addAll(
            colId, colName, colTime, colDC, colHosts, colVMs,
            colCL, colAvg, colOk, colStatus, colActions);

        emptyNote = new Label(
            "No experiments yet. Run a simulation to create your first experiment.");
        emptyNote.setStyle(Styles.FONT_BODY + "-fx-text-fill:" + Styles.TEXT_SECONDARY +
            ";-fx-font-style:italic;");
        table.setPlaceholder(emptyNote);

        VBox tableCard = new VBox(10,
            new Label("All Experiments") {{
                setStyle(Styles.FONT_H3 + "-fx-text-fill:" + Styles.TEXT_PRIMARY + ";"); }},
            new Label("Tip: Hold Ctrl and click rows to select multiple experiments for comparison.") {{
                setStyle(Styles.FONT_SMALL + "-fx-text-fill:" + Styles.TEXT_SECONDARY + ";"); }},
            table);
        tableCard.setPadding(new Insets(18));
        tableCard.setStyle(Styles.CARD);

        getChildren().addAll(title, desc, btnRow, tableCard);
        refresh();
    }

    public void refresh() {
        table.setItems(FXCollections.observableArrayList(ExperimentStore.getInstance().getAll()));
    }

    private void compareSelected() {
        List<Experiment> selected = table.getSelectionModel().getSelectedItems();
        if (selected.size() < 2) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Select Experiments");
            a.setHeaderText("Please select at least 2 experiments to compare.");
            a.setContentText("Hold Ctrl and click rows to select multiple experiments.");
            a.showAndWait();
            return;
        }
        onCompare.accept(List.copyOf(selected));
    }
}