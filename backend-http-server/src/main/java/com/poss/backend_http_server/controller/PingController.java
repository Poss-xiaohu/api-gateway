package com.poss.backend_http_server.controller;

import com.poss.client.core.ApiInvoker;
import com.poss.client.core.ApiProperties;
import com.poss.client.core.ApiProtocol;
import com.poss.client.core.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@ApiService(serviceId = "backend-http-server", protocol = ApiProtocol.HTTP, patternPath = "/http-server/**")
public class PingController {

    @Autowired
    private ApiProperties apiProperties;

    @ApiInvoker(path = "/http-server/ping")
    @GetMapping("/http-server/ping")
    public String ping() {
        log.info("{}", apiProperties);
        return "pong";
    }

}
