package com.saba.loadbalancerlibrary.factory;

import com.saba.loadbalancerlibrary.config.LoadBalancerProperties;
import com.saba.loadbalancerlibrary.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoadBalancerFactory {

    @Autowired
    private LoadBalancerProperties properties;

    @Autowired
    private RoundRobinLoadBalancer roundRobinLoadBalancer;

    @Autowired
    private WeightedRoundRobinLoadBalancer weightedRoundRobinLoadBalancer;

    @Autowired
    private LeastConnectionsLoadBalancer leastConnectionsLoadBalancer;

    @Autowired
    private RandomLoadBalancer randomLoadBalancer;

    @Autowired
    private IPHashLoadBalancer ipHashLoadBalancer;

    public LoadBalancer getLoadBalancer() {
        String algorithm = properties.getAlgorithm();

        return switch (algorithm) {
            case "round-robin" -> roundRobinLoadBalancer;
            case "weighted-round-robin" -> weightedRoundRobinLoadBalancer;
            case "least-connections" -> leastConnectionsLoadBalancer;
            case "random" -> randomLoadBalancer;
            case "ip-hash" -> ipHashLoadBalancer;
            default -> throw new IllegalArgumentException("Unknown load balancing algorithm: " + algorithm);
        };
    }
}