package com.saba.loadbalancerlibrary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceInstanceDto {

    private String id;
    private String host;
    private int port;

    @Builder.Default
    private int weight = 1;

    public String getUrl() {
        return "http://" + host + ":" + port;
    }
}