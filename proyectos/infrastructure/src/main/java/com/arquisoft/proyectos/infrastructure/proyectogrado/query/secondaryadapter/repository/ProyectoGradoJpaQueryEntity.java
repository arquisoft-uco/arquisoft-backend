package com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.util.UUID;

@Entity
@Immutable
@Subselect("""
        SELECT pg.id             AS id,
               pg.coordinador_id AS coordinador_id
        FROM proyecto_grado pg
        """)
@Synchronize("proyecto_grado")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProyectoGradoJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "coordinador_id")
    private UUID coordinadorId;
}
