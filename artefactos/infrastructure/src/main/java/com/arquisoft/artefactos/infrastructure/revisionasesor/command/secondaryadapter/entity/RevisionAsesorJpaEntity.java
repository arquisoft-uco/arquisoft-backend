package com.arquisoft.artefactos.infrastructure.revisionasesor.command.secondaryadapter.entity;

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
@Table(name = "revision_asesor")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevisionAsesorJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "version_artefacto_id", nullable = false, columnDefinition = "uuid")
    private UUID versionArtefacto;

    @Column(name = "estado_revision_asesor_id", nullable = false, length = 60)
    private String estado;
}
