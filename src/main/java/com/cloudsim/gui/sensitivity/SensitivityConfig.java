package com.cloudsim.gui.sensitivity;

import com.cloudsim.gui.model.SimulationConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * SensitivityConfig
 *
 * Defines one sensitivity analysis:
 *   - baseline : the SimulationConfig used for all runs (only one field changes)
 *   - parameter: which field to vary
 *   - start, end, step: the range to sweep
 *
 * generateValues() produces the ordered list of values to test.
 */
public class SensitivityConfig {

    private final SimulationConfig    baseline;
    private final SensitivityParameter parameter;
    private final long                start;
    private final long                end;
    private final long                step;

    public SensitivityConfig(SimulationConfig baseline,
                              SensitivityParameter parameter,
                              long start, long end, long step) {
        this.baseline  = baseline;
        this.parameter = parameter;
        this.start     = start;
        this.end       = end;
        this.step      = step;
    }

    /** Generates the ordered list of parameter values to test. */
    public List<Long> generateValues() {
        List<Long> values = new ArrayList<>();
        for (long v = start; v <= end; v += step) {
            values.add(v);
        }
        return values;
    }

    /**
     * Builds a SimulationConfig from the baseline with ONLY the
     * sensitivity parameter replaced by the given value.
     * Every other field stays exactly as the user configured it.
     */
    public SimulationConfig buildConfigFor(long value) {
        SimulationConfig b = baseline;
        return switch (parameter) {
            case VM_MIPS        -> new SimulationConfig(
                b.getDatacenterCount(), b.getHostCount(), b.getHostPes(), b.getHostMips(),
                b.getHostRamMb(), b.getHostStorageMb(), b.getHostBwMbps(),
                b.getVmCount(), value /*vmMips*/, b.getVmPes(),
                b.getVmRamMb(), b.getVmStorageMb(), b.getVmBwMbps(),
                b.getCloudletCount(), b.getCloudletLength(),
                b.getCloudletFileSize(), b.getCloudletOutputSize(), b.getCloudletPes());
            case VM_COUNT       -> new SimulationConfig(
                b.getDatacenterCount(), b.getHostCount(), b.getHostPes(), b.getHostMips(),
                b.getHostRamMb(), b.getHostStorageMb(), b.getHostBwMbps(),
                (int) value /*vmCount*/, b.getVmMips(), b.getVmPes(),
                b.getVmRamMb(), b.getVmStorageMb(), b.getVmBwMbps(),
                b.getCloudletCount(), b.getCloudletLength(),
                b.getCloudletFileSize(), b.getCloudletOutputSize(), b.getCloudletPes());
            case VM_RAM         -> new SimulationConfig(
                b.getDatacenterCount(), b.getHostCount(), b.getHostPes(), b.getHostMips(),
                b.getHostRamMb(), b.getHostStorageMb(), b.getHostBwMbps(),
                b.getVmCount(), b.getVmMips(), b.getVmPes(),
                value /*vmRam*/, b.getVmStorageMb(), b.getVmBwMbps(),
                b.getCloudletCount(), b.getCloudletLength(),
                b.getCloudletFileSize(), b.getCloudletOutputSize(), b.getCloudletPes());
            case VM_PES         -> new SimulationConfig(
                b.getDatacenterCount(), b.getHostCount(), b.getHostPes(), b.getHostMips(),
                b.getHostRamMb(), b.getHostStorageMb(), b.getHostBwMbps(),
                b.getVmCount(), b.getVmMips(), (int) value /*vmPes*/,
                b.getVmRamMb(), b.getVmStorageMb(), b.getVmBwMbps(),
                b.getCloudletCount(), b.getCloudletLength(),
                b.getCloudletFileSize(), b.getCloudletOutputSize(), b.getCloudletPes());
            case CLOUDLET_COUNT -> new SimulationConfig(
                b.getDatacenterCount(), b.getHostCount(), b.getHostPes(), b.getHostMips(),
                b.getHostRamMb(), b.getHostStorageMb(), b.getHostBwMbps(),
                b.getVmCount(), b.getVmMips(), b.getVmPes(),
                b.getVmRamMb(), b.getVmStorageMb(), b.getVmBwMbps(),
                (int) value /*cloudletCount*/, b.getCloudletLength(),
                b.getCloudletFileSize(), b.getCloudletOutputSize(), b.getCloudletPes());
            case CLOUDLET_LEN   -> new SimulationConfig(
                b.getDatacenterCount(), b.getHostCount(), b.getHostPes(), b.getHostMips(),
                b.getHostRamMb(), b.getHostStorageMb(), b.getHostBwMbps(),
                b.getVmCount(), b.getVmMips(), b.getVmPes(),
                b.getVmRamMb(), b.getVmStorageMb(), b.getVmBwMbps(),
                b.getCloudletCount(), value /*cloudletLength*/,
                b.getCloudletFileSize(), b.getCloudletOutputSize(), b.getCloudletPes());
            case HOST_MIPS      -> new SimulationConfig(
                b.getDatacenterCount(), b.getHostCount(), b.getHostPes(), value /*hostMips*/,
                b.getHostRamMb(), b.getHostStorageMb(), b.getHostBwMbps(),
                b.getVmCount(), b.getVmMips(), b.getVmPes(),
                b.getVmRamMb(), b.getVmStorageMb(), b.getVmBwMbps(),
                b.getCloudletCount(), b.getCloudletLength(),
                b.getCloudletFileSize(), b.getCloudletOutputSize(), b.getCloudletPes());
            case HOST_COUNT     -> new SimulationConfig(
                b.getDatacenterCount(), (int) value /*hostCount*/, b.getHostPes(), b.getHostMips(),
                b.getHostRamMb(), b.getHostStorageMb(), b.getHostBwMbps(),
                b.getVmCount(), b.getVmMips(), b.getVmPes(),
                b.getVmRamMb(), b.getVmStorageMb(), b.getVmBwMbps(),
                b.getCloudletCount(), b.getCloudletLength(),
                b.getCloudletFileSize(), b.getCloudletOutputSize(), b.getCloudletPes());
        };
    }

    public SimulationConfig    getBaseline()  { return baseline; }
    public SensitivityParameter getParameter() { return parameter; }
    public long                getStart()     { return start; }
    public long                getEnd()       { return end; }
    public long                getStep()      { return step; }
    public int                 getRunCount()  { return generateValues().size(); }
}