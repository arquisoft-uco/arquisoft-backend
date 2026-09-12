package com.arquisoft.fichas.infrastructure.revisionitem.query.secondaryadapter.repository;

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
        SELECT ri.id                  AS id,
               ri.item_id             AS item_id,
               f.asesor_ficha_id      AS asesor_id,
               ri.estado_revision_id  AS estado_revision_id,
               er.nombre              AS estado_revision_nombre,
               ri.fecha_creacion      AS fecha_creacion
        FROM revision_item ri
                 JOIN item i ON i.id = ri.item_id
                 JOIN ficha_perfil f ON f.id = i.ficha_perfil_id
                 JOIN estado_revision er ON er.id = ri.estado_revision_id
        """)
@Synchronize({"revision_item", "item", "ficha_perfil", "estado_revision"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevisionItemJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "item_id", columnDefinition = "uuid")
    private UUID itemId;

    @Column(name = "asesor_id", columnDefinition = "uuid")
    private UUID asesorId;

    @Column(name = "estado_revision_id")
    private String estadoRevisionId;

    @Column(name = "estado_revision_nombre")
    private String estadoRevisionNombre;

    @Column(name = "fecha_creacion")
    private Instant fechaCreacion;
}
