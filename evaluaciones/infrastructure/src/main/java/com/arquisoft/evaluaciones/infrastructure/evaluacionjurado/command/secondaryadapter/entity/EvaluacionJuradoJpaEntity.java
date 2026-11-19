package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "evaluacion_jurado")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluacionJuradoJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "evaluacion_id", nullable = false, columnDefinition = "uuid")
    private UUID evaluacion;

    @Column(name = "jurado_id", nullable = false, columnDefinition = "uuid")
    private UUID jurado;

    @Column(name = "estado_evaluacion_asignada_jurado_id", nullable = false, length = 60)
    private String estado;

    @Column(name = "eliminado_en")
    private Instant eliminadoEn;

    @Column(name = "resumen_cualitativo", precision = 4, scale = 2)
    private BigDecimal resumenCualitativo;

    @Column(name = "resumen_cuantitativo", precision = 4, scale = 2)
    private BigDecimal resumenCuantitativo;

    @Column(name = "nota_acumulada", precision = 4, scale = 2)
    private BigDecimal notaAcumulada;
}
