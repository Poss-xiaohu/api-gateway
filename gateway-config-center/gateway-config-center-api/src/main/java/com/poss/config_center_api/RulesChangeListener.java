package com.poss.config_center_api;

import com.poss.common.config.Rule;
import java.util.List;

public interface RulesChangeListener {
    void onRulesChange(List<Rule> rules);
}
