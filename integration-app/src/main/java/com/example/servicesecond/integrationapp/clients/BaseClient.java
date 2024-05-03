package com.example.servicesecond.integrationapp.clients;

import com.example.servicesecond.integrationapp.model.EntityModel;
import com.example.servicesecond.integrationapp.model.UpsertEntityRequest;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface BaseClient {

    String uploadFile(MultipartFile file);

    Resource downloadFile(String fileName);

    List<EntityModel> getEntityList();

    EntityModel getEntityByName(String name);

    EntityModel createEntity(UpsertEntityRequest request);


    EntityModel updateEntity(UUID id, UpsertEntityRequest req);


    void deleteEntityByID(UUID id);

}
