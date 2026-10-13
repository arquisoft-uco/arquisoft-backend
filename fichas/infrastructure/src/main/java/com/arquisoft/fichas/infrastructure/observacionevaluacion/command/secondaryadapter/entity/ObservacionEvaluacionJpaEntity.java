package com.arquisoft.fichas.infrastructure.observacionevaluacion.command.secondaryadapter.entity;

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
@Table(name = "observacion_evaluacion")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservacionEvaluacionJpaEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "evaluacion_ficha_perfil_id", nullable = false)
    private UUID evaluacionFichaPerfilId;

    @Column(name = "observacion", nullable = false, length = 200)
    private String observacion;
}
