package com.example.servicesecond.integrationapp.controller;

import com.example.servicesecond.integrationapp.clients.OKVEDClient;
import com.example.servicesecond.integrationapp.model.QueryToOKVED;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/okved")
public class OKVEDController {

    private final OKVEDClient client;

    @PostMapping("/getCategory")
    public Object getDescription(@RequestBody QueryToOKVED query) {
        return client.getTypeForCode(query);
    }
}
