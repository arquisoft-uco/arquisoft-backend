package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.query.secondaryadapter.repository;

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
        SELECT eca.id                   AS id,
               eca.evaluacion_asesor_id AS evaluacion_asesor_id,
               ea.asesor_id             AS asesor_id
        FROM evaluacion_cuantitativa_asesor eca
        JOIN evaluacion_asesor ea ON ea.id = eca.evaluacion_asesor_id
        """)
@Synchronize({"evaluacion_cuantitativa_asesor", "evaluacion_asesor"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionCuantitativaAsesorAccesoJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "evaluacion_asesor_id")
    private UUID evaluacionAsesorId;

    @Column(name = "asesor_id")
    private UUID asesorId;
}
