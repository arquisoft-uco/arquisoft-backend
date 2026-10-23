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
               epg.estudiante_id      AS estudiante_id,
               en.proyecto_grado_id   AS proyecto_grado_id,
               en.id                  AS entregable_id,
               en.proyecto            AS entregable_proyecto,
               en.version_entregable  AS entregable_version,
               ee.id                  AS estado_id,
               ee.nombre              AS estado_nombre
        FROM evaluacion e
        JOIN entregable en                 ON en.id = e.entregable_id
        JOIN estado_evaluacion ee          ON ee.id = e.estado_evaluacion_id
        JOIN estudiante_proyecto_grado epg ON epg.proyecto_grado_id = en.proyecto_grado_id
                                          AND epg.eliminado_en IS NULL
        """)
@Synchronize({"evaluacion", "entregable", "estado_evaluacion", "estudiante_proyecto_grado"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionEstudianteJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "estudiante_id")
    private UUID estudianteId;

    @Column(name = "proyecto_grado_id")
    private UUID proyectoGradoId;

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
