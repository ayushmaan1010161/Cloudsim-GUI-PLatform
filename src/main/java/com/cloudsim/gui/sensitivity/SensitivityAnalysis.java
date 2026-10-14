package com.cloudsim.gui.sensitivity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * SensitivityAnalysis
 *
 * A complete record of one sensitivity analysis sweep.
 * Stored in SensitivityStore (in-memory, session-scoped).
 *
 * Architecture:
 *   SensitivityConfig -> SensitivityRunner -> SensitivityAnalysis -> SensitivityStore
 */
public class SensitivityAnalysis {

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final int                      id;
    private       String                   name;
    private final LocalDateTime            timestamp;
    private final SensitivityConfig        config;
    private final List<SensitivityRunResult> runResults;

    public SensitivityAnalysis(int id,
                                SensitivityConfig config,
                                List<SensitivityRunResult> runResults) {
        this.id         = id;
        this.config     = config;
        this.runResults = List.copyOf(runResults);
        this.timestamp  = LocalDateTime.now();
        this.name       = "Analysis " + id + " — " + config.getParameter().getDisplayName();
    }

    // ── Identity ──────────────────────────────────────────────────────
    public int    getId()           { return id; }
    public String getName()         { return name; }
    public void   setName(String n) { if (n != null && !n.isBlank()) this.name = n.trim(); }
    public String getTimestampStr() { return timestamp.format(FMT); }

    // ── Config ────────────────────────────────────────────────────────
    public SensitivityConfig        getConfig()     { return config; }
    public List<SensitivityRunResult> getRunResults() { return runResults; }

    // ── Derived summary stats ─────────────────────────────────────────

    public int completedRuns() {
        return (int) runResults.stream().filter(r -> !r.isFailed()).count();
    }

    public int failedRuns() {
        return (int) runResults.stream().filter(SensitivityRunResult::isFailed).count();
    }

    /** Avg exec time of the first successful run (lowest parameter value). */
    public double firstSuccessAvgTime() {
        return runResults.stream().filter(r -> !r.isFailed())
            .findFirst().map(SensitivityRunResult::getAvgExecTime).orElse(0.0);
    }

    /** Avg exec time of the last successful run (highest parameter value). */
    public double lastSuccessAvgTime() {
        List<SensitivityRunResult> ok = runResults.stream()
            .filter(r -> !r.isFailed()).toList();
        return ok.isEmpty() ? 0.0 : ok.get(ok.size() - 1).getAvgExecTime();
    }

    /**
     * Percentage change in average execution time from first to last
     * successful run. Positive = increased, Negative = decreased.
     */
    public double percentChangeAvgTime() {
        double first = firstSuccessAvgTime();
        double last  = lastSuccessAvgTime();
        if (first == 0) return 0;
        return ((last - first) / first) * 100.0;
    }

    /** Run result with the lowest observed average execution time. */
    public SensitivityRunResult bestAvgTimeRun() {
        return runResults.stream().filter(r -> !r.isFailed())
            .min(java.util.Comparator.comparingDouble(SensitivityRunResult::getAvgExecTime))
            .orElse(null);
    }

    /** Run result with the highest observed average execution time. */
    public SensitivityRunResult worstAvgTimeRun() {
        return runResults.stream().filter(r -> !r.isFailed())
            .max(java.util.Comparator.comparingDouble(SensitivityRunResult::getAvgExecTime))
            .orElse(null);
    }
}