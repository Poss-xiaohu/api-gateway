package com.poss.config_center_api;

public interface ConfigCenter {

    void init(String serverAddr, String env);

    void subscribeRulesChange(RulesChangeListener listener);
}
