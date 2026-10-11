package com.arquisoft.fichas.infrastructure.observacionitem.query.secondaryadapter.repository;

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
        SELECT oi.id                             AS id,
               oi.revision_item_id               AS revision_item_id,
               f.asesor_ficha_id                 AS asesor_id,
               oi.observacion                    AS observacion,
               oi.estado_observacion_revision_id AS estado_observacion_revision_id,
               eor.nombre                        AS estado_observacion_revision_nombre,
               CASE oi.estado_observacion_revision_id
                   WHEN 'PENDIENTE'   THEN 1
                   WHEN 'EN_PROGRESO' THEN 2
                   WHEN 'CERRADO'     THEN 3
                   ELSE 4
               END                               AS estado_orden
        FROM observacion_item oi
                 JOIN revision_item ri ON ri.id = oi.revision_item_id
                 JOIN item i ON i.id = ri.item_id
                 JOIN ficha_perfil f ON f.id = i.ficha_perfil_id
                 JOIN estado_observacion_revision eor ON eor.id = oi.estado_observacion_revision_id
        """)
@Synchronize({"observacion_item", "revision_item", "item", "ficha_perfil", "estado_observacion_revision"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservacionItemJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "revision_item_id", columnDefinition = "uuid")
    private UUID revisionItemId;

    @Column(name = "asesor_id", columnDefinition = "uuid")
    private UUID asesorId;

    @Column(name = "observacion")
    private String observacion;

    @Column(name = "estado_observacion_revision_id")
    private String estadoObservacionRevisionId;

    @Column(name = "estado_observacion_revision_nombre")
    private String estadoObservacionRevisionNombre;

    @Column(name = "estado_orden")
    private Integer estadoOrden;
}
