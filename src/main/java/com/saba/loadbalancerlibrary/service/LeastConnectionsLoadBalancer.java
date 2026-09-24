package com.saba.loadbalancerlibrary.service;

import com.saba.loadbalancerlibrary.dto.RequestContext;
import com.saba.loadbalancerlibrary.dto.ServiceInstanceDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@Service
public class LeastConnectionsLoadBalancer implements LoadBalancer {

    private final Map<String, AtomicInteger> activeConnections = new ConcurrentHashMap<>();

    @Override
    public ServiceInstanceDto selectInstance(List<ServiceInstanceDto> instances, RequestContext context) {
        if (instances == null || instances.isEmpty()) {
            throw new IllegalArgumentException("Instance list is empty");
        }

        ServiceInstanceDto selected = null;
        int minConnections = Integer.MAX_VALUE;

        for (ServiceInstanceDto instance : instances) {
            int currentConnections = activeConnections
                    .computeIfAbsent(instance.getId(), id -> new AtomicInteger(0))
                    .get();

            if (currentConnections < minConnections) {
                minConnections = currentConnections;
                selected = instance;
            }
        }

        activeConnections.get(selected.getId()).incrementAndGet();
        return selected;
    }

    public void releaseConnection(String instanceId) {
        AtomicInteger count = activeConnections.get(instanceId);
        if (count != null && count.get() > 0) {
            count.decrementAndGet();
        }
    }

    public <T> T executeWithLoadBalancing(List<ServiceInstanceDto> instances,
                                          Function<ServiceInstanceDto, T> requestLogic) {
        ServiceInstanceDto selected = selectInstance(instances, null);
        try {
            return requestLogic.apply(selected);
        } finally {
            releaseConnection(selected.getId());
        }
    }

    public String getConnectionCount(String instanceId, List<ServiceInstanceDto> validInstances) {
        boolean exists = validInstances.stream()
                .anyMatch(instance -> instance.getId().equals(instanceId));
        if (!exists) {
            return "Instance '" + instanceId + "' does not exist in the given list";
        }
        AtomicInteger count = activeConnections.get(instanceId);
        int currentCount = count != null ? count.get() : 0;
        return instanceId + " has " + currentCount + " active connections";
    }
}