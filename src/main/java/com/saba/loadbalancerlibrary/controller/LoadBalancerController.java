package com.saba.loadbalancerlibrary.controller;

import com.saba.loadbalancerlibrary.dto.ServiceInstanceDto;
import com.saba.loadbalancerlibrary.service.LeastConnectionsLoadBalancer;
import com.saba.loadbalancerlibrary.service.RandomLoadBalancer;
import com.saba.loadbalancerlibrary.service.RoundRobinLoadBalancer;
import com.saba.loadbalancerlibrary.service.WeightedRoundRobinLoadBalancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoadBalancerController {

    @Autowired
    private RoundRobinLoadBalancer roundRobinLoadBalancer;

    @Autowired
    private WeightedRoundRobinLoadBalancer weightedRoundRobinLoadBalancer;

    @Autowired
    private LeastConnectionsLoadBalancer leastConnectionsLoadBalancer;

    @Autowired
    private RandomLoadBalancer randomLoadBalancer;



    private final List<ServiceInstanceDto> instances = List.of(
            ServiceInstanceDto.builder().id("instance-1").host("localhost").port(8081).build(),
            ServiceInstanceDto.builder().id("instance-2").host("localhost").port(8082).build(),
            ServiceInstanceDto.builder().id("instance-3").host("localhost").port(8083).build(),
            ServiceInstanceDto.builder().id("instance-4").host("localhost").port(8084).build()
    );

    private final List<ServiceInstanceDto> weightedInstances = List.of(
            ServiceInstanceDto.builder().id("instance-A").host("localhost").port(8081).weight(5).build(),
            ServiceInstanceDto.builder().id("instance-B").host("localhost").port(8082).weight(1).build(),
            ServiceInstanceDto.builder().id("instance-C").host("localhost").port(8083).weight(1).build()
    );

    private final List<ServiceInstanceDto> leastConnInstances = List.of(
            ServiceInstanceDto.builder().id("instance-A").host("localhost").port(8081).build(),
            ServiceInstanceDto.builder().id("instance-B").host("localhost").port(8082).build(),
            ServiceInstanceDto.builder().id("instance-C").host("localhost").port(8083).build()
    );

    @GetMapping("/select-instance")
    public ServiceInstanceDto selectInstance() {
        return roundRobinLoadBalancer.selectInstance(instances);
    }

    @GetMapping("/select-weighted-instance")
    public ServiceInstanceDto selectWeightedInstance() {
        return weightedRoundRobinLoadBalancer.selectInstance(weightedInstances);
    }

    @GetMapping("/select-least-conn-instance")
    public ServiceInstanceDto selectLeastConnInstance() {
        return leastConnectionsLoadBalancer.selectInstance(leastConnInstances);
    }

    @GetMapping("/release-instance/{instanceId}")
    public String releaseInstance(@PathVariable String instanceId) {
        leastConnectionsLoadBalancer.releaseConnection(instanceId);
        return "Released: " + instanceId;
    }

    @GetMapping("/connection-count/{instanceId}")
    public String getConnectionCount(@PathVariable String instanceId) {
        return leastConnectionsLoadBalancer.getConnectionCount(instanceId, leastConnInstances);
    }

    @GetMapping("/select-random-instance")
    public ServiceInstanceDto selectRandomInstance() {
        return randomLoadBalancer.selectInstance(instances); // wahi purani 4-instance wali list use kar lo
    }
}