package com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.entity;

import com.arquisoft.fichas.infrastructure.estadorevision.command.secondaryadapter.entity.EstadoRevisionJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "revision_item")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevisionItemJpaEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_revision_id", nullable = false)
    private EstadoRevisionJpaEntity estadoRevision;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;
}
