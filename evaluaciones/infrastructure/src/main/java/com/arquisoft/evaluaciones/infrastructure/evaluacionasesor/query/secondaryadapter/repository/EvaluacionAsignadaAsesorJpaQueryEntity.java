package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository;

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
        SELECT ea.id                 AS id,
               ea.asesor_id          AS asesor_id,
               e.id                  AS evaluacion_id,
               ee.id                 AS estado_id,
               ee.nombre             AS estado_nombre,
               ee.orden              AS estado_orden,
               en.id                 AS entregable_id,
               en.proyecto           AS entregable_proyecto,
               en.version_entregable AS entregable_version,
               EXISTS (SELECT 1 FROM evaluacion_cualitativa_asesor eca
                       WHERE eca.evaluacion_asesor_id = ea.id) AS tiene_evaluaciones_cualitativas,
               EXISTS (SELECT 1 FROM evaluacion_cuantitativa_asesor eqa
                       WHERE eqa.evaluacion_asesor_id = ea.id) AS tiene_evaluaciones_cuantitativas
        FROM evaluacion_asesor ea
        JOIN evaluacion e ON e.id = ea.evaluacion_id
        JOIN estado_evaluacion ee ON ee.id = e.estado_evaluacion_id
        JOIN entregable en ON en.id = e.entregable_id
        """)
@Synchronize({"evaluacion_asesor", "evaluacion", "estado_evaluacion", "entregable",
        "evaluacion_cualitativa_asesor", "evaluacion_cuantitativa_asesor"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluacionAsignadaAsesorJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "asesor_id")
    private UUID asesorId;

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

    @Column(name = "tiene_evaluaciones_cualitativas")
    private boolean tieneEvaluacionesCualitativas;

    @Column(name = "tiene_evaluaciones_cuantitativas")
    private boolean tieneEvaluacionesCuantitativas;
}
