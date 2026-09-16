package com.arquisoft.fichas.infrastructure.observacionitem.command.secondaryadapter.entity;

import com.arquisoft.fichas.infrastructure.estadoobservacionrevision.command.secondaryadapter.entity.EstadoObservacionRevisionJpaEntity;
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

import java.util.UUID;

@Entity
@Table(name = "observacion_item")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservacionItemJpaEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "revision_item_id", nullable = false)
    private UUID revisionItemId;

    @Column(name = "observacion", nullable = false, length = 200)
    private String observacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_observacion_revision_id", nullable = false)
    private EstadoObservacionRevisionJpaEntity estadoObservacionRevision;
}
