package com.arquisoft.shared.jpa.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface QueryRepository<T, ID> extends Repository<T, ID> {

    Optional<T> findById(ID id);

    boolean existsById(ID id);

    List<T> findAll();

    long count();
}
