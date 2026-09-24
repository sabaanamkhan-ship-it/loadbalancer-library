package com.saba.loadbalancerlibrary.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestContext {
    private String clientIp; // sirf IP Hash use karega, baaki ignore karenge
}