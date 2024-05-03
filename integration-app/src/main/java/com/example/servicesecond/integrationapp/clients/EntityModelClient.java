package com.example.servicesecond.integrationapp.clients;

import com.example.servicesecond.integrationapp.model.EntityModel;
import com.example.servicesecond.integrationapp.model.UpsertEntityRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface EntityModelClient {

    @PostMapping(value = "/api/v1/file/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Mono<String> uploadFile(MultipartFile file);

    @GetMapping(value = "/api/v1/file/download/{fileName}",
            consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    Mono<Resource> downloadFile(@PathVariable("fileName") String fileName);

    @GetMapping(value = "/api/v1/entity")
    Flux<EntityModel> getEntityList();

    @GetMapping(value = "/api/v1/entity/{name}")
    Mono<EntityModel> getEntityByName(@PathVariable String name);

    @PutMapping(value = "/api/v1/entity/{id}")
    Mono<EntityModel> updateEntity(@PathVariable UUID id, @RequestBody UpsertEntityRequest req);

    @PostMapping(value = "/api/v1/entity")
    Mono<EntityModel> createEntity(@RequestBody UpsertEntityRequest request);

    @DeleteMapping(value = "/api/v1/entity/{id}")
    Mono<Void> deleteEntityByID(@PathVariable UUID id);
}
