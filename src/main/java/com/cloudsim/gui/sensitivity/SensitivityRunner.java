package com.cloudsim.gui.sensitivity;

import com.cloudsim.gui.model.CloudletResult;
import com.cloudsim.gui.model.SimulationConfig;
import com.cloudsim.gui.simulation.BasicSimulation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * SensitivityRunner
 *
 * Pure logic class — no JavaFX, no UI.
 * Executes the parameter sweep by calling BasicSimulation for each
 * generated value, collects SensitivityRunResult objects, and
 * saves the completed SensitivityAnalysis to SensitivityStore.
 *
 * Reuses BasicSimulation directly — no duplicate simulation logic.
 *
 * @param progressCallback called after each run with (runIndex, totalRuns)
 *                         so the UI can update a progress indicator.
 *                         Called on the simulation thread, not the FX thread.
 */
public class SensitivityRunner {

    private final SensitivityConfig            config;
    private final BiConsumer<Integer, Integer> progressCallback;

    public SensitivityRunner(SensitivityConfig config,
                              BiConsumer<Integer, Integer> progressCallback) {
        this.config           = config;
        this.progressCallback = progressCallback;
    }

    /**
     * Executes the full sweep synchronously (call from a background thread).
     * Returns the completed SensitivityAnalysis.
     */
    public SensitivityAnalysis run() {
        List<Long>                values     = config.generateValues();
        List<SensitivityRunResult> runResults = new ArrayList<>();
        int total = values.size();

        for (int i = 0; i < total; i++) {
            long value = values.get(i);

            // Build a config with only the selected parameter changed
            SimulationConfig runConfig = config.buildConfigFor(value);

            SensitivityRunResult result;
            try {
                List<CloudletResult> cloudletResults =
                    new BasicSimulation(runConfig).run();
                result = new SensitivityRunResult(value, runConfig, cloudletResults);
            } catch (Exception ex) {
                String msg = ex.getMessage();
                if (msg == null || msg.isBlank())
                    msg = "Simulation engine error for value " + value;
                result = new SensitivityRunResult(value, runConfig, msg);
            }

            runResults.add(result);

            // Notify UI of progress (1-based index)
            if (progressCallback != null) {
                progressCallback.accept(i + 1, total);
            }
        }

        // Save and return
        return SensitivityStore.getInstance().save(config, runResults);
    }
}