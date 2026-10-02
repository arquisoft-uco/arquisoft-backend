package com.arquisoft.fichas.infrastructure.observacionevaluacion.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.ObservacionEvaluacionOutputPort;
import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.entity.ObservacionEvaluacionEntity;
import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.entity.PertenenciaObservacionEvaluacionEntity;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.command.secondaryadapter.mapper.ObservacionEvaluacionJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ObservacionEvaluacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ObservacionEvaluacionCommandOutputAdapter implements ObservacionEvaluacionOutputPort {

    private final ObservacionEvaluacionCommandRepository repository;
    private final AppLogger logger;

    @Override
    public void registrarObservacion(ObservacionEvaluacionEntity observacion) {
        repository.save(ObservacionEvaluacionJpaMapper.toJpaEntity(observacion));
        logger.debug(ObservacionEvaluacionKey.LOG_GUARDADA, observacion.id());
    }

    @Override
    public boolean existePorEvaluacionYObservacion(UUID evaluacionFichaPerfil, String observacion) {
        return repository.existsByEvaluacionFichaPerfilIdAndObservacion(evaluacionFichaPerfil, observacion);
    }

    @Override
    public Optional<PertenenciaObservacionEvaluacionEntity> obtenerPertenencia(UUID observacionEvaluacion,
                                                                               UUID representanteComite) {
        return repository.obtenerPertenencia(observacionEvaluacion, representanteComite);
    }

    @Override
    public boolean existeOtraConMismoTexto(UUID observacionEvaluacion, String observacion) {
        return repository.existeOtraConMismoTexto(observacionEvaluacion, observacion);
    }

    @Override
    public void actualizarObservacion(UUID observacionEvaluacion, String observacion) {
        repository.actualizarObservacion(observacionEvaluacion, observacion);
        logger.debug(ObservacionEvaluacionKey.LOG_ACTUALIZADA, observacionEvaluacion);
    }
}
