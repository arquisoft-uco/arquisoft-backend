package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.secondaryadapter.repository;

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
               i.id                       AS item_id,
               i.nombre                   AS item_nombre,
               i.descripcion              AS item_descripcion,
               c.id                       AS criterio_id,
               c.nombre                   AS criterio_nombre,
               c.descripcion              AS criterio_descripcion
        FROM evaluacion_cualitativa_asesor eca
        JOIN item_cualitativo_asesor i ON i.id = eca.item_id
        JOIN criterio_item_cualitativo_asesor c ON c.id = eca.criterio_id
        """)
@Synchronize({
        "evaluacion_cualitativa_asesor", "item_cualitativo_asesor", "criterio_item_cualitativo_asesor"
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluacionCualitativaAsesorJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "evaluacion_asesor_id")
    private UUID evaluacionAsesorId;

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
