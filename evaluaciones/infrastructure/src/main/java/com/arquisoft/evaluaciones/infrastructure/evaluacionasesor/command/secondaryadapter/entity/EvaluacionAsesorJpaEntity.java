package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.entity;

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
@Table(name = "evaluacion_asesor")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluacionAsesorJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "evaluacion_id", nullable = false, columnDefinition = "uuid")
    private UUID evaluacion;

    @Column(name = "asesor_id", nullable = false, columnDefinition = "uuid")
    private UUID asesor;
}
