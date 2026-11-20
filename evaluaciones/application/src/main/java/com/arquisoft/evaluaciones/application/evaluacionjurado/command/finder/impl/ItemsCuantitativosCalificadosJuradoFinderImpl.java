package com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.mapper.ResumenEvaluacionMapper;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.ItemsCuantitativosCalificadosJuradoFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ItemCuantitativoCalificado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemsCuantitativosCalificadosJuradoFinderImpl implements ItemsCuantitativosCalificadosJuradoFinder {

    private final EvaluacionJuradoOutputPort outputPort;

    @Override
    public List<ItemCuantitativoCalificado> obtener(UUID evaluacionJurado) {
        return outputPort.consultarCuantitativosCalificados(evaluacionJurado).stream()
                .map(ResumenEvaluacionMapper::toDomain)
                .toList();
    }
}
