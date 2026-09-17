package com.saba.loadbalancerlibrary.service;

import com.saba.loadbalancerlibrary.dto.ServiceInstanceDto;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RoundRobinLoadBalancer implements LoadBalancer {

    private final AtomicInteger currentIndex = new AtomicInteger(-1);

    @Override
    public ServiceInstanceDto selectInstance(List<ServiceInstanceDto> instances) {
        if (instances == null || instances.isEmpty()) {
            throw new IllegalArgumentException("Instance list is empty");
        }

        int index = currentIndex.updateAndGet(i -> (i + 1) % instances.size());
        return instances.get(index);
    }
}
