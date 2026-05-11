package com.delissa.model;

import jakarta.persistence.*;

@Entity
@Table(name = "config_settings")
public class ConfigSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String configKey;

    private String configValue;

    public ConfigSetting() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getConfigKey() { return configKey; }
    public void setConfigKey(String configKey) { this.configKey = configKey; }

    public String getConfigValue() { return configValue; }
    public void setConfigValue(String configValue) { this.configValue = configValue; }
}
