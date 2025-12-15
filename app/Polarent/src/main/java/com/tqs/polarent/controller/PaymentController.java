package com.tqs.polarent.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.Environment;
import com.paypal.sdk.authentication.ClientCredentialsAuthModel;
import com.paypal.sdk.controllers.OrdersController;
import com.paypal.sdk.models.OrderRequest;
import com.paypal.sdk.models.PurchaseUnitRequest;
import com.paypal.sdk.models.AmountWithBreakdown;
import com.paypal.sdk.models.Money;
import com.paypal.sdk.models.Order;
import com.paypal.sdk.models.CheckoutPaymentIntent;
import com.paypal.sdk.models.CreateOrderInput;
import com.paypal.sdk.models.CaptureOrderInput;
import com.paypal.sdk.http.response.ApiResponse;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    private PaypalServerSdkClient payPalSDK;

    private PaypalServerSdkClient getPayPalSDK() {
        if (payPalSDK == null) {
            ClientCredentialsAuthModel authModel = new ClientCredentialsAuthModel.Builder(clientId, clientSecret).build();
            payPalSDK = new PaypalServerSdkClient.Builder()
                    .environment(Environment.SANDBOX)
                    .clientCredentialsAuth(authModel)
                    .build();
        }
        return payPalSDK;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody Map<String, String> request) {
        try {
            String amount = request.get("amount");
            
            OrderRequest orderRequest = new OrderRequest.Builder(
                CheckoutPaymentIntent.CAPTURE,
                Arrays.asList(
                    new PurchaseUnitRequest.Builder(
                        new AmountWithBreakdown.Builder("EUR", amount).build()
                    ).build()
                )
            ).build();

            CreateOrderInput input = new CreateOrderInput.Builder("", orderRequest).build();
            OrdersController ordersController = getPayPalSDK().getOrdersController();
            ApiResponse<Order> response = ordersController.createOrder(input);
            Order order = response.getResult();
            
            return ResponseEntity.ok(Map.of("id", order.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/capture/{orderId}")
    public ResponseEntity<?> capturePayment(@PathVariable String orderId) {
        try {
            CaptureOrderInput input = new CaptureOrderInput.Builder("", orderId).build();
            OrdersController ordersController = getPayPalSDK().getOrdersController();
            ApiResponse<Order> response = ordersController.captureOrder(input);
            Order order = response.getResult();
            
            return ResponseEntity.ok(Map.of("status", "success", "order", order));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
