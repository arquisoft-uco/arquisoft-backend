package com.arquisoft.fichas.infrastructure.estadoevaluacionficha.persistence;

import com.arquisoft.fichas.infrastructure.estadoevaluacion.persistence.EstadoEvaluacionEntity;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence.EvaluacionFichaPerfilEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class EstadoEvaluacionFichaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "evaluacion_ficha_perfil_id", nullable = false)
    private EvaluacionFichaPerfilEntity evaluacionFichaPerfil;

    @ManyToOne
    @JoinColumn(name = "estado_evaluacion_id", nullable = false)
    private EstadoEvaluacionEntity estadoEvaluacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion;
}
