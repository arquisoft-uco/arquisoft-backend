package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder.EvaluacionCualitativaJuradoPorIdFinder;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.EvaluacionCualitativaJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.mapper.EvaluacionCualitativaJuradoMapper;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.EvaluacionCualitativaJuradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionCualitativaJuradoPorIdFinderImpl implements EvaluacionCualitativaJuradoPorIdFinder {

    private final EvaluacionCualitativaJuradoOutputPort outputPort;

    @Override
    public EvaluacionCualitativaJuradoDomain obtener(UUID id) {
        return outputPort.obtenerPorId(id)
                .map(EvaluacionCualitativaJuradoMapper::toDomain)
                .orElse(EvaluacionCualitativaJuradoDomain.VACIO);
    }
}
