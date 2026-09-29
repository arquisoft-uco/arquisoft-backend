package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder.ItemsEvaluacionCualitativaJuradoRegistradosFinder;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder.model.CriterioItemsEvaluacion;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.EvaluacionCualitativaJuradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemsEvaluacionCualitativaJuradoRegistradosFinderImpl
        implements ItemsEvaluacionCualitativaJuradoRegistradosFinder {

    private final EvaluacionCualitativaJuradoOutputPort evaluacionCualitativaJuradoOutputPort;

    @Override
    public Set<UUID> obtener(CriterioItemsEvaluacion criterio) {
        return evaluacionCualitativaJuradoOutputPort.consultarItemsRegistrados(
                criterio.evaluacionJurado(), criterio.items());
    }
}
