package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.query.secondaryadapter.repository;

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
        SELECT eca.id                     AS id,
               eca.evaluacion_asesor_id   AS evaluacion_asesor_id,
               eca.puntaje                AS puntaje,
               i.id                       AS item_id,
               i.nombre                   AS item_nombre,
               i.descripcion              AS item_descripcion,
               i.categoria_id             AS item_categoria_id,
               i.valor                    AS item_valor
        FROM evaluacion_cuantitativa_asesor eca
        JOIN item_cuantitativo_asesor i ON i.id = eca.item_id
        """)
@Synchronize({"evaluacion_cuantitativa_asesor", "item_cuantitativo_asesor"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluacionCuantitativaAsesorJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "evaluacion_asesor_id")
    private UUID evaluacionAsesorId;

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
