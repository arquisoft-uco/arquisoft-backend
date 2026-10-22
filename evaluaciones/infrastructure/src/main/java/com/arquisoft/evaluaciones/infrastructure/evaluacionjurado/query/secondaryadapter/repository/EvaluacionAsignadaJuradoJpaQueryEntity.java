package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.util.UUID;

@Entity
@Immutable
@Subselect("""
        SELECT ej.id                 AS id,
               ej.jurado_id          AS jurado_id,
               e.id                  AS evaluacion_id,
               ee.id                 AS estado_id,
               ee.nombre             AS estado_nombre,
               ee.orden              AS estado_orden,
               en.id                 AS entregable_id,
               en.proyecto           AS entregable_proyecto,
               en.version_entregable AS entregable_version
        FROM evaluacion_jurado ej
        JOIN evaluacion e ON e.id = ej.evaluacion_id
        JOIN estado_evaluacion ee ON ee.id = e.estado_evaluacion_id
        JOIN entregable en ON en.id = e.entregable_id
        """)
@Synchronize({"evaluacion_jurado", "evaluacion", "estado_evaluacion", "entregable"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluacionAsignadaJuradoJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "jurado_id")
    private UUID juradoId;

    @Column(name = "evaluacion_id")
    private UUID evaluacionId;

    @Column(name = "estado_id")
    private String estadoId;

    @Column(name = "estado_nombre")
    private String estadoNombre;

    @Column(name = "estado_orden")
    private Integer estadoOrden;

    @Column(name = "entregable_id")
    private UUID entregableId;

    @Column(name = "entregable_proyecto")
    private String entregableProyecto;

    @Column(name = "entregable_version")
    private Integer entregableVersion;
}
