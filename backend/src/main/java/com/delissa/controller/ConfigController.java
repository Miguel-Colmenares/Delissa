package com.delissa.controller;

import com.delissa.model.ConfigSetting;
import com.delissa.repository.ConfigSettingRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/config")
public class ConfigController {

    private final ConfigSettingRepository configSettingRepository;

    public ConfigController(ConfigSettingRepository configSettingRepository) {
        this.configSettingRepository = configSettingRepository;
    }

    @GetMapping
    public Map<String, String> getAll() {
        return configSettingRepository.findAll().stream()
                .collect(Collectors.toMap(ConfigSetting::getConfigKey, ConfigSetting::getConfigValue));
    }

    @GetMapping("/{key}")
    public String getByKey(@PathVariable String key) {
        return configSettingRepository.findByConfigKey(key)
                .map(ConfigSetting::getConfigValue)
                .orElse(null);
    }

    @PutMapping("/{key}")
    public String update(@PathVariable String key, @RequestBody Map<String, String> body) {
        String value = body.get("value");
        ConfigSetting setting = configSettingRepository.findByConfigKey(key)
                .orElseGet(() -> {
                    ConfigSetting s = new ConfigSetting();
                    s.setConfigKey(key);
                    return s;
                });
        setting.setConfigValue(value);
        configSettingRepository.save(setting);
        return value;
    }
}
