package com.tqs.polarent.services;

import com.tqs.polarent.entity.PlatformConfig;
import com.tqs.polarent.repository.PlatformConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlatformConfigServiceTest {

    @Mock
    private PlatformConfigRepository configRepository;

    @InjectMocks
    private PlatformConfigService configService;

    private PlatformConfig commissionConfig;

    @BeforeEach
    void setUp() {
        commissionConfig = PlatformConfig.builder()
                .id(1L)
                .configKey("commission_fee_percentage")
                .configValue("10.0")
                .description("Platform commission fee percentage")
                .build();
    }

    @Test
    void whenGetCommissionFeeAndConfigExists_thenReturnValue() {
        // Arrange
        when(configRepository.findByConfigKey("commission_fee_percentage"))
                .thenReturn(Optional.of(commissionConfig));

        // Act
        Double result = configService.getCommissionFee();

        // Assert
        assertThat(result).isEqualTo(10.0);
        verify(configRepository).findByConfigKey("commission_fee_percentage");
    }

    @Test
    void whenGetCommissionFeeAndConfigNotExists_thenReturnDefault() {
        // Arrange
        when(configRepository.findByConfigKey("commission_fee_percentage"))
                .thenReturn(Optional.empty());
        Double result = configService.getCommissionFee();

        assertThat(result).isEqualTo(10.0); // Default value
        verify(configRepository).findByConfigKey("commission_fee_percentage");
    }

    @Test
    void whenSetCommissionFeeWithValidPercentage_thenSaveConfig() {
        when(configRepository.findByConfigKey("commission_fee_percentage"))
                .thenReturn(Optional.of(commissionConfig));
        when(configRepository.save(any(PlatformConfig.class)))
                .thenReturn(commissionConfig);
        configService.setCommissionFee(15.5);
        verify(configRepository).findByConfigKey("commission_fee_percentage");
        verify(configRepository).save(argThat(config ->
                config.getConfigValue().equals("15.5") &&
                        config.getConfigKey().equals("commission_fee_percentage")
        ));
    }

    @Test
    void whenSetCommissionFeeWithNewConfig_thenCreateNewConfig() {
        when(configRepository.findByConfigKey("commission_fee_percentage"))
                .thenReturn(Optional.empty());
        when(configRepository.save(any(PlatformConfig.class)))
                .thenReturn(commissionConfig);
        configService.setCommissionFee(20.0);
        verify(configRepository).findByConfigKey("commission_fee_percentage");
        verify(configRepository).save(argThat(config ->
                config.getConfigValue().equals("20.0") &&
                        config.getConfigKey().equals("commission_fee_percentage")
        ));
    }

    @Test
    void whenSetCommissionFeeWithNegativePercentage_thenThrowException() {
        assertThatThrownBy(() -> configService.setCommissionFee(-5.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Commission fee must be between 0 and 100");

        verify(configRepository, never()).findByConfigKey(anyString());
        verify(configRepository, never()).save(any());
    }

    @Test
    void whenSetCommissionFeeWithPercentageOver100_thenThrowException() {
        assertThatThrownBy(() -> configService.setCommissionFee(150.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Commission fee must be between 0 and 100");
        verify(configRepository, never()).findByConfigKey(anyString());
        verify(configRepository, never()).save(any());
    }

    @Test
    void whenCalculateCommission_thenReturnCorrectAmount() {
        when(configRepository.findByConfigKey("commission_fee_percentage"))
                .thenReturn(Optional.of(commissionConfig));
        Double result = configService.calculateCommission(200.0);
        assertThat(result).isEqualTo(20.0); // 10% of 200
        verify(configRepository).findByConfigKey("commission_fee_percentage");
    }

    @Test
    void whenCalculateCommissionWithZeroAmount_thenReturnZero() {
        when(configRepository.findByConfigKey("commission_fee_percentage"))
                .thenReturn(Optional.of(commissionConfig));
        Double result = configService.calculateCommission(0.0);
        assertThat(result).isEqualTo(0.0);
    }

    @Test
    void whenCalculateCommissionWithLargeAmount_thenReturnCorrectAmount() {
        PlatformConfig highCommissionConfig = PlatformConfig.builder()
                .configKey("commission_fee_percentage")
                .configValue("25.0")
                .build();
        when(configRepository.findByConfigKey("commission_fee_percentage"))
                .thenReturn(Optional.of(highCommissionConfig));
        Double result = configService.calculateCommission(1000.0);
        assertThat(result).isEqualTo(250.0); // 25% of 1000
    }

    @Test
    void whenCalculateCommissionWithDecimalPercentage_thenReturnCorrectAmount() {
        PlatformConfig decimalCommissionConfig = PlatformConfig.builder()
                .configKey("commission_fee_percentage")
                .configValue("12.5")
                .build();
        when(configRepository.findByConfigKey("commission_fee_percentage"))
                .thenReturn(Optional.of(decimalCommissionConfig));
        Double result = configService.calculateCommission(80.0);
        assertThat(result).isEqualTo(10.0); // 12.5% of 80
    }

    @Test
    void whenSetCommissionFeeWithZeroPercentage_thenSaveSuccessfully() {
        when(configRepository.findByConfigKey("commission_fee_percentage"))
                .thenReturn(Optional.of(commissionConfig));
        when(configRepository.save(any(PlatformConfig.class)))
                .thenReturn(commissionConfig);
        configService.setCommissionFee(0.0);
        verify(configRepository).save(argThat(config ->
                config.getConfigValue().equals("0.0")
        ));
    }

    @Test
    void whenSetCommissionFeeWith100Percentage_thenSaveSuccessfully() {
        when(configRepository.findByConfigKey("commission_fee_percentage"))
                .thenReturn(Optional.of(commissionConfig));
        when(configRepository.save(any(PlatformConfig.class)))
                .thenReturn(commissionConfig);
        configService.setCommissionFee(100.0);
        verify(configRepository).save(argThat(config ->
                config.getConfigValue().equals("100.0")
        ));
    }

    @Test
    void whenGetCommissionFeeWithInvalidConfigValue_thenReturnDefault() {
        PlatformConfig invalidConfig = PlatformConfig.builder()
                .configKey("commission_fee_percentage")
                .configValue("invalid")
                .build();

        when(configRepository.findByConfigKey("commission_fee_percentage"))
                .thenReturn(Optional.of(invalidConfig));
        assertThatThrownBy(() -> configService.getCommissionFee())
                .isInstanceOf(NumberFormatException.class);
    }
}