package com.github.funnyzak.biz.service.config;

import com.github.funnyzak.bean.config.Config;
import com.github.funnyzak.biz.service.GeneralService;
import org.springframework.stereotype.Service;


@Service
public class ConfigService extends GeneralService<Config> {
    public Config fetchOrCreate(String name, String defValue) {
        Config config = fetch(name);
        if (config != null) {
            return config;
        }

        config.setValue(defValue);
        return save(config);
    }

    public Config save(String name, String val) {
        Config config = fetch(name);
        if (config != null) {
            config.setValue(val);
            update(config);
        } else {
            config = new Config();
            config.setName(name);
            config.setValue(val);
            config = save(config);
        }
        return config;
    }
}
