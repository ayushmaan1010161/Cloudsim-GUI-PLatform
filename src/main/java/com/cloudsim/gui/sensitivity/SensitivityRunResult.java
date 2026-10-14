package com.cloudsim.gui.sensitivity;

import com.cloudsim.gui.model.CloudletResult;
import com.cloudsim.gui.model.SimulationConfig;

import java.util.List;

/**
 * SensitivityRunResult
 *
 * Stores the outcome of one sweep run:
 *   - the parameter value that was used
 *   - the SimulationConfig that was built for this run
 *   - the raw CloudletResult list
 *   - pre-computed metrics
 *
 * Metrics are computed once at construction from actual CloudSim results.
 */
public class SensitivityRunResult {

    private final long             parameterValue;
    private final SimulationConfig config;
    private final List<CloudletResult> results;
    private final String           errorMessage;   // non-null if this run failed

    // Pre-computed metrics
    private final int    totalCloudlets;
    private final long   successfulCloudlets;
    private final long   failedCloudlets;
    private final double successRate;
    private final double avgExecTime;
    private final double minExecTime;
    private final double maxExecTime;
    private final double totalExecTime;

    /** Constructor for a successful run. */
    public SensitivityRunResult(long parameterValue,
                                 SimulationConfig config,
                                 List<CloudletResult> results) {
        this.parameterValue = parameterValue;
        this.config         = config;
        this.results        = List.copyOf(results);
        this.errorMessage   = null;

        this.totalCloudlets      = results.size();
        this.successfulCloudlets = results.stream()
            .filter(r -> "SUCCESS".equalsIgnoreCase(r.getStatus())).count();
        this.failedCloudlets  = totalCloudlets - successfulCloudlets;
        this.successRate      = totalCloudlets == 0 ? 0
            : (successfulCloudlets * 100.0 / totalCloudlets);
        this.avgExecTime      = results.stream()
            .mapToDouble(CloudletResult::getExecutionTime).average().orElse(0);
        this.minExecTime      = results.stream()
            .mapToDouble(CloudletResult::getExecutionTime).min().orElse(0);
        this.maxExecTime      = results.stream()
            .mapToDouble(CloudletResult::getExecutionTime).max().orElse(0);
        this.totalExecTime    = results.stream()
            .mapToDouble(CloudletResult::getExecutionTime).sum();
    }

    /** Constructor for a failed run. */
    public SensitivityRunResult(long parameterValue,
                                 SimulationConfig config,
                                 String errorMessage) {
        this.parameterValue      = parameterValue;
        this.config              = config;
        this.results             = List.of();
        this.errorMessage        = errorMessage;
        this.totalCloudlets      = 0;
        this.successfulCloudlets = 0;
        this.failedCloudlets     = 0;
        this.successRate         = 0;
        this.avgExecTime         = 0;
        this.minExecTime         = 0;
        this.maxExecTime         = 0;
        this.totalExecTime       = 0;
    }

    public boolean isFailed()            { return errorMessage != null; }
    public long    getParameterValue()   { return parameterValue; }
    public SimulationConfig getConfig()  { return config; }
    public List<CloudletResult> getResults() { return results; }
    public String  getErrorMessage()     { return errorMessage; }
    public int     getTotalCloudlets()   { return totalCloudlets; }
    public long    getSuccessfulCloudlets() { return successfulCloudlets; }
    public long    getFailedCloudlets()  { return failedCloudlets; }
    public double  getSuccessRate()      { return successRate; }
    public double  getAvgExecTime()      { return avgExecTime; }
    public double  getMinExecTime()      { return minExecTime; }
    public double  getMaxExecTime()      { return maxExecTime; }
    public double  getTotalExecTime()    { return totalExecTime; }
}