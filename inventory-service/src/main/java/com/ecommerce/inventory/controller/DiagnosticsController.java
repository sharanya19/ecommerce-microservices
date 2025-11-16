package com.ecommerce.inventory.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.inventory.diagnostics.ConfigServerHealthClient;

@RestController
@RequestMapping("/diagnostics")
public class DiagnosticsController {

    private final ConfigServerHealthClient configServerHealthClient;

    public DiagnosticsController(ConfigServerHealthClient configServerHealthClient) {
        this.configServerHealthClient = configServerHealthClient;
    }

    @GetMapping("/config-server")
    public ResponseEntity<Map<String, Object>> configServerHealth() {
        return ResponseEntity.ok(configServerHealthClient.fetchConfigServerHealth());
    }
}


