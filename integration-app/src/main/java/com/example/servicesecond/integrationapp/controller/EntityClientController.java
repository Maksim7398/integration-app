package com.example.servicesecond.integrationapp.controller;

import com.example.servicesecond.integrationapp.clients.BaseClient;
import com.example.servicesecond.integrationapp.entity.DatabaseEntity;
import com.example.servicesecond.integrationapp.model.EntityModel;
import com.example.servicesecond.integrationapp.model.UpsertEntityRequest;
import com.example.servicesecond.integrationapp.repositoty.DatabaseEntityRepository;
import com.example.servicesecond.integrationapp.service.DatabaseEntityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/client/entity")
@RequiredArgsConstructor
@Slf4j
public class EntityClientController {
    private final DatabaseEntityRepository databaseEntityRepository;

    private final BaseClient clientSender;

    private final DatabaseEntityService service;

    @GetMapping
    public ResponseEntity<List<EntityModel>> entityList() {
        return ResponseEntity.ok().body(service.findAll().stream().map(EntityModel::from).toList());
    }

    @GetMapping("/{name}")
    public ResponseEntity<EntityModel> entityByName(@PathVariable String name) {
        return ResponseEntity.ok(EntityModel.from(service.findByName(name)));
    }

    @PostMapping
    public ResponseEntity<EntityModel> createEntity(@RequestBody UpsertEntityRequest request) {
        EntityModel entityModel = clientSender.createEntity(request);
        DatabaseEntity save = service.create(DatabaseEntity.from(entityModel));
        return ResponseEntity.status(HttpStatus.CREATED).body(EntityModel.from(save));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel> updateEntity(@PathVariable UUID id,
                                                    @RequestBody UpsertEntityRequest request) {
        EntityModel entityModel = clientSender.updateEntity(id, request);
        DatabaseEntity updateEntity = service.update(id, DatabaseEntity.from(entityModel));
        return ResponseEntity.ok(EntityModel.from(updateEntity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EntityModel> deleteEntityModel(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
