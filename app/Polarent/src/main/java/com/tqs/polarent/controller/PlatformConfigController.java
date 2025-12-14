package com.tqs.polarent.controller;

import com.tqs.polarent.services.PlatformConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/config")
@RequiredArgsConstructor
public class PlatformConfigController {

    private final PlatformConfigService configService;

    @GetMapping("/commission")
    public ResponseEntity<Double> getCommissionFee() {
        return ResponseEntity.ok(configService.getCommissionFee());
    }

    @PutMapping("/commission")
    public ResponseEntity<Void> setCommissionFee(@RequestParam Double percentage) {
        configService.setCommissionFee(percentage);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/commission/calculate")
    public ResponseEntity<Double> calculateCommission(@RequestParam Double amount) {
        return ResponseEntity.ok(configService.calculateCommission(amount));
    }
}