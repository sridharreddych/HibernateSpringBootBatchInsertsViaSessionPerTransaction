package com.bookstore;

import com.bookstore.service.BookstoreService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MainApplication {

    private final BookstoreService bookstoreService;

    public MainApplication(BookstoreService bookstoreService) {
        this.bookstoreService = bookstoreService;
    }

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Bean
    public ApplicationRunner init() {
        return args -> {
            bookstoreService.batchAuthors();
        };
    }
}

/*
 *
 * Session-Level Batching (Hibernate 5.2 or Higher) in MySQL

Description: Batch inserts via Hibernate session-level batching (Hibernate 5.2 or higher) in MySQL. This example commits the database transaction after each batch excecution. This way we avoid long-running transactions and, in case of a failure, we rollback only the failed batch and don't lose the previous batches. For each batch, the Persistent Context is flushed and cleared, therefore we maintain a thin Persistent Context. This way the code is not prone to memory errors and performance penalties caused by slow flushes.

Key points:

in application.properties set spring.jpa.properties.hibernate.generate_statistics (just to check that batching is working)
in application.properties set JDBC URL with rewriteBatchedStatements=true (optimization for MySQL)
in application.properties set JDBC URL with cachePrepStmts=true (enable caching and is useful if you decide to set prepStmtCacheSize, prepStmtCacheSqlLimit, etc as well; without this setting the cache is disabled)
in application.properties set JDBC URL with useServerPrepStmts=true (this way you switch to server-side prepared statements (may lead to signnificant performance boost))
in case of using a parent-child relationship with cascade persist (e.g. one-to-many, many-to-many) then consider to set up spring.jpa.properties.hibernate.order_inserts=true to optimize the batching by ordering inserts
in entity, use the assigned generator since MySQL IDENTITY will cause insert batching to be disabled
the Hibernate Session is obtained by un-wrapping it via EntityManager#unwrap(Session.class)
the batching size is set via Session#setJdbcBatchSize(Integer size) and get via Session#getJdbcBatchSize()
in your DAO layer, commit the database transaction after each batch execution
if is not needed, then ensure that Second Level Cache is disabled via spring.jpa.properties.hibernate.cache.use_second_level_cache=false
 */
