package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.secondaryadapter.entity;

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
@Table(name = "evaluacion_cualitativa_jurado")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluacionCualitativaJuradoJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "evaluacion_jurado_id", nullable = false)
    private UUID evaluacionJurado;

    @Column(name = "item_id", nullable = false)
    private UUID item;

    @Column(name = "criterio_id", nullable = false)
    private UUID criterio;
}
