package com.theduckers.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.springframework.test.annotation.DirtiesContext;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;




//test/integration/AbstractIntegrationTest:


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractIntegrationTest {

        @Autowired
        protected MockMvc mockMvc;

        @Autowired
        protected ObjectMapper objectMapper;

        @Autowired
        protected MongoTemplate mongoTemplate;

        @Container
        @ServiceConnection
        static MySQLContainer<?> mysql =
                new MySQLContainer<>("mysql:8.0")
                        .withDatabaseName("the_duckers_test")
                        .withUsername("test")
                        .withPassword("test");

        @Container
        @ServiceConnection
        static MongoDBContainer mongo =
                new MongoDBContainer("mongo:7");

        @BeforeEach
        void cleanMongoCollections() {
                mongoTemplate.getDb().getCollection("products").deleteMany(new Document());
                mongoTemplate.getDb().getCollection("categories").deleteMany(new Document());
        }
}