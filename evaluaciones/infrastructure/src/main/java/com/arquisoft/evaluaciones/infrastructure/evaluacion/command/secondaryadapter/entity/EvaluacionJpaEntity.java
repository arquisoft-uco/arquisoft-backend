package com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.entity;

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
}
