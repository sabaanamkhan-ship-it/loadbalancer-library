package com.saba.loadbalancerlibrary.controller;

import com.saba.loadbalancerlibrary.dto.ServiceInstanceDto;
import com.saba.loadbalancerlibrary.service.LoadBalancer;
import com.saba.loadbalancerlibrary.service.RoundRobinLoadBalancer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoadBalancerController {

    private final LoadBalancer loadBalancer = new RoundRobinLoadBalancer();

    private final List<ServiceInstanceDto> instances = List.of(
            new ServiceInstanceDto("instance-1", "localhost", 8081, 1),
            new ServiceInstanceDto("instance-2", "localhost", 8082, 1),
            new ServiceInstanceDto("instance-3", "localhost", 8083, 1),
            new ServiceInstanceDto("instance-4", "localhost", 8084, 1)
    );

    @GetMapping("/select-instance")
    public ServiceInstanceDto selectInstance() {
        return loadBalancer.selectInstance(instances);
    }
}
