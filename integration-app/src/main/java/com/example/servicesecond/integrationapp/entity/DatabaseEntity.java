package com.example.servicesecond.integrationapp.entity;

import com.example.servicesecond.integrationapp.model.EntityModel;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "database_entity")
public class DatabaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private Instant date;

    public static DatabaseEntity from(EntityModel entityModel){
        return new DatabaseEntity(entityModel.getId(),entityModel.getName(),entityModel.getDate());
    }
}
