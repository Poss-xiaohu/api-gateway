package com.poss.register_center_api;

import com.poss.common.config.ServiceDefinition;
import com.poss.common.config.ServiceInstance;

import java.util.Set;

public interface RegisterCenterListener {

    void onChange(ServiceDefinition serviceDefinition,
                  Set<ServiceInstance> serviceInstanceSet);
}
