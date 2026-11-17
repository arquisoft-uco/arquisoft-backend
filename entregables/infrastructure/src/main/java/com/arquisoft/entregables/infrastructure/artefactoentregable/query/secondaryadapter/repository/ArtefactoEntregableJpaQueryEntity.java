package com.arquisoft.entregables.infrastructure.artefactoentregable.query.secondaryadapter.repository;

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
        SELECT ae.id                        AS id,
               ae.entregable_id             AS entregable_id,
               ae.id_version_artefacto      AS id_version_artefacto,
               ae.uri_archivo               AS uri_archivo,
               ae.artefacto_id              AS artefacto_id,
               ae.version_artefacto         AS version_artefacto,
               ae.id_repositorio_artefacto  AS id_repositorio_artefacto,
               ae.nombre_documento          AS nombre_documento,
               ae.id_repositorio            AS id_repositorio,
               ae.nombre_repositorio        AS nombre_repositorio
        FROM artefacto_entregable ae
        """)
@Synchronize("artefacto_entregable")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtefactoEntregableJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "entregable_id", columnDefinition = "uuid")
    private UUID entregableId;

    @Column(name = "id_version_artefacto", columnDefinition = "uuid")
    private UUID versionArtefacto;

    @Column(name = "uri_archivo")
    private String uriArchivo;

    @Column(name = "artefacto_id", columnDefinition = "uuid")
    private UUID artefacto;

    @Column(name = "version_artefacto")
    private int numeroVersion;

    @Column(name = "id_repositorio_artefacto", columnDefinition = "uuid")
    private UUID idRepositorioArtefacto;

    @Column(name = "nombre_documento")
    private String nombreDocumento;

    @Column(name = "id_repositorio", columnDefinition = "uuid")
    private UUID idRepositorio;

    @Column(name = "nombre_repositorio")
    private String nombreRepositorio;
}
