package com.tqs.polarent.controller;

import com.tqs.polarent.services.PlatformConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlatformConfigControllerTest {

    @Mock
    private PlatformConfigService configService;

    @InjectMocks
    private PlatformConfigController configController;

    @BeforeEach
    void setUp() {
        // Reset mocks if needed
    }

    @Test
    void whenGetCommissionFee_thenReturnOkWithValue() {
        // Arrange
        when(configService.getCommissionFee()).thenReturn(15.0);

        // Act
        ResponseEntity<Double> response = configController.getCommissionFee();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(15.0);
        verify(configService).getCommissionFee();
    }

    @Test
    void whenSetCommissionFeeWithValidPercentage_thenReturnOk() {
        // Arrange
        doNothing().when(configService).setCommissionFee(20.0);

        // Act
        ResponseEntity<Void> response = configController.setCommissionFee(20.0);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(configService).setCommissionFee(20.0);
    }

    @Test
    void whenSetCommissionFeeWithDecimalPercentage_thenReturnOk() {
        // Arrange
        doNothing().when(configService).setCommissionFee(12.5);

        // Act
        ResponseEntity<Void> response = configController.setCommissionFee(12.5);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(configService).setCommissionFee(12.5);
    }

    @Test
    void whenSetCommissionFeeWithZeroPercentage_thenReturnOk() {
        // Arrange
        doNothing().when(configService).setCommissionFee(0.0);

        // Act
        ResponseEntity<Void> response = configController.setCommissionFee(0.0);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(configService).setCommissionFee(0.0);
    }

    @Test
    void whenSetCommissionFeeWith100Percentage_thenReturnOk() {
        // Arrange
        doNothing().when(configService).setCommissionFee(100.0);

        // Act
        ResponseEntity<Void> response = configController.setCommissionFee(100.0);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(configService).setCommissionFee(100.0);
    }

    @Test
    void whenCalculateCommission_thenReturnOkWithCalculatedValue() {
        // Arrange
        when(configService.calculateCommission(200.0)).thenReturn(20.0);

        // Act
        ResponseEntity<Double> response = configController.calculateCommission(200.0);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(20.0);
        verify(configService).calculateCommission(200.0);
    }

    @Test
    void whenCalculateCommissionWithZeroAmount_thenReturnZero() {
        // Arrange
        when(configService.calculateCommission(0.0)).thenReturn(0.0);

        // Act
        ResponseEntity<Double> response = configController.calculateCommission(0.0);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(0.0);
        verify(configService).calculateCommission(0.0);
    }

    @Test
    void whenCalculateCommissionWithLargeAmount_thenReturnCorrectValue() {
        // Arrange
        when(configService.calculateCommission(1000.0)).thenReturn(100.0);

        // Act
        ResponseEntity<Double> response = configController.calculateCommission(1000.0);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(100.0);
        verify(configService).calculateCommission(1000.0);
    }

    @Test
    void whenSetCommissionFeeThrowsException_thenControllerShouldPropagate() {
        // Arrange
        doThrow(new IllegalArgumentException("Invalid percentage"))
                .when(configService).setCommissionFee(-10.0);

        // Act & Assert
        try {
            configController.setCommissionFee(-10.0);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("Invalid percentage");
        }

        verify(configService).setCommissionFee(-10.0);
    }

    @Test
    void whenCalculateCommissionWithNegativeAmount_thenReturnNegativeValue() {
        // Arrange
        when(configService.calculateCommission(-100.0)).thenReturn(-10.0);

        // Act
        ResponseEntity<Double> response = configController.calculateCommission(-100.0);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(-10.0);
        verify(configService).calculateCommission(-100.0);
    }

    @Test
    void testControllerEndpointsCalledCorrectly() {
        // Test multiple calls to verify interactions
        when(configService.getCommissionFee()).thenReturn(10.0);
        when(configService.calculateCommission(50.0)).thenReturn(5.0);
        doNothing().when(configService).setCommissionFee(15.0);

        // Call all endpoints
        configController.getCommissionFee();
        configController.setCommissionFee(15.0);
        configController.calculateCommission(50.0);

        // Verify all were called
        verify(configService).getCommissionFee();
        verify(configService).setCommissionFee(15.0);
        verify(configService).calculateCommission(50.0);
    }
}