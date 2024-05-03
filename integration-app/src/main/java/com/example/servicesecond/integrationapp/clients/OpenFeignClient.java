package com.example.servicesecond.integrationapp.clients;

import com.example.servicesecond.integrationapp.model.EntityModel;
import com.example.servicesecond.integrationapp.model.UpsertEntityRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "openFeignClient", url = "${app.integration.base-url}")
@ConditionalOnProperty(value = "app.client.feignClient")
public interface OpenFeignClient extends BaseClient{

    @PostMapping(value = "/api/v1/file/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    String uploadFile(MultipartFile file);

    @GetMapping(value = "/api/v1/file/download/{fileName}",
            consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Override
    Resource downloadFile(@PathVariable("fileName") String fileName);

    @GetMapping(value = "/api/v1/entity")
    @Override
    List<EntityModel> getEntityList();

    @GetMapping(value = "/api/v1/entity/{name}")
    @Override
    EntityModel getEntityByName(@PathVariable String name);

    @PutMapping(value = "/api/v1/entity/{id}")
    @Override
    EntityModel updateEntity(@PathVariable UUID id, @RequestBody UpsertEntityRequest req);

    @PostMapping(value = "/api/v1/entity")
    @Override
    EntityModel createEntity(@RequestBody UpsertEntityRequest request);

    @DeleteMapping(value = "/api/v1/entity/{id}")
    @Override
    void deleteEntityByID(@PathVariable UUID id);
}
