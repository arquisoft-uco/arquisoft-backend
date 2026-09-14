package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.EvaluacionCuantitativaJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.entity.EvaluacionCuantitativaJuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.secondaryadapter.mapper.EvaluacionCuantitativaJuradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionCuantitativaJuradoCommandOutputAdapter implements EvaluacionCuantitativaJuradoOutputPort {

    private final EvaluacionCuantitativaJuradoCommandRepository repository;
    private final AppLogger logger;

    @Override
    public Optional<EvaluacionCuantitativaJuradoEntity> obtenerPorId(UUID id) {
        return repository.findById(id).map(EvaluacionCuantitativaJuradoJpaMapper::toEntity);
    }

    @Override
    public void cambiarPuntaje(UUID id, Integer nuevoPuntaje) {
        repository.actualizarPuntaje(id, nuevoPuntaje);
        logger.debug(EvaluacionCuantitativaJuradoKey.LOG_GUARDADO, id);
    }
}
