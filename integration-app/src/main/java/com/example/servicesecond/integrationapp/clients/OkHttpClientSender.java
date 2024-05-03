package com.example.servicesecond.integrationapp.clients;

import com.example.servicesecond.integrationapp.model.EntityModel;
import com.example.servicesecond.integrationapp.model.UpsertEntityRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "app.client.okHttp")
public class OkHttpClientSender implements BaseClient {

    private final OkHttpClient httpClient;

    private final ObjectMapper objectMapper;

    @Value("${app.integration.base-url}")
    private String baseUrl;

    @Value("${app.integration.path-file}")
    private String pathFile;

    @Value("${app.integration.path-entity}")
    private String pathEntity;

    @SneakyThrows
    public String uploadFile(MultipartFile file) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                RequestBody.create(MediaType.parse("application/octet-stream"), file.getBytes()));

        Request request = new Request.Builder().url(baseUrl + pathFile + "/upload").header("Content-Type", "multipart/form-data").post(builder.build()).build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Error trying to request to upload file");

                return "Error";
            }
            return new String(response.body().bytes());
        }
    }

    @SneakyThrows
    public Resource downloadFile(String fileName) {
        Request request = new Request.Builder().url(baseUrl + pathFile +"/download/" + fileName).header("Accept", "application/octet-stream").get().build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Error trying to request to download file");
                return null;
            }
            return new ByteArrayResource(response.body().bytes());
        }
    }

    public List<EntityModel> getEntityList() {
        Request request = new Request.Builder().url(baseUrl + pathEntity).build();

        return processResponse(request, new TypeReference<>() {
        });
    }

    public EntityModel getEntityByName(String name) {
        Request request = new Request.Builder().url(baseUrl + pathEntity + "/" + name).build();
        return processResponse(request, new TypeReference<EntityModel>() {
        });
    }

    @SneakyThrows
    public EntityModel createEntity(UpsertEntityRequest request) {
        MediaType JSON = MediaType.get("application/json;charset=utf-8");
        String requestBody = objectMapper.writeValueAsString(request);
        RequestBody body = RequestBody.create(requestBody, JSON);

        Request req = new Request.Builder()
                .url(baseUrl + pathEntity)
                .post(body)
                .build();
        return processResponse(req, new TypeReference<EntityModel>() {
        });
    }

    @SneakyThrows
    public EntityModel updateEntity(UUID id, UpsertEntityRequest req) {
        MediaType JSON = MediaType.get("application/json;charset=utf-8");
        String requestBody = objectMapper.writeValueAsString(req);
        RequestBody body = RequestBody.create(requestBody, JSON);

        Request request = new Request.Builder()
                .url(baseUrl + pathEntity + "/" + id)
                .put(body)
                .build();

        return processResponse(request, new TypeReference<EntityModel>() {
        });
    }

    @SneakyThrows
    public void deleteEntityByID(UUID id) {
        Request request = new Request.Builder()
                .url(baseUrl + pathEntity + "/" + id)
                .delete()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Unexpected response code: " + response);
            }
        }
    }

    @SneakyThrows
    private <T> T processResponse(Request request, TypeReference<T> typeReference) {
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Unexpected response code: " + response);
            }
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                String stringBody = responseBody.string();
                return objectMapper.readValue(stringBody, typeReference);
            }
            throw new RuntimeException("Response body is empty");
        }
    }
}
