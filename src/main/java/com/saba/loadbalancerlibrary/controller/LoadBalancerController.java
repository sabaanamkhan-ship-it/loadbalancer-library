package com.saba.loadbalancerlibrary.controller;

import com.saba.loadbalancerlibrary.dto.RequestContext;
import com.saba.loadbalancerlibrary.dto.ServiceInstanceDto;
import com.saba.loadbalancerlibrary.factory.LoadBalancerFactory;
import com.saba.loadbalancerlibrary.service.LoadBalancer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LoadBalancerController {

    @Autowired
    private LoadBalancerFactory loadBalancerFactory;

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/select-instance")
    public ServiceInstanceDto selectInstance(HttpServletRequest request) {
        LoadBalancer loadBalancer = loadBalancerFactory.getLoadBalancer();

        List<ServiceInstance> eurekaInstances = discoveryClient.getInstances("DUMMY-SERVICE");

        List<ServiceInstanceDto> instances = eurekaInstances.stream()
                .map(instance -> ServiceInstanceDto.builder()
                        .id(instance.getInstanceId())
                        .host(instance.getHost())
                        .port(instance.getPort())
                        .build())
                .sorted(Comparator.comparing(ServiceInstanceDto::getId))
                .collect(Collectors.toList());

        String clientIp = request.getRemoteAddr();
        RequestContext context = RequestContext.builder().clientIp(clientIp).build();

        return loadBalancer.selectInstance(instances, context);
    }
}