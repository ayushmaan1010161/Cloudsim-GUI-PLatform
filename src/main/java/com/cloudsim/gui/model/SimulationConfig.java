package com.cloudsim.gui.model;

/**
 * SimulationConfig  (Step 5 - Full Configuration)
 *
 * Immutable value object holding ALL parameters for one CloudSim Plus run.
 * Every field comes from the GUI - nothing is hardcoded in the simulation.
 *
 * New in Step 5 vs Step 4:
 *   - datacenterCount  : number of datacenters
 *   - hostCount        : number of hosts per datacenter
 *   - cloudletFileSize : input file size per cloudlet (MB)
 *   - cloudletOutputSize: output file size per cloudlet (MB)
 *   All other fields were already present; now ALL come from the GUI.
 */
public class SimulationConfig {

    // ── Datacenter ────────────────────────────────────────────────────
    private final int  datacenterCount;

    // ── Host (per Datacenter) ─────────────────────────────────────────
    private final int  hostCount;
    private final int  hostPes;
    private final long hostMips;
    private final long hostRamMb;
    private final long hostStorageMb;
    private final long hostBwMbps;

    // ── Virtual Machine ───────────────────────────────────────────────
    private final int  vmCount;
    private final long vmMips;
    private final int  vmPes;
    private final long vmRamMb;
    private final long vmStorageMb;
    private final long vmBwMbps;

    // ── Cloudlet ──────────────────────────────────────────────────────
    private final int  cloudletCount;
    private final long cloudletLength;
    private final long cloudletFileSize;
    private final long cloudletOutputSize;
    private final int  cloudletPes;

    public SimulationConfig(
            int  datacenterCount,
            int  hostCount,
            int  hostPes,
            long hostMips,
            long hostRamMb,
            long hostStorageMb,
            long hostBwMbps,
            int  vmCount,
            long vmMips,
            int  vmPes,
            long vmRamMb,
            long vmStorageMb,
            long vmBwMbps,
            int  cloudletCount,
            long cloudletLength,
            long cloudletFileSize,
            long cloudletOutputSize,
            int  cloudletPes) {

        this.datacenterCount    = datacenterCount;
        this.hostCount          = hostCount;
        this.hostPes            = hostPes;
        this.hostMips           = hostMips;
        this.hostRamMb          = hostRamMb;
        this.hostStorageMb      = hostStorageMb;
        this.hostBwMbps         = hostBwMbps;
        this.vmCount            = vmCount;
        this.vmMips             = vmMips;
        this.vmPes              = vmPes;
        this.vmRamMb            = vmRamMb;
        this.vmStorageMb        = vmStorageMb;
        this.vmBwMbps           = vmBwMbps;
        this.cloudletCount      = cloudletCount;
        this.cloudletLength     = cloudletLength;
        this.cloudletFileSize   = cloudletFileSize;
        this.cloudletOutputSize = cloudletOutputSize;
        this.cloudletPes        = cloudletPes;
    }

    // ── Getters ───────────────────────────────────────────────────────

    public int  getDatacenterCount()    { return datacenterCount; }
    public int  getHostCount()          { return hostCount; }
    public int  getHostPes()            { return hostPes; }
    public long getHostMips()           { return hostMips; }
    public long getHostRamMb()          { return hostRamMb; }
    public long getHostStorageMb()      { return hostStorageMb; }
    public long getHostBwMbps()         { return hostBwMbps; }
    public int  getVmCount()            { return vmCount; }
    public long getVmMips()             { return vmMips; }
    public int  getVmPes()              { return vmPes; }
    public long getVmRamMb()            { return vmRamMb; }
    public long getVmStorageMb()        { return vmStorageMb; }
    public long getVmBwMbps()           { return vmBwMbps; }
    public int  getCloudletCount()      { return cloudletCount; }
    public long getCloudletLength()     { return cloudletLength; }
    public long getCloudletFileSize()   { return cloudletFileSize; }
    public long getCloudletOutputSize() { return cloudletOutputSize; }
    public int  getCloudletPes()        { return cloudletPes; }

    @Override
    public String toString() {
        return String.format(
            "SimulationConfig { %d DC | %d hosts x %d PEs @ %d MIPS | %d VMs | %d cloudlets x %d MI }",
            datacenterCount, hostCount, hostPes, hostMips, vmCount, cloudletCount, cloudletLength);
    }
}