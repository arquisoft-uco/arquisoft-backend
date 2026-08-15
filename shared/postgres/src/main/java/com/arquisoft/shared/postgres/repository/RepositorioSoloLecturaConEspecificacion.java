package com.arquisoft.shared.postgres.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface RepositorioSoloLecturaConEspecificacion<T, ID> extends RepositorioSoloLectura<T, ID> {

    Page<T> findAll(Specification<T> spec, Pageable pageable);

    long count(Specification<T> spec);
}
