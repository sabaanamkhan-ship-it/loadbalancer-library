package com.saba.loadbalancerlibrary.service;

import com.saba.loadbalancerlibrary.dto.RequestContext;
import com.saba.loadbalancerlibrary.dto.ServiceInstanceDto;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WeightedRoundRobinLoadBalancer implements LoadBalancer {


    private final Map<String, Integer> currentWeights = new ConcurrentHashMap<>();

    @Override
    public synchronized ServiceInstanceDto selectInstance(List<ServiceInstanceDto> instances, RequestContext context) {
        if (instances == null || instances.isEmpty()) {
            throw new IllegalArgumentException("Instance list is empty");
        }

        int totalWeight = instances.stream()
                .mapToInt(ServiceInstanceDto::getWeight)
                .sum();

        ServiceInstanceDto selected = null;
        int maxCurrentWeight = Integer.MIN_VALUE;

        for (ServiceInstanceDto instance : instances) {
            int newCurrentWeight = currentWeights.merge(instance.getId(), instance.getWeight(), Integer::sum);

            if (newCurrentWeight > maxCurrentWeight) {
                maxCurrentWeight = newCurrentWeight;
                selected = instance;
            }
        }

        currentWeights.merge(selected.getId(), -totalWeight, Integer::sum);

        return selected;
    }
}
