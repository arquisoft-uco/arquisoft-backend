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
        SELECT ea.id                  AS id,
               e.id                   AS evaluacion_id,
               en.id                  AS entregable_id,
               en.proyecto            AS entregable_proyecto,
               en.version_entregable  AS entregable_version,
               ee.id                  AS estado_id,
               ee.nombre              AS estado_nombre,
               a.id                   AS asesor_id,
               a.nombre               AS asesor_nombre,
               a.email                AS asesor_email
        FROM evaluacion_asesor ea
        JOIN asesor a             ON a.id = ea.asesor_id
        JOIN evaluacion e         ON e.id = ea.evaluacion_id
        JOIN entregable en        ON en.id = e.entregable_id
        JOIN estado_evaluacion ee ON ee.id = e.estado_evaluacion_id
        """)
@Synchronize({"evaluacion_asesor", "asesor", "evaluacion", "entregable", "estado_evaluacion"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionAsesorRealizadaJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "evaluacion_id")
    private UUID evaluacionId;

    @Column(name = "entregable_id")
    private UUID entregableId;

    @Column(name = "entregable_proyecto")
    private String entregableProyecto;

    @Column(name = "entregable_version")
    private Integer entregableVersion;

    @Column(name = "estado_id")
    private String estadoId;

    @Column(name = "estado_nombre")
    private String estadoNombre;

    @Column(name = "asesor_id")
    private UUID asesorId;

    @Column(name = "asesor_nombre")
    private String asesorNombre;

    @Column(name = "asesor_email")
    private String asesorEmail;
}
