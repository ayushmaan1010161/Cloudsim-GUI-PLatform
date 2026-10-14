package com.cloudsim.gui.controller;

import com.cloudsim.gui.model.CloudletResult;
import com.cloudsim.gui.model.SimulationConfig;
import com.cloudsim.gui.simulation.BasicSimulation;

import java.util.List;

/**
 * MainController  (Step 5 - Full Configuration)
 *
 * Step 5 change: accepts a fully-populated SimulationConfig directly
 * from the GUI. No values are hardcoded here anymore.
 *
 * The old runSimulation(int,int,int,long) method is replaced by
 * runSimulation(SimulationConfig) so every GUI field flows through.
 */
public class MainController {

    /** Config used in the most recent run. Null before first run. */
    private SimulationConfig lastConfig;

    /**
     * Runs a CloudSim Plus simulation using the provided configuration.
     * Every parameter in config comes directly from the GUI.
     *
     * @param config fully-populated SimulationConfig from the GUI
     * @return list of CloudletResult, one per finished cloudlet
     */
    public List<CloudletResult> runSimulation(SimulationConfig config) {
        this.lastConfig = config;
        return new BasicSimulation(config).run();
    }

    /**
     * Returns the SimulationConfig used in the most recent run.
     * Null if no simulation has been run yet.
     */
    public SimulationConfig getLastConfig() {
        return lastConfig;
    }
}