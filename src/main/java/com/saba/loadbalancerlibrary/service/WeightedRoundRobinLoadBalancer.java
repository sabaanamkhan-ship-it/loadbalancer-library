package com.saba.loadbalancerlibrary.service;

import com.saba.loadbalancerlibrary.dto.ServiceInstanceDto;

import java.util.List;

public class WeightedRoundRobinLoadBalancer implements LoadBalancer {

    @Override
    public ServiceInstanceDto selectInstance(List<ServiceInstanceDto> instances) {
        // TODO: Phase 3 mein implement karenge
        return null;
    }
}
