package com.example.servicesecond.integrationapp.repositoty;

import com.example.servicesecond.integrationapp.entity.DatabaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DatabaseEntityRepository extends JpaRepository<DatabaseEntity, UUID> {
}
