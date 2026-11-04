package com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.query.secondaryadapter.repository;

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
        SELECT i.id            AS id,
               i.nombre        AS nombre,
               i.descripcion   AS descripcion
        FROM criterio_item_cualitativo_asesor i
        """)
@Synchronize("criterio_item_cualitativo_asesor")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CriterioItemCualitativoAsesorJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;
}
