package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.query.secondaryadapter.repository;

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
        SELECT o.id                                AS id,
               o.evaluacion_cuantitativa_asesor_id AS evaluacion_cuantitativa_asesor_id,
               o.descripcion                       AS descripcion
        FROM observacion_item_asesor o
        """)
@Synchronize("observacion_item_asesor")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservacionItemAsesorJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "evaluacion_cuantitativa_asesor_id")
    private UUID evaluacionCuantitativaAsesorId;

    @Column(name = "descripcion")
    private String descripcion;
}
