package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.secondaryadapter.entity;

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
@Table(name = "evaluacion_cualitativa_asesor")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluacionCualitativaAsesorJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "evaluacion_asesor_id", nullable = false)
    private UUID evaluacionAsesor;

    @Column(name = "item_id", nullable = false)
    private UUID item;

    @Column(name = "criterio_id", nullable = false)
    private UUID criterio;
}
