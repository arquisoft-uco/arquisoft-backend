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
               efp.estudiante_id      AS estudiante_id,
               ri.estado_revision_id  AS estado_revision_id,
               er.nombre              AS estado_revision_nombre,
               ri.fecha_creacion      AS fecha_creacion
        FROM revision_item ri
                 JOIN item i ON i.id = ri.item_id
                 JOIN estudiante_ficha_perfil efp ON efp.ficha_perfil_id = i.ficha_perfil_id
                 JOIN estado_revision er ON er.id = ri.estado_revision_id
        WHERE EXISTS (
            SELECT 1 FROM observacion_item oi WHERE oi.revision_item_id = ri.id
        )
        """)
@Synchronize({"revision_item", "item", "estudiante_ficha_perfil", "estado_revision", "observacion_item"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevisionItemEstudianteJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "item_id", columnDefinition = "uuid")
    private UUID itemId;

    @Column(name = "estudiante_id", columnDefinition = "uuid")
    private UUID estudianteId;

    @Column(name = "estado_revision_id")
    private String estadoRevisionId;

    @Column(name = "estado_revision_nombre")
    private String estadoRevisionNombre;

    @Column(name = "fecha_creacion")
    private Instant fechaCreacion;
}
