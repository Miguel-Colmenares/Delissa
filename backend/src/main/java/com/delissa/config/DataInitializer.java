package com.delissa.config;

import com.delissa.model.ConfigSetting;
import com.delissa.repository.ConfigSettingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ConfigSettingRepository configSettingRepository;

    public DataInitializer(ConfigSettingRepository configSettingRepository) {
        this.configSettingRepository = configSettingRepository;
    }

    @Override
    public void run(String... args) {
        if (configSettingRepository.findByConfigKey("iva_rate").isEmpty()) {
            ConfigSetting setting = new ConfigSetting();
            setting.setConfigKey("iva_rate");
            setting.setConfigValue("0.19");
            configSettingRepository.save(setting);
        }
    }
}
