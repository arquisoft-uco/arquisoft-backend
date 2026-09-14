package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.EvaluacionCuantitativaJuradoPorIdFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.EvaluacionCuantitativaJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.mapper.EvaluacionCuantitativaJuradoMapper;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.EvaluacionCuantitativaJuradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionCuantitativaJuradoPorIdFinderImpl implements EvaluacionCuantitativaJuradoPorIdFinder {

    private final EvaluacionCuantitativaJuradoOutputPort outputPort;

    @Override
    public Optional<EvaluacionCuantitativaJuradoDomain> obtener(UUID id) {
        return outputPort.obtenerPorId(id).map(EvaluacionCuantitativaJuradoMapper::toDomain);
    }
}
