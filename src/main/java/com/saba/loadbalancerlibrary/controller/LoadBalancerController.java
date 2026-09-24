package com.saba.loadbalancerlibrary.controller;

import com.saba.loadbalancerlibrary.dto.RequestContext;
import com.saba.loadbalancerlibrary.dto.ServiceInstanceDto;
import com.saba.loadbalancerlibrary.factory.LoadBalancerFactory;
import com.saba.loadbalancerlibrary.service.LoadBalancer;
import com.saba.loadbalancerlibrary.service.WeightedRoundRobinLoadBalancer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoadBalancerController {

    @Autowired
    private LoadBalancerFactory loadBalancerFactory;

    // Normal instances - equal weight, baaki algorithms ke liye
    private final List<ServiceInstanceDto> instances = List.of(
            ServiceInstanceDto.builder().id("instance-1").host("localhost").port(8081).build(),
            ServiceInstanceDto.builder().id("instance-2").host("localhost").port(8082).build(),
            ServiceInstanceDto.builder().id("instance-3").host("localhost").port(8083).build(),
            ServiceInstanceDto.builder().id("instance-4").host("localhost").port(8084).build()
    );

    // Weighted instances - alag-alag weight, Weighted Round Robin test karne ke liye
    private final List<ServiceInstanceDto> weightedInstances = List.of(
            ServiceInstanceDto.builder().id("instance-A").host("localhost").port(8081).weight(5).build(),
            ServiceInstanceDto.builder().id("instance-B").host("localhost").port(8082).weight(1).build(),
            ServiceInstanceDto.builder().id("instance-C").host("localhost").port(8083).weight(1).build()
    );

    @GetMapping("/select-instance")
    public ServiceInstanceDto selectInstance(HttpServletRequest request) {
        LoadBalancer loadBalancer = loadBalancerFactory.getLoadBalancer();

        String clientIp = request.getRemoteAddr();
        RequestContext context = RequestContext.builder().clientIp(clientIp).build();

        List<ServiceInstanceDto> instancesToUse =
                (loadBalancer instanceof WeightedRoundRobinLoadBalancer) ? weightedInstances : instances;

        return loadBalancer.selectInstance(instancesToUse, context);
    }
}