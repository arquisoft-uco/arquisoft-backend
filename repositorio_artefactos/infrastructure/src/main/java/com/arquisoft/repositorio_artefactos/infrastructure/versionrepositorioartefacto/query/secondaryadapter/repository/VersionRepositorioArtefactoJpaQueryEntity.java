package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.secondaryadapter.repository;

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
        SELECT v.id                       AS id,
               v.nombre_documento         AS nombre_documento,
               v.numero_version           AS numero_version,
               v.fecha_creacion           AS fecha_creacion,
               v.repositorio_artefacto_id AS repositorio_artefacto_id,
               v.tipo_contenido           AS tipo_contenido,
               v.tamano_bytes             AS tamano_bytes,
               v.nombre_archivo           AS nombre_archivo,
               v.hash_sha256              AS hash_sha256
        FROM version_repositorio_artefacto v
        """)
@Synchronize({"version_repositorio_artefacto"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VersionRepositorioArtefactoJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "nombre_documento")
    private String nombreDocumento;

    @Column(name = "numero_version")
    private int numeroVersion;

    @Column(name = "fecha_creacion")
    private Instant fechaCreacion;

    @Column(name = "repositorio_artefacto_id", columnDefinition = "uuid")
    private UUID repositorioArtefacto;

    @Column(name = "tipo_contenido")
    private String tipoContenido;

    @Column(name = "tamano_bytes")
    private long tamanoBytes;

    @Column(name = "nombre_archivo")
    private String nombreArchivo;

    @Column(name = "hash_sha256")
    private String hashSha256;
}
