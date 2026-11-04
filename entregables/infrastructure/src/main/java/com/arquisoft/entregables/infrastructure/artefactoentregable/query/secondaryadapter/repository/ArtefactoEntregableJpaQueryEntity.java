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
               a.id                         AS artefacto_id,
               a.uri_archivo                AS uri_archivo,
               a.id_artefacto               AS id_artefacto,
               a.id_repositorio_artefacto   AS id_repositorio_artefacto,
               a.nombre_documento           AS nombre_documento,
               a.id_repositorio             AS id_repositorio,
               a.nombre_repositorio         AS nombre_repositorio
        FROM artefacto_entregable ae
                 JOIN artefacto a ON a.id = ae.artefacto_id
        """)
@Synchronize({"artefacto_entregable", "artefacto"})
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

    @Column(name = "artefacto_id", columnDefinition = "uuid")
    private UUID artefactoId;

    @Column(name = "uri_archivo")
    private String uriArchivo;

    @Column(name = "id_artefacto", columnDefinition = "uuid")
    private UUID idArtefacto;

    @Column(name = "id_repositorio_artefacto", columnDefinition = "uuid")
    private UUID idRepositorioArtefacto;

    @Column(name = "nombre_documento")
    private String nombreDocumento;

    @Column(name = "id_repositorio", columnDefinition = "uuid")
    private UUID idRepositorio;

    @Column(name = "nombre_repositorio")
    private String nombreRepositorio;
}
