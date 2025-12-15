package com.tqs.polarent;

import com.tqs.polarent.entity.PlatformConfig;
import com.tqs.polarent.repository.PlatformConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
class PlatformConfigIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private PlatformConfigRepository configRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/admin/config";
        reset(configRepository);
    }

    @Test
    void whenGetCommissionFee_thenReturnCommission() {
        PlatformConfig config = PlatformConfig.builder()
                .configKey("commission_fee_percentage")
                .configValue("15.0")
                .build();

        when(configRepository.findByConfigKey("commission_fee_percentage"))
                .thenReturn(Optional.of(config));
        ResponseEntity<Double> response = restTemplate.getForEntity(
                baseUrl + "/commission", Double.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(15.0);
    }

    @Test
    void whenSetCommissionFee_thenReturnSuccess() {
        PlatformConfig existingConfig = PlatformConfig.builder()
                .id(1L)
                .configKey("commission_fee_percentage")
                .configValue("10.0")
                .build();

        when(configRepository.findByConfigKey("commission_fee_percentage"))
                .thenReturn(Optional.of(existingConfig));
        when(configRepository.save(any(PlatformConfig.class)))
                .thenReturn(existingConfig);
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/commission?percentage=20.0",
                HttpMethod.PUT,
                null,
                Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(configRepository).findByConfigKey("commission_fee_percentage");
        verify(configRepository).save(argThat(config ->
                config.getConfigValue().equals("20.0")
        ));
    }

    @Test
    void whenCalculateCommission_thenReturnCalculatedValue() {
        PlatformConfig config = PlatformConfig.builder()
                .configKey("commission_fee_percentage")
                .configValue("25.0")
                .build();

        when(configRepository.findByConfigKey("commission_fee_percentage"))
                .thenReturn(Optional.of(config));

        ResponseEntity<Double> response = restTemplate.getForEntity(
                baseUrl + "/commission/calculate?amount=400.0",
                Double.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(100.0); // 25% of 400
    }

    @Test
    void whenGetCommissionFeeNotExists_thenReturnDefault() {
        when(configRepository.findByConfigKey("commission_fee_percentage"))
                .thenReturn(Optional.empty());
        ResponseEntity<Double> response = restTemplate.getForEntity(
                baseUrl + "/commission", Double.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(10.0); // Default value
    }

    @Test
    void whenSetCommissionFeeWithInvalidPercentage_thenReturnBadRequest() {
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/commission?percentage=150.0",
                HttpMethod.PUT,
                null,
                Void.class);
        assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.OK);
    }
}