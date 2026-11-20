package com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "evaluacion")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluacionJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "entregable_id", nullable = false, columnDefinition = "uuid")
    private UUID entregable;

    @Column(name = "estado_evaluacion_id", nullable = false, length = 60)
    private String estadoEvaluacion;

    @Column(name = "nota_asesor", precision = 4, scale = 2)
    private BigDecimal notaAsesor;

    @Column(name = "nota_jurados", precision = 4, scale = 2)
    private BigDecimal notaJurados;

    @Column(name = "peso_asesor", precision = 5, scale = 4)
    private BigDecimal pesoAsesor;

    @Column(name = "peso_jurados", precision = 5, scale = 4)
    private BigDecimal pesoJurados;

    @Column(name = "cantidad_jurados")
    private Integer cantidadJurados;

    @Column(name = "nota_final", precision = 4, scale = 2)
    private BigDecimal notaFinal;

    @Column(name = "resultado_evaluacion_id", length = 60)
    private String resultadoEvaluacion;
}
