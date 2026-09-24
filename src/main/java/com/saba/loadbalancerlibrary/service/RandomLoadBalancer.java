package com.saba.loadbalancerlibrary.service;

import com.saba.loadbalancerlibrary.dto.RequestContext;
import com.saba.loadbalancerlibrary.dto.ServiceInstanceDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class RandomLoadBalancer implements LoadBalancer {

    @Override
    public ServiceInstanceDto selectInstance(List<ServiceInstanceDto> instances, RequestContext context) {
        if (instances == null || instances.isEmpty()) {
            throw new IllegalArgumentException("Instance list is empty");
        }

        int randomIndex = ThreadLocalRandom.current().nextInt(instances.size());
        return instances.get(randomIndex);
    }
}