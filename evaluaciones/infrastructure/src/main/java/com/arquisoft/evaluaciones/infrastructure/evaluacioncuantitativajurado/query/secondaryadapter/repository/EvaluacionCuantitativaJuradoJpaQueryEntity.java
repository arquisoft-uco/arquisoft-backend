package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.query.secondaryadapter.repository;

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
               ecj.evaluacion_jurado_id AS evaluacion_jurado_id,
               pea.estudiante_id        AS estudiante_id,
               ecj.puntaje              AS puntaje,
               i.id                     AS item_id,
               i.nombre                 AS item_nombre,
               i.descripcion            AS item_descripcion,
               i.categoria_id           AS item_categoria_id,
               i.valor                  AS item_valor
        FROM evaluacion_cuantitativa_jurado ecj
        JOIN evaluacion_jurado ej ON ej.id = ecj.evaluacion_jurado_id
        JOIN evaluacion e ON e.id = ej.evaluacion_id
        JOIN entregable_proyecto_acceso epa ON epa.entregable_id = e.entregable_id AND epa.activo = true
        JOIN proyecto_estudiante_acceso pea ON pea.proyecto_id = epa.proyecto_id AND pea.activo = true
        JOIN item_cuantitativo_jurado i ON i.id = ecj.item_id
        """)
@Synchronize({
        "evaluacion_cuantitativa_jurado", "evaluacion_jurado", "evaluacion",
        "entregable_proyecto_acceso", "proyecto_estudiante_acceso", "item_cuantitativo_jurado"
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluacionCuantitativaJuradoJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "evaluacion_jurado_id")
    private UUID evaluacionJuradoId;

    @Column(name = "estudiante_id")
    private UUID estudianteId;

    @Column(name = "puntaje")
    private Integer puntaje;

    @Column(name = "item_id")
    private UUID itemId;

    @Column(name = "item_nombre")
    private String itemNombre;

    @Column(name = "item_descripcion")
    private String itemDescripcion;

    @Column(name = "item_categoria_id")
    private UUID itemCategoriaId;

    @Column(name = "item_valor")
    private Integer itemValor;
}
