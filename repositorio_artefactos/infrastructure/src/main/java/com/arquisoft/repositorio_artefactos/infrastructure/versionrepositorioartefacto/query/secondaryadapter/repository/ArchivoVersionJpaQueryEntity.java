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

import java.util.UUID;

@Entity
@Immutable
@Subselect("""
        SELECT v.id             AS id,
               v.uri_archivo    AS uri_archivo,
               v.nombre_archivo AS nombre_archivo
        FROM version_repositorio_artefacto v
        """)
@Synchronize({"version_repositorio_artefacto"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArchivoVersionJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "uri_archivo")
    private String uriArchivo;

    @Column(name = "nombre_archivo")
    private String nombreArchivo;
}
