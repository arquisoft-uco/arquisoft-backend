package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.ConteoEvaluacionesPorEstadoEntity;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.EstadoFichaDeEvaluacionEntity;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.EvaluacionExistenteEntity;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.EvaluacionFichaPerfilEntity;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.mapper.EvaluacionFichaPerfilJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EvaluacionFichaPerfilKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionFichaPerfilCommandOutputAdapter
        implements EvaluacionFichaPerfilOutputPort {

    private final EvaluacionFichaPerfilCommandRepository repository;
    private final AppLogger logger;

    @Override
    public void registrarEvaluacion(EvaluacionFichaPerfilEntity evaluacion) {
        repository.save(EvaluacionFichaPerfilJpaMapper.toJpaEntity(evaluacion));
        logger.debug(EvaluacionFichaPerfilKey.LOG_GUARDADA,
                evaluacion.id(), evaluacion.fichaPerfilId());
    }

    @Override
    public boolean existePorId(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public Optional<EvaluacionExistenteEntity> obtenerEvaluacionDeRepresentanteEnFicha(
            UUID representanteComiteId, UUID fichaPerfilId) {
        return repository.obtenerEvaluacionDeRepresentanteEnFicha(representanteComiteId, fichaPerfilId);
    }

    @Override
    public boolean esRepresentantePropietario(UUID evaluacionFichaPerfil, UUID representanteComite) {
        return repository.existsByIdAndRepresentanteComiteId(evaluacionFichaPerfil, representanteComite);
    }

    @Override
    public Optional<EstadoFichaDeEvaluacionEntity> obtenerEstadoFichaActual(UUID evaluacionFichaPerfilId) {
        return repository.obtenerEstadoFichaActual(evaluacionFichaPerfilId);
    }

    @Override
    public List<ConteoEvaluacionesPorEstadoEntity> contarEvaluacionesDeFichaPorEstadoEvaluacionActual(UUID fichaPerfil) {
        return repository.contarEvaluacionesDeFichaPorEstadoEvaluacionActual(fichaPerfil);
    }
}
