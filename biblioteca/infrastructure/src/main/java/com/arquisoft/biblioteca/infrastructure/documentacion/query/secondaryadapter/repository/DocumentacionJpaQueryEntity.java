package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository;

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

import java.time.Instant;
import java.util.UUID;

@Entity
@Immutable
@Subselect("""
        SELECT d.id                                 AS id,
               d.url                                AS url,
               d.fecha_publicacion                  AS fecha_publicacion,
               e.id                                 AS entregable_id,
               e.proyecto_grado_id                  AS proyecto_grado_id,
               CAST(e.version_entregable AS BIGINT) AS entregable_version,
               e.estado_entregable_nombre           AS entregable_estado,
               b.id                                 AS bibliotecario_id,
               b.nombre                             AS bibliotecario_nombre,
               s.id                                 AS estado_id,
               s.nombre                             AS estado_nombre
        FROM documentacion d
                 JOIN entregable_proyecto_grado e ON e.id = d.entregable_proyecto_grado_id
                 JOIN bibliotecario b ON b.id = d.bibliotecario_id
                 JOIN estado_documentacion s ON s.id = d.estado_documentacion_id
        """)
@Synchronize({"documentacion", "entregable_proyecto_grado", "bibliotecario", "estado_documentacion"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentacionJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "url")
    private String url;

    @Column(name = "fecha_publicacion")
    private Instant fechaPublicacion;

    @Column(name = "entregable_id", columnDefinition = "uuid")
    private UUID entregableId;

    @Column(name = "proyecto_grado_id", columnDefinition = "uuid")
    private UUID proyectoGradoId;

    @Column(name = "entregable_version")
    private Long entregableVersion;

    @Column(name = "entregable_estado")
    private String entregableEstado;

    @Column(name = "bibliotecario_id", columnDefinition = "uuid")
    private UUID bibliotecarioId;

    @Column(name = "bibliotecario_nombre")
    private String bibliotecarioNombre;

    @Column(name = "estado_id")
    private String estadoId;

    @Column(name = "estado_nombre")
    private String estadoNombre;
}
