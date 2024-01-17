package com.poss.core.context;

import io.netty.channel.ChannelHandlerContext;

import java.nio.channels.Channel;
import java.util.function.Consumer;

/***
 * @title IContext
 * @description
 * @author poss
 * @version 1.0.0
 * @create 2024/1/5 23:24
 **/
public interface IContext {
    
    //上下文生命周期，运行中状态
    int Running = 1;
    
    //运行过程中发生错误，对其进行标记，告诉我们请求已经结束，需要返回客户端。
    int Written = 0;
    
    //标记写回成功，防止并发情况下的多次写回。
    int Completed = 1;
    
    //表示网关请求结束
    int Terminated = 2;
    
    /**
     * @Description 设置上下文状态为运行中
     * @param 
     * @return void
     * @Date 23:33 2024/1/5
     **/
    void runned();
    
    /**
     * @Description 设置上下文状态为标记写回
     * @param 
     * @return void
     * @Date 23:33 2024/1/5
     **/
    void writtened();
    
    /**
     * @Description 设置上下文状态为标记写回成功
     * @param 
     * @return void
     * @Date 23:33 2024/1/5
     **/
    void completed();
    
    /**
     * @Description 设置上下文状态为请求结束
     * @param 
     * @return void
     * @Date 23:33 2024/1/5
     **/
    void terminated();
    
    /**
     * @Description 判断网关状态
     * @param 
     * @return boolean
     * @Date 23:37 2024/1/5
     **/
    boolean isRunning();
    boolean isWritten();
    boolean isCompleted();
    boolean isTerminated();
    
    /**
     * @Description 获取协议
     * @param 
     * @return String
     * @Date 23:38 2024/1/5
     **/
    String getProtocol();
    
    /**
     * @Description 获取请求对象
     * @param 
     * @return Object
     * @Date 23:38 2024/1/5
     **/
    Object getRequest();
    
    /**
     * @Description 获取响应对象
     * @param 
     * @return Object
     * @Date 23:38 2024/1/5
     **/
    Object getResponse();
    
    /**
     * @Description 获取异常对象
     * @param 
     * @return Throwable
     * @Date 23:38 2024/1/5
     **/
    Throwable getThrowable();
    
    void setResponse(Object response);
    
    void setThrowable(Throwable throwable);

    /**
     * 获取上下文参数
     * @param key
     * @return
     * @param <T>
     */
    <T> T getAttribute(String key);

    /**
     *
     * @param key
     * @param value
     * @return
     * @param <T>
     */
    <T> T putAttribute(String key, T value);
    
    ChannelHandlerContext getNettyCtx();
    
    boolean isKeepAlive();
    
    //释放请求资源
    boolean releaseRequest();

    /**
     * 设置回调函数
     * @param consumer
     */
    void setCompletedCallBack(Consumer<IContext> consumer);

    /**
     * 设置回调函数
     * @param 
     */
    void invokeCompletedCallBack();
    
    
    
}
