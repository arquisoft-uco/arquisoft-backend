package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.secondaryadapter.entity;

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
@Table(name = "evaluacion_cuantitativa_jurado")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluacionCuantitativaJuradoJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "evaluacion_jurado_id", nullable = false, columnDefinition = "uuid")
    private UUID evaluacionJuradoId;

    @Column(name = "item_id", nullable = false, columnDefinition = "uuid")
    private UUID itemId;

    @Column(name = "puntaje", nullable = false)
    private Integer puntaje;
}
