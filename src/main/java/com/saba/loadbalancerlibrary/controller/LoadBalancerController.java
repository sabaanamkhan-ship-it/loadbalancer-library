package com.saba.loadbalancerlibrary.controller;

import com.saba.loadbalancerlibrary.dto.ServiceInstanceDto;
import com.saba.loadbalancerlibrary.service.LoadBalancer;
import com.saba.loadbalancerlibrary.service.RoundRobinLoadBalancer;
import com.saba.loadbalancerlibrary.service.WeightedRoundRobinLoadBalancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoadBalancerController {

    @Autowired
    private RoundRobinLoadBalancer roundRobinLoadBalancer;

    @Autowired
    private WeightedRoundRobinLoadBalancer weightedRoundRobinLoadBalancer;

    private final List<ServiceInstanceDto> instances = List.of(
            new ServiceInstanceDto("instance-1", "localhost", 8081, 1),
            new ServiceInstanceDto("instance-2", "localhost", 8082, 1),
            new ServiceInstanceDto("instance-3", "localhost", 8083, 1),
            new ServiceInstanceDto("instance-4", "localhost", 8084, 1)
    );

    private final List<ServiceInstanceDto> weightedInstances = List.of(
            new ServiceInstanceDto("instance-A", "localhost", 8081, 5),
            new ServiceInstanceDto("instance-B", "localhost", 8082, 1),
            new ServiceInstanceDto("instance-C", "localhost", 8083, 1)
    );

    @GetMapping("/select-instance")
    public ServiceInstanceDto selectInstance() {
        return roundRobinLoadBalancer.selectInstance(instances);
    }

    @GetMapping("/select-weighted-instance")
    public ServiceInstanceDto selectWeightedInstance() {
        return weightedRoundRobinLoadBalancer.selectInstance(weightedInstances);
    }
}
