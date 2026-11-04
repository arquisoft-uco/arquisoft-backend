package com.arquisoft.artefactos.infrastructure.observacion.query.secondaryadapter.repository;

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
        SELECT o.id                      AS id,
               o.revision_asesor_id      AS revision_asesor_id,
               ea.estudiante_id          AS estudiante_id,
               o.descripcion             AS descripcion,
               o.estado_observacion_id   AS estado_id,
               eo.nombre                 AS estado_nombre
        FROM observacion o
                 JOIN estado_observacion eo ON eo.id = o.estado_observacion_id
                 JOIN revision_asesor ra ON ra.id = o.revision_asesor_id
                 JOIN version_artefacto va ON va.id = ra.version_artefacto_id
                 JOIN estudiante_artefacto ea ON ea.artefacto_id = va.artefacto_id
        """)
@Synchronize({"observacion", "estado_observacion", "revision_asesor", "version_artefacto", "estudiante_artefacto"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservacionEstudianteJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "revision_asesor_id", columnDefinition = "uuid")
    private UUID revisionAsesor;

    @Column(name = "estudiante_id", columnDefinition = "uuid")
    private UUID estudiante;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "estado_id")
    private String estado;

    @Column(name = "estado_nombre")
    private String estadoNombre;
}
