package com.cloudsim.gui.sensitivity;

/**
 * SensitivityParameter
 *
 * Enum of all CloudSim Plus parameters that the sensitivity analysis
 * engine can vary. Each entry knows its display name, unit, and
 * whether its value type is int or long.
 *
 * Only parameters that exist in SimulationConfig and are actually
 * passed into BasicSimulation are listed here.
 */
public enum SensitivityParameter {

    VM_MIPS       ("VM MIPS",              "MIPS",    100,   1_000_000, false),
    VM_COUNT      ("Number of VMs",        "VMs",     1,     1000,      true),
    VM_RAM        ("VM RAM",               "MB",      128,   10_000_000,false),
    VM_PES        ("VM CPU Cores",         "cores",   1,     512,       true),
    CLOUDLET_COUNT("Number of Cloudlets",  "tasks",   1,     50_000,    true),
    CLOUDLET_LEN  ("Cloudlet Length",      "MI",      1,     Long.MAX_VALUE, false),
    HOST_MIPS     ("Host CPU MIPS/core",   "MIPS",    100,   1_000_000, false),
    HOST_COUNT    ("Number of Hosts",      "hosts",   1,     100,       true);

    private final String  displayName;
    private final String  unit;
    private final long    minAllowed;
    private final long    maxAllowed;
    private final boolean isIntType;   // true = int field, false = long field

    SensitivityParameter(String displayName, String unit,
                         long minAllowed, long maxAllowed, boolean isIntType) {
        this.displayName = displayName;
        this.unit        = unit;
        this.minAllowed  = minAllowed;
        this.maxAllowed  = maxAllowed;
        this.isIntType   = isIntType;
    }

    public String  getDisplayName() { return displayName; }
    public String  getUnit()        { return unit; }
    public long    getMinAllowed()  { return minAllowed; }
    public long    getMaxAllowed()  { return maxAllowed; }
    public boolean isIntType()      { return isIntType; }

    @Override public String toString() { return displayName; }
}