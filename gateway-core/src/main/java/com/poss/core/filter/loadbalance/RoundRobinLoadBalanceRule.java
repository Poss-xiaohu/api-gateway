package com.poss.core.filter.loadbalance;

import com.poss.common.config.DynamicConfigManager;
import com.poss.common.config.ServiceInstance;
import com.poss.common.exception.NotFoundException;
import com.poss.core.context.GatewayContext;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.poss.common.enums.ResponseCode.SERVICE_INSTANCE_NOT_FOUND;


/**
 * @PROJECT_NAME: api-gateway
 * @DESCRIPTION: 负载均衡-轮询算法
 * @USER: 
 * @DATE: 
 */
@Slf4j
public class RoundRobinLoadBalanceRule implements  IGatewayLoadBalanceRule{

    private AtomicInteger position = new AtomicInteger(1);

    private final  String serviceId;


    public RoundRobinLoadBalanceRule( String serviceId) {
        this.serviceId = serviceId;
    }

    private static ConcurrentHashMap<String,RoundRobinLoadBalanceRule> serviceMap = new ConcurrentHashMap<>();

    public static RoundRobinLoadBalanceRule getInstance(String serviceId){
        RoundRobinLoadBalanceRule loadBalanceRule = serviceMap.get(serviceId);
        if(loadBalanceRule == null){
            loadBalanceRule = new RoundRobinLoadBalanceRule(serviceId);
            serviceMap.put(serviceId,loadBalanceRule);
        }
        return loadBalanceRule;
    }

    @Override
    public ServiceInstance choose(GatewayContext ctx) {
        return choose(ctx.getUniqueId());
    }

    @Override
    public ServiceInstance choose(String serviceId) {
        Set<ServiceInstance> serviceInstanceSet =  DynamicConfigManager.getInstance().getServiceInstanceByUniqueId(serviceId);
        if(serviceInstanceSet.isEmpty()){
            log.warn("No instance available for:{}",serviceId);
            throw  new NotFoundException(SERVICE_INSTANCE_NOT_FOUND);
        }
        List<ServiceInstance> instances = new ArrayList<ServiceInstance>(serviceInstanceSet);
        if(instances.isEmpty()){
            log.warn("No instance available for service:{}",serviceId);
            return null;
        }else{
            int pos = Math.abs(this.position.incrementAndGet());
            return instances.get(pos%instances.size());
        }
    }
}
