package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.query.secondaryadapter.repository;

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
        SELECT ecj.id                   AS id,
               ecj.evaluacion_jurado_id  AS evaluacion_jurado_id,
               pea.estudiante_id         AS estudiante_id,
               i.id                      AS item_id,
               i.nombre                  AS item_nombre,
               i.descripcion             AS item_descripcion,
               c.id                      AS criterio_id,
               c.nombre                  AS criterio_nombre,
               c.descripcion             AS criterio_descripcion
        FROM evaluacion_cualitativa_jurado ecj
        JOIN evaluacion_jurado ej ON ej.id = ecj.evaluacion_jurado_id
        JOIN evaluacion e ON e.id = ej.evaluacion_id
        JOIN entregable_proyecto_acceso epa ON epa.entregable_id = e.entregable_id AND epa.activo = true
        JOIN proyecto_estudiante_acceso pea ON pea.proyecto_id = epa.proyecto_id AND pea.activo = true
        JOIN item_cualitativo_jurado i ON i.id = ecj.item_id
        JOIN criterio_item_cualitativo_jurado c ON c.id = ecj.criterio_id
        """)
@Synchronize({
        "evaluacion_cualitativa_jurado", "evaluacion_jurado", "evaluacion",
        "entregable_proyecto_acceso", "proyecto_estudiante_acceso",
        "item_cualitativo_jurado", "criterio_item_cualitativo_jurado"
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluacionCualitativaJuradoJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "evaluacion_jurado_id")
    private UUID evaluacionJuradoId;

    @Column(name = "estudiante_id")
    private UUID estudianteId;

    @Column(name = "item_id")
    private UUID itemId;

    @Column(name = "item_nombre")
    private String itemNombre;

    @Column(name = "item_descripcion")
    private String itemDescripcion;

    @Column(name = "criterio_id")
    private UUID criterioId;

    @Column(name = "criterio_nombre")
    private String criterioNombre;

    @Column(name = "criterio_descripcion")
    private String criterioDescripcion;
}
