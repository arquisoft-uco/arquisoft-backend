package com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativojurado.query.secondaryadapter.repository;

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
        SELECT c.id          AS id,
               c.nombre      AS nombre,
               c.descripcion AS descripcion
        FROM criterio_item_cualitativo_jurado c
        """)
@Synchronize("criterio_item_cualitativo_jurado")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CriterioItemCualitativoJuradoJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;
}
