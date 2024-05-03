package com.example.servicesecond.integrationapp.clients;

import com.example.servicesecond.integrationapp.model.EntityModel;
import com.example.servicesecond.integrationapp.model.UpsertEntityRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "app.client.webClient")
public class WebClientSender implements BaseClient {

    private final WebClient webClient;

    @Override
    @SneakyThrows
    public String uploadFile(MultipartFile file) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", file.getResource())
                .filename(file.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM);
        webClient.post()
                .uri("/api/v1/file/upload")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return file.getName();
    }

    @Override
    public Resource downloadFile(String fileName) {
        return webClient.get()
                .uri("/api/v1/file/download/" +  fileName)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve()
                .bodyToMono(Resource.class)
                .block();
    }

    @Override
    public List<EntityModel> getEntityList() {
        log.info("WEB client init");
//        return webClient.get()
//                .uri("/api/v1/entity")
//                .retrieve()
//                .toEntityList(EntityModel.class)
//                .block()
//                .getBody();
        return webClient.get()
                .uri("/api/v1/entity")
                .retrieve()
                .bodyToFlux(EntityModel.class)
                .collectList()
                .block();
    }

    @Override
    public EntityModel getEntityByName(String name) {
        return webClient.get()
                .uri("/api/v1/entity/{name}", name)
                .retrieve()
                .bodyToMono(EntityModel.class)
                .block();
    }

    @Override
    public EntityModel createEntity(UpsertEntityRequest request) {
        return webClient.post()
                .uri("/api/v1/entity")
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(EntityModel.class)
                .block();
    }

    @Override
    public EntityModel updateEntity(UUID id, UpsertEntityRequest req) {
        return webClient.put()
                .uri("/api/v1/entity/{id}", id)
                .body(BodyInserters.fromValue(req))
                .retrieve()
                .bodyToMono(EntityModel.class)
                .block();
    }

    @Override
    public void deleteEntityByID(UUID id) {
        webClient.delete()
                .uri("/api/v1/entity/{id}", id)
                .retrieve();
    }
}
