package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.query.secondaryadapter.repository;

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
               o.evaluacion_cuantitativa_jurado_id AS evaluacion_cuantitativa_jurado_id,
               o.descripcion                       AS descripcion
        FROM observacion_item_jurado o
        """)
@Synchronize("observacion_item_jurado")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservacionItemJuradoJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "evaluacion_cuantitativa_jurado_id")
    private UUID evaluacionCuantitativaJuradoId;

    @Column(name = "descripcion")
    private String descripcion;
}
