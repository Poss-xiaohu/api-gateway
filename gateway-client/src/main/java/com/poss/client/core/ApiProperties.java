package com.poss.client.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
//@Component
@ConfigurationProperties(prefix = "api")
public class ApiProperties {

    private String registerAddress;

    private String env = "dev";
}
