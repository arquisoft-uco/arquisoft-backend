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
               o.descripcion             AS descripcion,
               o.estado_observacion_id   AS estado_id,
               eo.nombre                 AS estado_nombre
        FROM observacion o
                 JOIN estado_observacion eo ON eo.id = o.estado_observacion_id
        """)
@Synchronize({"observacion", "estado_observacion"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservacionJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "revision_asesor_id", columnDefinition = "uuid")
    private UUID revisionAsesor;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "estado_id")
    private String estado;

    @Column(name = "estado_nombre")
    private String estadoNombre;
}
