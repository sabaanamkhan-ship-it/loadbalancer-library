package com.saba.loadbalancerlibrary.service;

import com.saba.loadbalancerlibrary.dto.RequestContext;
import com.saba.loadbalancerlibrary.dto.ServiceInstanceDto;

import java.util.List;

public interface LoadBalancer {
    ServiceInstanceDto selectInstance(List<ServiceInstanceDto> instances, RequestContext context);
}