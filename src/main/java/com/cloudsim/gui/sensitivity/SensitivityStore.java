package com.cloudsim.gui.sensitivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SensitivityStore
 *
 * In-memory singleton store for all sensitivity analyses in the session.
 * Follows the same pattern as ExperimentStore so persistent storage
 * can be added later by replacing only this class.
 */
public class SensitivityStore {

    private static final SensitivityStore INSTANCE = new SensitivityStore();

    private final List<SensitivityAnalysis> analyses = new ArrayList<>();
    private final AtomicInteger             nextId   = new AtomicInteger(1);

    private SensitivityStore() {}

    public static SensitivityStore getInstance() { return INSTANCE; }

    public SensitivityAnalysis save(SensitivityConfig config,
                                     List<SensitivityRunResult> results) {
        SensitivityAnalysis a =
            new SensitivityAnalysis(nextId.getAndIncrement(), config, results);
        analyses.add(a);
        return a;
    }

    /** Returns all analyses, newest first. */
    public List<SensitivityAnalysis> getAll() {
        List<SensitivityAnalysis> copy = new ArrayList<>(analyses);
        Collections.reverse(copy);
        return Collections.unmodifiableList(copy);
    }

    public boolean isEmpty() { return analyses.isEmpty(); }
    public int     size()    { return analyses.size(); }
}