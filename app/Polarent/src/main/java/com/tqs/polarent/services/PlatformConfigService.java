package com.tqs.polarent.services;

import com.tqs.polarent.entity.PlatformConfig;
import com.tqs.polarent.repository.PlatformConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PlatformConfigService {

    private final PlatformConfigRepository configRepository;

    private static final String COMMISSION_FEE_KEY = "commission_fee_percentage";
    private static final String DEFAULT_COMMISSION = "10.0"; // 10%

    @Transactional
    public void setCommissionFee(Double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Commission fee must be between 0 and 100");
        }

        configRepository.findByConfigKey(COMMISSION_FEE_KEY)
                .ifPresentOrElse(
                        config -> {
                            config.setConfigValue(percentage.toString());
                            configRepository.save(config);
                        },
                        () -> {
                            PlatformConfig newConfig = PlatformConfig.builder()
                                    .configKey(COMMISSION_FEE_KEY)
                                    .configValue(percentage.toString())
                                    .description("Platform commission fee percentage")
                                    .build();
                            configRepository.save(newConfig);
                        }
                );
    }

    @Transactional(readOnly = true)
    public Double getCommissionFee() {
        return configRepository.findByConfigKey(COMMISSION_FEE_KEY)
                .map(config -> Double.parseDouble(config.getConfigValue()))
                .orElse(Double.parseDouble(DEFAULT_COMMISSION));
    }

    @Transactional(readOnly = true)
    public Double calculateCommission(Double amount) {
        Double percentage = getCommissionFee();
        return amount * (percentage / 100);
    }
}