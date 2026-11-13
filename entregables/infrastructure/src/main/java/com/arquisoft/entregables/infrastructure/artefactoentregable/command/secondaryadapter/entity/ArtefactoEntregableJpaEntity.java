package com.arquisoft.entregables.infrastructure.artefactoentregable.command.secondaryadapter.entity;

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
@Table(name = "artefacto_entregable")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtefactoEntregableJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "artefacto_id", nullable = false, columnDefinition = "UUID")
    private UUID artefactoId;

    @Column(name = "entregable_id", nullable = false, columnDefinition = "UUID")
    private UUID entregableId;

    @Column(name = "uri_archivo", nullable = false, length = 2000)
    private String uriArchivo;
}
