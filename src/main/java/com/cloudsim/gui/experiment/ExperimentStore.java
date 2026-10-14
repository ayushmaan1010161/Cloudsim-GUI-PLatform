package com.cloudsim.gui.experiment;

import com.cloudsim.gui.model.CloudletResult;
import com.cloudsim.gui.model.SimulationConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ExperimentStore
 *
 * In-memory store for all experiments in the current session.
 * Singleton — one instance per application run.
 *
 * Design note: The store is intentionally separated from the simulation
 * engine so that persistent storage (file, JSON, SQLite) can be added
 * later by replacing only this class, without touching anything else.
 */
public class ExperimentStore {

    private static final ExperimentStore INSTANCE = new ExperimentStore();

    private final List<Experiment>  experiments = new ArrayList<>();
    private final AtomicInteger     nextId      = new AtomicInteger(1);

    private ExperimentStore() {}

    public static ExperimentStore getInstance() { return INSTANCE; }

    // ── Write ─────────────────────────────────────────────────────────

    /**
     * Creates a new Experiment from the given config and results,
     * stores it, and returns it.
     */
    public Experiment save(SimulationConfig config, List<CloudletResult> results) {
        Experiment exp = new Experiment(nextId.getAndIncrement(), config, results);
        experiments.add(exp);
        return exp;
    }

    // ── Read ──────────────────────────────────────────────────────────

    /** Returns an unmodifiable view of all experiments, newest first. */
    public List<Experiment> getAll() {
        List<Experiment> copy = new ArrayList<>(experiments);
        Collections.reverse(copy);
        return Collections.unmodifiableList(copy);
    }

    /** Returns the experiment with the given ID, or null. */
    public Experiment getById(int id) {
        return experiments.stream()
            .filter(e -> e.getId() == id)
            .findFirst().orElse(null);
    }

    public int size() { return experiments.size(); }
    public boolean isEmpty() { return experiments.isEmpty(); }
}