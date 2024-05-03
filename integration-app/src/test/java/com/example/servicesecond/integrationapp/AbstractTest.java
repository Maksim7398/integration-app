package com.example.servicesecond.integrationapp;

import com.example.servicesecond.integrationapp.model.EntityModel;
import com.example.servicesecond.integrationapp.model.UpsertEntityRequest;
import com.example.servicesecond.integrationapp.repositoty.DatabaseEntityRepository;
import com.example.servicesecond.integrationapp.service.DatabaseEntityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest
@Sql("classpath:db/init.sql")
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
public class AbstractTest {

    public static final UUID UPDATED_ID = UUID.fromString("b15a387d-9ea7-41dc-b65a-3792b42e3a85");

    public static final Instant ENTITY_DATE = Instant.now();

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected DatabaseEntityService entityService;

    @Autowired
    protected DatabaseEntityRepository entityRepository;

    @RegisterExtension
    protected static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @Container
    protected static PostgreSQLContainer<?> postgreSQLContainer;

    @Container
    protected static final RedisContainer redisContainer = new RedisContainer(
            DockerImageName.parse("redis:7.0.12")
    )
            .withExposedPorts(6379)
            .withReuse(true);

    static {
        DockerImageName postgres = DockerImageName.parse("postgres:14");

        postgreSQLContainer = (PostgreSQLContainer) new PostgreSQLContainer(postgres).withReuse(true);

        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    public static void registryProperties(DynamicPropertyRegistry registry) {
        String jdbcUrl = postgreSQLContainer.getJdbcUrl();
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.url", () -> jdbcUrl);

        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));

        registry.add("app.integration.base-url", wireMockServer::baseUrl);
    }

    @BeforeEach
    public void before() throws JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
        stubClient();
    }

    @AfterEach
    public void after() {
        wireMockServer.resetAll();
    }

    public void stubClient() throws JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException {
        List<EntityModel> findAllResponseBody = new ArrayList<>();

        findAllResponseBody.add(new EntityModel(UUID.randomUUID(), "Entity1", Instant.now()));
        findAllResponseBody.add(new EntityModel(UUID.randomUUID(), "Entity2", Instant.now()));

        wireMockServer.stubFor(WireMock.get("/api/v1/entity")
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(findAllResponseBody))
                        .withStatus(200)));

        EntityModel findByNameResponseBody = new EntityModel(UUID.randomUUID(), "someEntity", Instant.now());

        wireMockServer.stubFor(WireMock.get("/api/vi/entity/" + findByNameResponseBody.getName())
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(findByNameResponseBody))
                        .withStatus(200)));

        UpsertEntityRequest createRequest = new UpsertEntityRequest();
        createRequest.setName("newEntity");
        EntityModel newEntity = new EntityModel(UUID.randomUUID(), "newEntity", Instant.now());

        wireMockServer.stubFor(WireMock.post("/api/v1/entity")
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(createRequest)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(newEntity))
                        .withStatus(201)));
    }


}
