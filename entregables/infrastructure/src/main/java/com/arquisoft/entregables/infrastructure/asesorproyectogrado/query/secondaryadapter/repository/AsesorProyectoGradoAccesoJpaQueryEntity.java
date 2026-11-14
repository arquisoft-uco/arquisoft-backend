package com.arquisoft.entregables.infrastructure.asesorproyectogrado.query.secondaryadapter.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.time.Instant;
import java.util.UUID;

@Entity
@Immutable
@Subselect("""
        SELECT a.id                AS id,
               a.asesor_id         AS asesor_id,
               a.proyecto_grado_id AS proyecto_grado_id,
               a.ocurrido_en       AS ocurrido_en,
               a.finalizado_en     AS finalizado_en
        FROM asesor_proyecto_grado a
        """)
@Synchronize({"asesor_proyecto_grado"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AsesorProyectoGradoAccesoJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "asesor_id")
    private UUID asesorId;

    @Column(name = "proyecto_grado_id")
    private UUID proyectoGradoId;

    @Column(name = "ocurrido_en")
    private Instant ocurridoEn;

    @Column(name = "finalizado_en")
    private Instant finalizadoEn;
}
