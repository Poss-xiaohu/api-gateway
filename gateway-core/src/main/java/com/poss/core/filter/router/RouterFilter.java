package com.poss.core.filter.router;

import com.poss.common.enums.ResponseCode;
import com.poss.common.exception.ConnectException;
import com.poss.common.exception.ResponseException;
import com.poss.core.ConfigLoader;
import com.poss.core.context.GatewayContext;
import com.poss.core.filter.Filter;
import com.poss.core.filter.FilterAspect;
import com.poss.core.helper.AsyncHttpHelper;
import com.poss.core.helper.ResponseHelper;
import com.poss.core.response.GatewayResponse;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import static com.poss.common.constants.FilterConst.*;

/**
 * @PROJECT_NAME: api-gateway
 * @DESCRIPTION:路由过滤器
 * @USER: 
 * @DATE: 
 */
@Slf4j
@FilterAspect(id=ROUTER_FILTER_ID,
              name = ROUTER_FILTER_NAME,
              order = ROUTER_FILTER_ORDER)
public class RouterFilter implements Filter {
    @Override
    public void doFilter(GatewayContext gatewayContext) throws Exception {
        Request request = gatewayContext.getRequest().build();
        CompletableFuture<Response> future = AsyncHttpHelper.getInstance().executeRequest(request);

        boolean whenComplete = ConfigLoader.getConfig().isWhenComplete();

        if (whenComplete) {
            future.whenComplete((response, throwable) -> {
                complete(request, response, throwable, gatewayContext);
            });
        } else {
            future.whenCompleteAsync((response, throwable) -> {
                complete(request, response, throwable, gatewayContext);
            });
        }
    }

    private void complete(Request request,
                          Response response,
                          Throwable throwable,
                          GatewayContext gatewayContext) {
        gatewayContext.releaseRequest();

        try {
            if (Objects.nonNull(throwable)) {
                String url = request.getUrl();
                if (throwable instanceof TimeoutException) {
                    log.warn("complete time out {}", url);
                    gatewayContext.setThrowable(new ResponseException(ResponseCode.REQUEST_TIMEOUT));
                    gatewayContext.setResponse(GatewayResponse.buildGatewayResponse(ResponseCode.REQUEST_TIMEOUT));
                } else {
                    gatewayContext.setThrowable(new ConnectException(throwable,
                            gatewayContext.getUniqueId(),
                            url, ResponseCode.HTTP_RESPONSE_ERROR));
                    gatewayContext.setResponse(GatewayResponse.buildGatewayResponse(ResponseCode.HTTP_RESPONSE_ERROR));
                }
            } else {
                gatewayContext.setResponse(GatewayResponse.buildGatewayResponse(response));
            }
        } catch (Throwable t) {
            gatewayContext.setThrowable(new ResponseException(ResponseCode.INTERNAL_ERROR));
            gatewayContext.setResponse(GatewayResponse.buildGatewayResponse(ResponseCode.INTERNAL_ERROR));
            log.error("complete error", t);
        } finally {
            gatewayContext.writtened();
            ResponseHelper.writeResponse(gatewayContext);
        }
    }
}
