package de.ironicdev.amazebase;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClient;

@Configuration
public class MongoConfig {

    public @Bean
    MongoDbFactory mongoDbFactory() throws Exception {
        return new SimpleMongoDbFactory(new MongoClient(), "systemData");
    }

    MongoDbFactory mongoDbFactory(String project) throws Exception {
        return new SimpleMongoDbFactory(new MongoClient(), project);
    }

    public @Bean
    MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());

        return mongoTemplate;
    }

    MongoTemplate mongoTemplate(String project) throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory(project));

        return mongoTemplate;

    }
}