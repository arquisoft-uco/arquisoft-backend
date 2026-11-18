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
        SELECT epg.id                AS id,
               ea.id                 AS evaluacion_asesor_id,
               epg.estudiante_id     AS estudiante_id
        FROM evaluacion_asesor ea
        JOIN evaluacion e                  ON e.id = ea.evaluacion_id
        JOIN entregable en                 ON en.id = e.entregable_id
        JOIN estudiante_proyecto_grado epg ON epg.proyecto_grado_id = en.proyecto_grado_id
                                          AND epg.eliminado_en IS NULL
        """)
@Synchronize({"evaluacion_asesor", "evaluacion", "entregable", "estudiante_proyecto_grado"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EstudianteEvaluacionAsesorAccesoJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "evaluacion_asesor_id")
    private UUID evaluacionAsesorId;

    @Column(name = "estudiante_id")
    private UUID estudianteId;
}
