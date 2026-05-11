package com.delissa.repository;

import com.delissa.model.ConfigSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfigSettingRepository extends JpaRepository<ConfigSetting, Long> {
    Optional<ConfigSetting> findByConfigKey(String configKey);
}
