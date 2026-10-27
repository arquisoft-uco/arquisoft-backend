package com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.secondaryadapter.entity;

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
@Table(name = "estudiante_artefacto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstudianteArtefactoJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "artefacto_id", nullable = false, columnDefinition = "UUID")
    private UUID artefacto;

    @Column(name = "estudiante_id", nullable = false, columnDefinition = "UUID")
    private UUID estudiante;
}
