package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.secondaryadapter.repository;

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
        SELECT e.id                                 AS id,
               e.asesor_id                          AS asesor_id,
               e.proyecto_grado_id                  AS proyecto_grado_id,
               CAST(e.version_entregable AS BIGINT) AS version_entregable
        FROM entregable_proyecto_grado e
        """)
@Synchronize("entregable_proyecto_grado")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EntregableProyectoGradoAccesoJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "asesor_id")
    private UUID asesorId;

    @Column(name = "proyecto_grado_id")
    private UUID proyectoGradoId;

    @Column(name = "version_entregable")
    private Long versionEntregable;
}
