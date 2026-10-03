package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.query.secondaryadapter.repository;

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
               i.descripcion   AS descripcion,
               i.categoria_id  AS categoria_id,
               i.valor         AS valor
        FROM item_cuantitativo_jurado i
        """)
@Synchronize("item_cuantitativo_jurado")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCuantitativoJuradoJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "categoria_id")
    private UUID categoriaId;

    @Column(name = "valor")
    private Integer valor;
}
