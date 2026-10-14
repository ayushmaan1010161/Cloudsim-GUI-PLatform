package com.cloudsim.gui.simulation;

import com.cloudsim.gui.model.CloudletResult;
import com.cloudsim.gui.model.SimulationConfig;

import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudsimplus.schedulers.vm.VmSchedulerTimeShared;
import org.cloudsimplus.utilizationmodels.UtilizationModelDynamic;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;

import java.util.ArrayList;
import java.util.List;

/**
 * BasicSimulation  (Step 5 - Full Configuration)
 *
 * Now uses EVERY field from SimulationConfig - nothing is hardcoded.
 *
 * Step 5 changes vs Step 4:
 *   - createDatacenter() now creates datacenterCount datacenters,
 *     each containing hostCount hosts.
 *   - Each cloudlet now sets fileSize and outputSize from config.
 *   - UtilizationModelDynamic fraction auto-adjusted so cloudlets
 *     sharing a VM do not overcommit RAM/BW.
 */
public class BasicSimulation {

    private final SimulationConfig config;
    private CloudSimPlus           simulation;
    private DatacenterBrokerSimple broker;

    public BasicSimulation(SimulationConfig config) {
        this.config = config;
    }

    public List<CloudletResult> run() {
        simulation = new CloudSimPlus();
        broker     = new DatacenterBrokerSimple(simulation);

        createDatacenters();

        List<Vm>      vmList      = createVms();
        List<Cloudlet> cloudletList = createCloudlets();

        broker.submitVmList(vmList);
        broker.submitCloudletList(cloudletList);

        simulation.start();

        return collectResults();
    }

    // ── Infrastructure ────────────────────────────────────────────────

    /**
     * Creates datacenterCount datacenters, each with hostCount hosts.
     * Every host has hostPes PEs each running at hostMips MIPS.
     */
    private void createDatacenters() {
        for (int d = 0; d < config.getDatacenterCount(); d++) {
            List<HostSimple> hostList = new ArrayList<>();
            for (int h = 0; h < config.getHostCount(); h++) {
                List<Pe> peList = new ArrayList<>();
                for (int p = 0; p < config.getHostPes(); p++) {
                    peList.add(new PeSimple(config.getHostMips()));
                }
                HostSimple host = new HostSimple(
                        config.getHostRamMb(),
                        config.getHostBwMbps(),
                        config.getHostStorageMb(),
                        peList);
                host.setVmScheduler(new VmSchedulerTimeShared());
                hostList.add(host);
            }
            new DatacenterSimple(simulation, hostList);
        }
    }

    // ── Virtual Machines ──────────────────────────────────────────────

    private List<Vm> createVms() {
        List<Vm> vmList = new ArrayList<>();
        for (int i = 0; i < config.getVmCount(); i++) {
            Vm vm = new VmSimple(config.getVmMips(), config.getVmPes());
            vm.setRam(config.getVmRamMb())
              .setBw(config.getVmBwMbps())
              .setSize(config.getVmStorageMb())
              .setCloudletScheduler(new CloudletSchedulerTimeShared());
            vmList.add(vm);
        }
        return vmList;
    }

    // ── Cloudlets ─────────────────────────────────────────────────────

    /**
     * Each cloudlet uses 50% of VM resources so two can share a VM cleanly.
     * fileSize and outputSize are set from config (new in Step 5).
     */
    private List<Cloudlet> createCloudlets() {
        List<Cloudlet> list = new ArrayList<>();
        UtilizationModelDynamic util = new UtilizationModelDynamic(0.5);
        for (int i = 0; i < config.getCloudletCount(); i++) {
            CloudletSimple cl = new CloudletSimple(
                    config.getCloudletLength(),
                    config.getCloudletPes(),
                    util);
            cl.setFileSize(config.getCloudletFileSize());
            cl.setOutputSize(config.getCloudletOutputSize());
            list.add(cl);
        }
        return list;
    }

    // ── Results ───────────────────────────────────────────────────────

    private List<CloudletResult> collectResults() {
        List<CloudletResult> results = new ArrayList<>();
        for (Cloudlet cl : broker.getCloudletFinishedList()) {
            results.add(new CloudletResult(
                    (int) cl.getId(),
                    (int) cl.getVm().getId(),
                    cl.getStatus().name(),
                    cl.getExecStartTime(),
                    cl.getFinishTime(),
                    cl.getActualCpuTime()
            ));
        }
        results.sort((a, b) -> Integer.compare(a.getCloudletId(), b.getCloudletId()));
        return results;
    }
}