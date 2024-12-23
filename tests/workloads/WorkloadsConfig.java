package tests.workloads;

import java.util.*;

public class WorkloadsConfig {
    public static List<Workload> putWorkloads() {
        return Arrays.asList(new WorkloadPut(1.0));
    }

    public static List<Workload> getWorkloads() {
        return Arrays.asList(new WorkloadGet(1.0));
    }

    public static List<Workload> multiPutWorkloads() {
        return Arrays.asList(new WorkloadMultiPut(1.0));
    }

    public static List<Workload> multiGetWorkloads() {
        return Arrays.asList(new WorkloadMultiGet(1.0));
    }

    public static List<Workload> getWhenWorkloads() {
        return Arrays.asList(new WorkloadGetWhen());
    }

    public static List<Workload> mixedPutGetWorkloads() {
        List<Workload> workloads = new ArrayList<>();
        workloads.add(new WorkloadMixedPutGet(0.70)); // 70% get, 30% put
        workloads.add(new WorkloadMixedPutGet(0.50)); // 50% get, 50% put
        workloads.add(new WorkloadMixedPutGet(0.30)); // 30% get, 70% put
        return workloads;
    }

    public static List<Workload> mixedMultiPutMultiGetWorkloads() {
        List<Workload> workloads = new ArrayList<>();
        workloads.add(new WorkloadMixedMultiPutMultiGet(0.70)); // 70% multi-get, 30% multi-put
        workloads.add(new WorkloadMixedMultiPutMultiGet(0.50)); // 50% multi-get, 50% multi-put
        workloads.add(new WorkloadMixedMultiPutMultiGet(0.30)); // 30% multi-get, 70% multi-put
        return workloads;
    }

    public static List<Workload> allWorkloads() {
        List<Workload> workloads = new ArrayList<>();
        workloads.addAll(putWorkloads());
        workloads.addAll(getWorkloads());
        workloads.addAll(multiPutWorkloads());
        workloads.addAll(multiGetWorkloads());
        workloads.addAll(getWhenWorkloads());
        workloads.addAll(mixedPutGetWorkloads());
        workloads.addAll(mixedMultiPutMultiGetWorkloads());
        return workloads;
    }
}
