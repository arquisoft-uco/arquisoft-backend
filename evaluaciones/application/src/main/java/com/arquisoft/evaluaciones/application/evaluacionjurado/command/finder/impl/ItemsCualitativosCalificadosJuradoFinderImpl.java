package com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.mapper.ResumenEvaluacionMapper;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.ItemsCualitativosCalificadosJuradoFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ItemCualitativoCalificado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemsCualitativosCalificadosJuradoFinderImpl implements ItemsCualitativosCalificadosJuradoFinder {

    private final EvaluacionJuradoOutputPort outputPort;

    @Override
    public List<ItemCualitativoCalificado> obtener(UUID evaluacionJurado) {
        return outputPort.consultarCualitativosCalificados(evaluacionJurado).stream()
                .map(ResumenEvaluacionMapper::toDomain)
                .toList();
    }
}
