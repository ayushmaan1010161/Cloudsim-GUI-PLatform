package com.cloudsim.gui.experiment;

import java.util.ArrayList;
import java.util.List;

/**
 * ComparisonEngine
 *
 * Pure logic class — no JavaFX, no CloudSim.
 * Produces a ComparisonRow list from a list of experiments.
 * Each ComparisonRow is one metric with a value per experiment.
 *
 * Architecture:
 *   List<Experiment> -> ComparisonEngine -> List<ComparisonRow> -> UI
 */
public class ComparisonEngine {

    private ComparisonEngine() {}

    /**
     * Builds the comparison table rows for the given experiments.
     * Each row represents one metric; each column is one experiment.
     *
     * @param experiments list of experiments to compare (2 or more)
     * @return list of ComparisonRow, one per metric
     */
    public static List<ComparisonRow> compare(List<Experiment> experiments) {
        List<ComparisonRow> rows = new ArrayList<>();

        // ── Configuration metrics ─────────────────────────────────────
        rows.add(row("Datacenters",
            experiments, e -> String.valueOf(e.getConfig().getDatacenterCount())));
        rows.add(row("Hosts",
            experiments, e -> String.valueOf(e.getConfig().getHostCount())));
        rows.add(row("Host CPU Cores",
            experiments, e -> String.valueOf(e.getConfig().getHostPes())));
        rows.add(row("Host MIPS/core",
            experiments, e -> String.format("%,d", e.getConfig().getHostMips())));
        rows.add(row("Host RAM (MB)",
            experiments, e -> String.format("%,d", e.getConfig().getHostRamMb())));
        rows.add(row("VMs",
            experiments, e -> String.valueOf(e.getConfig().getVmCount())));
        rows.add(row("VM MIPS",
            experiments, e -> String.format("%,d", e.getConfig().getVmMips())));
        rows.add(row("VM CPU Cores",
            experiments, e -> String.valueOf(e.getConfig().getVmPes())));
        rows.add(row("VM RAM (MB)",
            experiments, e -> String.format("%,d", e.getConfig().getVmRamMb())));
        rows.add(row("Cloudlets",
            experiments, e -> String.valueOf(e.getConfig().getCloudletCount())));
        rows.add(row("Cloudlet Length (MI)",
            experiments, e -> String.format("%,d", e.getConfig().getCloudletLength())));

        // ── Performance metrics ───────────────────────────────────────
        rows.add(row("Successful Cloudlets",
            experiments, e -> e.getSuccessfulCloudlets() + " / " + e.getResults().size()));
        rows.add(row("Failed Cloudlets",
            experiments, e -> String.valueOf(e.getFailedCloudlets())));
        rows.add(row("Success Rate",
            experiments, e -> String.format("%.0f%%", e.getSuccessRate())));
        rows.add(row("Avg Exec Time (s)",
            experiments, e -> String.format("%.2f", e.getAvgExecTime())));
        rows.add(row("Min Exec Time (s)",
            experiments, e -> String.format("%.2f", e.getMinExecTime())));
        rows.add(row("Max Exec Time (s)",
            experiments, e -> String.format("%.2f", e.getMaxExecTime())));
        rows.add(row("Total CPU Time (s)",
            experiments, e -> String.format("%.2f", e.getTotalCpuTime())));

        return rows;
    }

    private static ComparisonRow row(String metric,
                                     List<Experiment> exps,
                                     java.util.function.Function<Experiment,String> extractor) {
        List<String> values = new ArrayList<>();
        for (Experiment e : exps) values.add(extractor.apply(e));
        return new ComparisonRow(metric, values);
    }

    // ── Inner data class ──────────────────────────────────────────────

    /** One row in the comparison table: a metric name and one value per experiment. */
    public static class ComparisonRow {
        private final String       metric;
        private final List<String> values;   // index matches experiment list

        public ComparisonRow(String metric, List<String> values) {
            this.metric = metric;
            this.values = List.copyOf(values);
        }

        public String       getMetric()              { return metric; }
        public List<String> getValues()              { return values; }
        public String       getValue(int expIndex)   {
            return expIndex < values.size() ? values.get(expIndex) : "—";
        }
    }
}