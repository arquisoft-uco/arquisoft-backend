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
        SELECT epg.id                AS id,
               eca.id                AS evaluacion_cuantitativa_asesor_id,
               epg.estudiante_id     AS estudiante_id
        FROM evaluacion_cuantitativa_asesor eca
        JOIN evaluacion_asesor ea          ON ea.id = eca.evaluacion_asesor_id
        JOIN evaluacion e                  ON e.id = ea.evaluacion_id
        JOIN entregable en                 ON en.id = e.entregable_id
        JOIN estudiante_proyecto_grado epg ON epg.proyecto_grado_id = en.proyecto_grado_id
                                          AND epg.eliminado_en IS NULL
        """)
@Synchronize({"evaluacion_cuantitativa_asesor", "evaluacion_asesor", "evaluacion", "entregable",
        "estudiante_proyecto_grado"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EstudianteEvaluacionCuantitativaAsesorAccesoJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "evaluacion_cuantitativa_asesor_id")
    private UUID evaluacionCuantitativaAsesorId;

    @Column(name = "estudiante_id")
    private UUID estudianteId;
}
