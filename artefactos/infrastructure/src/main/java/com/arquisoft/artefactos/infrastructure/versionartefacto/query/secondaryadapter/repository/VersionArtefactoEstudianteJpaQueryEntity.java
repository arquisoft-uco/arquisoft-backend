package com.arquisoft.artefactos.infrastructure.versionartefacto.query.secondaryadapter.repository;

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
        SELECT va.id                AS id,
               va.artefacto_id      AS artefacto_id,
               va.version_artefacto AS version,
               va.fecha_creacion    AS fecha_creacion,
               va.uri_archivo       AS uri_archivo,
               ea.estudiante_id         AS estudiante_id
        FROM version_artefacto va
                 JOIN estudiante_artefacto ea ON ea.artefacto_id = va.artefacto_id
        """)
@Synchronize({"version_artefacto", "estudiante_artefacto"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VersionArtefactoEstudianteJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "artefacto_id", columnDefinition = "uuid")
    private UUID artefactoId;

    @Column(name = "version")
    private int version;

    @Column(name = "fecha_creacion")
    private Instant fechaCreacion;

    @Column(name = "uri_archivo")
    private String uriArchivo;

    @Column(name = "estudiante_id", columnDefinition = "uuid")
    private UUID estudianteId;
}
