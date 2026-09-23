package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.util.UUID;

@Entity
@Immutable
@Subselect("""
        SELECT e.id AS id
        FROM evaluacion e
        """)
@Synchronize("evaluacion")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionAccesoJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;
}
