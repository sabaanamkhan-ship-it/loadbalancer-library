package com.saba.loadbalancerlibrary.service;

import com.saba.loadbalancerlibrary.dto.RequestContext;
import com.saba.loadbalancerlibrary.dto.ServiceInstanceDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IPHashLoadBalancer implements LoadBalancer {

    @Override
    public ServiceInstanceDto selectInstance(List<ServiceInstanceDto> instances, RequestContext context) {
        if (instances == null || instances.isEmpty()) {
            throw new IllegalArgumentException("Instance list is empty");
        }
        if (context == null || context.getClientIp() == null || context.getClientIp().isEmpty()) {
            throw new IllegalArgumentException("Client IP is required for IP Hash algorithm");
        }

        int hash = Math.abs(context.getClientIp().hashCode());
        int index = hash % instances.size();
        return instances.get(index);
    }
}