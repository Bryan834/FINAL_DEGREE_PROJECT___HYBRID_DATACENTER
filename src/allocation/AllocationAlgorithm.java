package allocation;

import datacenter.DataCenter;
import demand.Demand;

public interface AllocationAlgorithm {
    String getName();
    AllocationResult execute(DataCenter datacenter, Demand demand);
}