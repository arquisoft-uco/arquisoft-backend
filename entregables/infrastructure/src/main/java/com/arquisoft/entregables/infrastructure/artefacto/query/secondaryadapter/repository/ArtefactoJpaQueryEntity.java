package com.arquisoft.entregables.infrastructure.artefacto.query.secondaryadapter.repository;

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
        SELECT a.id                AS id,
               a.proyecto_grado_id AS proyecto_grado_id,
               a.nombre_documento  AS nombre_documento,
               a.version_artefacto AS version_artefacto
        FROM artefacto a
        WHERE a.omitido_en IS NULL
        """)
@Synchronize("artefacto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtefactoJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "proyecto_grado_id", columnDefinition = "uuid")
    private UUID proyectoGradoId;

    @Column(name = "nombre_documento")
    private String nombreDocumento;

    @Column(name = "version_artefacto")
    private int numeroVersion;
}
