package com.arquisoft.fichas.infrastructure.estadoevaluacion.query.secondaryadapter.repository;

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

@Entity
@Immutable
@Subselect("""
        SELECT e.id          AS id,
               e.nombre      AS nombre,
               e.descripcion AS descripcion
        FROM estado_evaluacion e
        """)
@Synchronize("estado_evaluacion")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoEvaluacionJpaQueryEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;
}
