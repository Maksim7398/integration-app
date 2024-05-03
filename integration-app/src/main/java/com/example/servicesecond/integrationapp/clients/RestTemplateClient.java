package com.example.servicesecond.integrationapp.clients;

import com.example.servicesecond.integrationapp.model.EntityModel;
import com.example.servicesecond.integrationapp.model.UpsertEntityRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "app.client.rest")
public class RestTemplateClient implements BaseClient {

    private final RestTemplate restTemplate;

    @Value("${app.integration.base-url}")
    private String baseUrl;


    @SneakyThrows
    public String uploadFile(MultipartFile file) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());

        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(body, headers);
        restTemplate.postForObject(baseUrl + "/api/v1/file/upload", httpEntity, String.class);

        return file.getName();
    }

    @Override
    public Resource downloadFile(String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
        ResponseEntity<Resource> response =
                restTemplate
                        .exchange(baseUrl + "/api/v1/file/download/{fileName}",
                                HttpMethod.GET,
                                new HttpEntity<>(headers),
                                Resource.class, fileName);

        return response.getBody();
    }

    @Override
    public List<EntityModel> getEntityList() {
        ResponseEntity<List<EntityModel>> response =
                restTemplate.exchange(
                        baseUrl + "/api/v1/entity",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        }
                );

        return response.getBody();
    }

    @Override
    public EntityModel getEntityByName(String name) {
        ResponseEntity<EntityModel> response =
                restTemplate.getForEntity(baseUrl + "/api/v1/entity/" + name, EntityModel.class);

        return response.getBody();
    }

    @Override
    public EntityModel createEntity(UpsertEntityRequest request) {
        ResponseEntity<EntityModel> response =
                restTemplate.postForEntity(baseUrl + "/api/v1/entity",
                        new HttpEntity<>(request), EntityModel.class);
        return response.getBody();
    }

    @Override
    public EntityModel updateEntity(UUID id, UpsertEntityRequest req) {
        ResponseEntity<EntityModel> response = restTemplate.exchange(baseUrl + "/api/v1/entity/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(req),
                EntityModel.class,
                id);
        return response.getBody();
    }

    @Override
    public void deleteEntityByID(UUID id) {
        restTemplate.delete(baseUrl + "/api/v1/entity/" + id);
        log.info("Entity deleted by id: {}", id);
    }

}
