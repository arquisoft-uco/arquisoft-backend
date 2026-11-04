package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository;

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
        SELECT ea.id            AS id,
               ea.evaluacion_id AS evaluacion_id,
               a.id             AS asesor_id,
               a.nombre         AS asesor_nombre,
               a.email          AS asesor_email
        FROM evaluacion_asesor ea
        JOIN asesor a ON a.id = ea.asesor_id
        """)
@Synchronize({"evaluacion_asesor", "asesor"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionAsesorDeEvaluacionJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "evaluacion_id")
    private UUID evaluacionId;

    @Column(name = "asesor_id")
    private UUID asesorId;

    @Column(name = "asesor_nombre")
    private String asesorNombre;

    @Column(name = "asesor_email")
    private String asesorEmail;
}
