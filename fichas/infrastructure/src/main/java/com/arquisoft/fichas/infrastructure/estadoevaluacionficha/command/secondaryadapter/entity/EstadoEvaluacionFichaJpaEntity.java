package com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.secondaryadapter.entity;

import com.arquisoft.fichas.infrastructure.estadoevaluacion.command.secondaryadapter.entity.EstadoEvaluacionJpaEntity;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.entity.EvaluacionFichaPerfilJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "estado_evaluacion_ficha")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoEvaluacionFichaJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluacion_ficha_perfil_id", nullable = false)
    private EvaluacionFichaPerfilJpaEntity evaluacionFichaPerfil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_evaluacion_id", nullable = false)
    private EstadoEvaluacionJpaEntity estadoEvaluacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion;
}
