package com.saba.loadbalancerlibrary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInstanceDto {

        private String id;
        private String host;
        private int port;
        private int weight;

        public String getUrl() {
            return "http://" + host + ":" + port;
        }
    }

