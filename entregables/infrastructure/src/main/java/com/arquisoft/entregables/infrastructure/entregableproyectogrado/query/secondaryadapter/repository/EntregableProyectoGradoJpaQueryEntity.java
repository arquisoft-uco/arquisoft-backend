package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.secondaryadapter.repository;

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
        SELECT e.id                                 AS id,
               CAST(e.version_entregable AS BIGINT) AS version_entregable,
               p.id                                 AS proyecto_grado_id,
               p.nombre                             AS proyecto_grado_nombre,
               a.id                                 AS asesor_id,
               a.nombre                             AS asesor_nombre,
               a.email                              AS asesor_email,
               s.id                                 AS estado_id,
               s.nombre                             AS estado_nombre
        FROM entregable_proyecto_grado e
                 JOIN proyecto_grado p ON p.id = e.proyecto_grado_id
                 JOIN asesor a ON a.id = e.asesor_id
                 JOIN estado_entregable s ON s.id = e.estado_entregable_id
        """)
@Synchronize({"entregable_proyecto_grado", "proyecto_grado", "asesor", "estado_entregable"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntregableProyectoGradoJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "version_entregable")
    private Long versionEntregable;

    @Column(name = "proyecto_grado_id", columnDefinition = "uuid")
    private UUID proyectoGradoId;

    @Column(name = "proyecto_grado_nombre")
    private String proyectoGradoNombre;

    @Column(name = "asesor_id", columnDefinition = "uuid")
    private UUID asesorId;

    @Column(name = "asesor_nombre")
    private String asesorNombre;

    @Column(name = "asesor_email")
    private String asesorEmail;

    @Column(name = "estado_id")
    private String estadoId;

    @Column(name = "estado_nombre")
    private String estadoNombre;
}
