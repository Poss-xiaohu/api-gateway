package com.poss.core.filter.loadbalance;

import com.alibaba.fastjson.JSON;
import com.poss.common.config.ServiceInstance;
import com.poss.common.exception.NotFoundException;
import com.poss.common.config.Rule;
import com.poss.core.context.GatewayContext;
import com.poss.core.filter.Filter;
import com.poss.core.filter.FilterAspect;
import com.poss.core.request.GatewayRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.poss.common.constants.FilterConst.*;
import static com.poss.common.enums.ResponseCode.SERVICE_INSTANCE_NOT_FOUND;

/**
 * @PROJECT_NAME: api-gateway
 * @DESCRIPTION: 负载均衡过滤器
 * @USER: WuYang
 * @DATE: 2023/3/12 22:02
 */
@Slf4j
@FilterAspect(id=LOAD_BALANCE_FILTER_ID,
              name = LOAD_BALANCE_FILTER_NAME,
              order = LOAD_BALANCE_FILTER_ORDER)
public class LoadBalanceFilter implements Filter {

    @Override
    public void doFilter(GatewayContext ctx){
        String serviceId = ctx.getUniqueId();
        IGatewayLoadBalanceRule gatewayLoadBalanceRule = getLoadBalanceRule(ctx);
        ServiceInstance serviceInstance = gatewayLoadBalanceRule.choose(serviceId);
        System.out.println("IP为"+serviceInstance.getIp()+",端口号："+serviceInstance.getPort());
        GatewayRequest request = ctx.getRequest();
        if(serviceInstance != null && request != null){
            String host  = serviceInstance.getIp()+":"+serviceInstance.getPort();
            request.setModifyHost(host);
        }else{
            log.warn("No instance available for :{}",serviceId);
            throw new NotFoundException(SERVICE_INSTANCE_NOT_FOUND);
        }
    }


    /**
     * 根据配置获取负载均衡器
     *
     * @param ctx
     * @return
     */
    public IGatewayLoadBalanceRule getLoadBalanceRule(GatewayContext ctx) {
        IGatewayLoadBalanceRule loadBalanceRule = null;
        Rule configRule = ctx.getRule();
        if (configRule != null) {
            Set<Rule.FilterConfig> filterConfigs = configRule.getFilterConfigs();
            Iterator iterator = filterConfigs.iterator();
            Rule.FilterConfig filterConfig;
            while (iterator.hasNext()) {
                filterConfig = (Rule.FilterConfig) iterator.next();
                if (filterConfig == null) {
                    continue;
                }
                String filterId = filterConfig.getId();
                if (filterId.equals(LOAD_BALANCE_FILTER_ID)) {
                    String config = filterConfig.getConfig();
                    String strategy = LOAD_BALANCE_STRATEGY_RANDOM;
                    if (StringUtils.isNotEmpty(config)) {
                        Map<String, String> mapTypeMap = JSON.parseObject(config, Map.class);
                        strategy = mapTypeMap.getOrDefault(LOAD_BALANCE_KEY, strategy);
                    }
                    switch (strategy) {
                        case LOAD_BALANCE_STRATEGY_RANDOM:
                            loadBalanceRule = RandomLoadBalanceRule.getInstance(configRule.getServiceId());
                            break;
                        case LOAD_BALANCE_STRATEGY_ROUND_ROBIN:
                            loadBalanceRule = RoundRobinLoadBalanceRule.getInstance(configRule.getServiceId());
                            break;
                        default:
                            log.warn("No loadBalance strategy for service:{}", strategy);
                            loadBalanceRule = RandomLoadBalanceRule.getInstance(configRule.getServiceId());
                            break;
                    }
                }
            }
        }
        return loadBalanceRule;
    }
}
