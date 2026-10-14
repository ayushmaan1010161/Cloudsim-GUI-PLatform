package com.cloudsim.gui.model;

/**
 * CloudletResult
 *
 * A plain data-holder that captures the outcome of a single finished
 * cloudlet. Populated by BasicSimulation after the simulation ends.
 */
public class CloudletResult {

    private final int    cloudletId;
    private final int    vmId;
    private final String status;
    private final double startTime;
    private final double finishTime;
    private final double executionTime;

    public CloudletResult(int cloudletId, int vmId, String status,
                          double startTime, double finishTime, double executionTime) {
        this.cloudletId    = cloudletId;
        this.vmId          = vmId;
        this.status        = status;
        this.startTime     = startTime;
        this.finishTime    = finishTime;
        this.executionTime = executionTime;
    }

    public int    getCloudletId()    { return cloudletId; }
    public int    getVmId()          { return vmId; }
    public String getStatus()        { return status; }
    public double getStartTime()     { return startTime; }
    public double getFinishTime()    { return finishTime; }
    public double getExecutionTime() { return executionTime; }

    @Override
    public String toString() {
        return String.format(
            "Cloudlet %-3d | VM %-3d | %-8s | Start: %7.2f s | Finish: %7.2f s | Exec: %6.2f s",
            cloudletId, vmId, status, startTime, finishTime, executionTime);
    }
}