package com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.query.secondaryadapter.repository;

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
        SELECT i.id          AS id,
               i.nombre      AS nombre,
               i.descripcion AS descripcion
        FROM item_cualitativo_jurado i
        """)
@Synchronize("item_cualitativo_jurado")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCualitativoJuradoJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;
}
