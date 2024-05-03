package com.example.servicesecond.integrationapp.model;

import com.example.servicesecond.integrationapp.entity.DatabaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityModel {

    private UUID id;

    private String name;

    private Instant date;

    public static EntityModel from(DatabaseEntity entityModel){
        return new EntityModel(entityModel.getId(),entityModel.getName(),entityModel.getDate());
    }

}
