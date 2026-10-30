package com.arquisoft.artefactos.infrastructure.observacion.command.secondaryadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "observacion")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservacionJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "revision_asesor_id", nullable = false, columnDefinition = "uuid")
    private UUID revisionAsesor;

    @Column(name = "descripcion", nullable = false, length = 1000)
    private String descripcion;

    @Column(name = "estado_observacion_id", nullable = false, length = 60)
    private String estado;
}
