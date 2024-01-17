package com.poss.core.context;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/***
 * @title BaseContext
 * @description
 * @author poss
 * @version 1.0.0
 * @create 2024/1/8 13:43
 **/
public class BaseContext implements IContext{
    
    //转发协议
    protected final String protocol;
    
    //状态
    protected volatile int status = IContext.Running;
    //Netty上下文
    protected final ChannelHandlerContext nettyCtx;
    
    //上下文参数
    protected final Map<String,Object> attributes = new HashMap<>();
    
    //请求过程中发生的异常
    protected Throwable throwable;
    //是否保持长连接
    protected final boolean keepAlive;
    
    //存放回调函数集合
    protected List<Consumer<IContext>> completedCallback;
    
    //定义是否已经释放资源
    protected final AtomicBoolean requestReleased = new AtomicBoolean(false); 
    
    public BaseContext(String protocol, ChannelHandlerContext nettyCtx, boolean keepAlive) {
        this.protocol = protocol;
        this.nettyCtx = nettyCtx;
        this.keepAlive = keepAlive;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T putAttribute(String key, T value) {
        return (T) attributes.put(key, value);
    }
    
    @Override
    public void runned() {
        status = IContext.Running;
    }

    @Override
    public void writtened() {
        status = IContext.Written;
    }

    @Override
    public void completed() {
        status = IContext.Completed;
    }

    @Override
    public void terminated() {
        status = IContext.Terminated;
    }

    @Override
    public boolean isRunning() {
        return status == IContext.Running;
    }

    @Override
    public boolean isWritten() {
        return status == IContext.Written;
    }

    @Override
    public boolean isCompleted() {
        return status == IContext.Completed;
    }

    @Override
    public boolean isTerminated() {
        return status == IContext.Terminated;
    }

    @Override
    public String getProtocol() {
        return this.protocol;
    }

    @Override
    public Object getRequest() {
        return null;
    }

    @Override
    public Object getResponse() {
        return null;
    }

    @Override
    public Throwable getThrowable() {
        return this.throwable;
    }

    @Override
    public void setResponse(Object response) {
        
    }

    @Override
    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public ChannelHandlerContext getNettyCtx() {
        return this.nettyCtx;
    }

    @Override
    public boolean isKeepAlive() {
        return this.keepAlive;
    }

    @Override
    public boolean releaseRequest() {
        return false;
    }

    @Override
    public void setCompletedCallBack(Consumer<IContext> consumer) {
        if(completedCallback == null){
            synchronized (this){
                if(completedCallback == null){
                    completedCallback = new ArrayList<>();
                }
            }
        }
        completedCallback.add(consumer);
    }

    @Override
    public void invokeCompletedCallBack() {
        if(completedCallback != null){
            completedCallback.forEach(c -> c.accept(this));
        }
    }
}
