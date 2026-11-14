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
               (SELECT CASE WHEN av.finalizado_en IS NULL THEN av.asesor_id END
                FROM asesor_proyecto_grado av
                WHERE av.proyecto_grado_id = e.proyecto_grado_id
                ORDER BY av.ocurrido_en DESC, av.asesor_id
                FETCH FIRST 1 ROWS ONLY) AS asesor_vigente_id,
               e.proyecto_grado_id                  AS proyecto_grado_id,
               CAST(e.version_entregable AS BIGINT) AS version_entregable,
               (pg.estado_proyecto_id = 'CANCELADO') AS proyecto_cancelado
        FROM entregable_proyecto_grado e
        JOIN proyecto_grado pg ON pg.id = e.proyecto_grado_id
        """)
@Synchronize({"entregable_proyecto_grado", "proyecto_grado", "asesor_proyecto_grado"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EntregableProyectoGradoAccesoJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "asesor_vigente_id")
    private UUID asesorVigenteId;

    @Column(name = "proyecto_grado_id")
    private UUID proyectoGradoId;

    @Column(name = "version_entregable")
    private Long versionEntregable;

    @Column(name = "proyecto_cancelado")
    private boolean proyectoCancelado;
}
