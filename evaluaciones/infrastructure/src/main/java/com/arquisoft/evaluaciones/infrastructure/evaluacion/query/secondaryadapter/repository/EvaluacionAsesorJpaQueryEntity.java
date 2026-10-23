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
        SELECT e.id                   AS id,
               ea.asesor_id           AS asesor_id,
               en.id                  AS entregable_id,
               en.proyecto            AS entregable_proyecto,
               en.version_entregable  AS entregable_version,
               ee.id                  AS estado_id,
               ee.nombre              AS estado_nombre
        FROM evaluacion e
        JOIN evaluacion_asesor ea ON ea.evaluacion_id = e.id
        JOIN entregable en        ON en.id = e.entregable_id
        JOIN estado_evaluacion ee ON ee.id = e.estado_evaluacion_id
        """)
@Synchronize({"evaluacion", "evaluacion_asesor", "entregable", "estado_evaluacion"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionAsesorJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "asesor_id")
    private UUID asesorId;

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
}
