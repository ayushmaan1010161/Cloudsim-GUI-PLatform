package com.cloudsim.gui.experiment;

import com.cloudsim.gui.model.CloudletResult;
import com.cloudsim.gui.model.SimulationConfig;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Experiment
 *
 * Immutable record of one complete simulation run.
 * Contains the configuration used, the raw results, and
 * pre-computed performance metrics so views never re-calculate.
 *
 * Architecture position:
 *   Simulation Engine -> CloudletResult list -> Experiment -> ExperimentStore
 */
public class Experiment {

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ── Identity ──────────────────────────────────────────────────────
    private final int           id;
    private       String        name;          // mutable for rename
    private final LocalDateTime timestamp;

    // ── What was configured ───────────────────────────────────────────
    private final SimulationConfig config;

    // ── What the simulation produced ──────────────────────────────────
    private final List<CloudletResult> results;

    // ── Pre-computed metrics ──────────────────────────────────────────
    private final long   successfulCloudlets;
    private final long   failedCloudlets;
    private final double avgExecTime;
    private final double minExecTime;
    private final double maxExecTime;
    private final double totalCpuTime;
    private final double successRate;

    public Experiment(int id, SimulationConfig config, List<CloudletResult> results) {
        this.id        = id;
        this.name      = "Experiment " + id;
        this.timestamp = LocalDateTime.now();
        this.config    = config;
        this.results   = List.copyOf(results);   // defensive copy

        // Pre-compute metrics once
        this.successfulCloudlets = results.stream()
            .filter(r -> "SUCCESS".equalsIgnoreCase(r.getStatus())).count();
        this.failedCloudlets  = results.size() - successfulCloudlets;
        this.avgExecTime      = results.stream()
            .mapToDouble(CloudletResult::getExecutionTime).average().orElse(0);
        this.minExecTime      = results.stream()
            .mapToDouble(CloudletResult::getExecutionTime).min().orElse(0);
        this.maxExecTime      = results.stream()
            .mapToDouble(CloudletResult::getExecutionTime).max().orElse(0);
        this.totalCpuTime     = results.stream()
            .mapToDouble(CloudletResult::getExecutionTime).sum();
        this.successRate      = results.isEmpty() ? 0
            : (successfulCloudlets * 100.0 / results.size());
    }

    // ── Getters ───────────────────────────────────────────────────────

    public int               getId()                  { return id; }
    public String            getName()                { return name; }
    public String            getTimestampStr()        { return timestamp.format(FMT); }
    public SimulationConfig  getConfig()              { return config; }
    public List<CloudletResult> getResults()          { return results; }
    public long              getSuccessfulCloudlets() { return successfulCloudlets; }
    public long              getFailedCloudlets()     { return failedCloudlets; }
    public double            getAvgExecTime()         { return avgExecTime; }
    public double            getMinExecTime()         { return minExecTime; }
    public double            getMaxExecTime()         { return maxExecTime; }
    public double            getTotalCpuTime()        { return totalCpuTime; }
    public double            getSuccessRate()         { return successRate; }

    /** Allows the user to rename this experiment. */
    public void setName(String name) {
        if (name != null && !name.isBlank()) this.name = name.trim();
    }

    @Override
    public String toString() {
        return String.format("[%d] %s @ %s — %d cloudlets, %.2f s avg",
            id, name, getTimestampStr(), results.size(), avgExecTime);
    }
}