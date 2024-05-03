package com.example.servicesecond.integrationapp.clients;

import com.example.servicesecond.integrationapp.model.QueryToOKVED;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;


@Component
@RequiredArgsConstructor
public class OKVEDClient {

    private final WebClient webClient;

    @Cacheable("okved-category")
    public Object getTypeForCode(QueryToOKVED query){
        return webClient
                .post()
                .header("Authorization", "Token 0ad41cccd3c7936e8dc7e074a4049a3661a68a3c")
                .body(BodyInserters.fromValue(query))
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

}
