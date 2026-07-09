package com.arquisoft.fichas.infrastructure.estadoevaluacionficha.persistence;

import com.arquisoft.fichas.infrastructure.estadoevaluacion.persistence.EstadoEvaluacionJpaEntity;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence.EvaluacionFichaPerfilJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "estado_evaluacion_ficha")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoEvaluacionFichaJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "evaluacion_ficha_perfil_id", nullable = false)
    private EvaluacionFichaPerfilJpaEntity evaluacionFichaPerfil;

    @ManyToOne
    @JoinColumn(name = "estado_evaluacion_id", nullable = false)
    private EstadoEvaluacionJpaEntity estadoEvaluacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion;
}
