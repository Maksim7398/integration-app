package com.example.servicesecond.integrationapp.controller;

import com.example.servicesecond.integrationapp.AbstractTest;
import com.example.servicesecond.integrationapp.entity.DatabaseEntity;
import com.example.servicesecond.integrationapp.model.UpsertEntityRequest;
import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class EntityClientControllerTest extends AbstractTest {

    @Test
    void whenGetAllEntities_thenReturnEntityList() throws Exception {
        assertTrue(redisTemplate.keys("*").isEmpty());

        String actualResponse = mockMvc.perform(get("/api/v1/client/entity"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = objectMapper.writeValueAsString(entityService.findAll());

        assertFalse(redisTemplate.keys("*").isEmpty());
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    void whenEntityName_thenReturnEntity() throws Exception {
        assertTrue(redisTemplate.keys("*").isEmpty());

        String actualResponse = mockMvc.perform(get("/api/v1/client/entity/testName1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = objectMapper.writeValueAsString(entityService.findByName("testName1"));

        assertFalse(redisTemplate.keys("*").isEmpty());
        System.out.println(redisTemplate.keys("*"));
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    void whenCreateEntity_thenReturnEntity() throws Exception {
        assertTrue(redisTemplate.keys("*").isEmpty());
        assertEquals(3, entityRepository.count());

        UpsertEntityRequest request = new UpsertEntityRequest();
        request.setName("newEntity");

        String actualResponse = mockMvc.perform(post("/api/v1/client/entity")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expected = objectMapper.writeValueAsString(new DatabaseEntity(UUID.randomUUID(),
                "newEntity", Instant.now()));
        assertTrue(redisTemplate.keys("*").isEmpty());
        assertEquals(4, entityRepository.count());
        JsonAssert.assertJsonEquals(expected, actualResponse,JsonAssert.whenIgnoringPaths("id","date"));
    }


}
