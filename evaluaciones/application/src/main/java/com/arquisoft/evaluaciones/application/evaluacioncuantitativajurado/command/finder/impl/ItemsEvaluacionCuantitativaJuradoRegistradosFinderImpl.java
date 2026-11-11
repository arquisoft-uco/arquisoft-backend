package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.ItemsEvaluacionCuantitativaJuradoRegistradosFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.model.ItemsPorEvaluacionCuantitativaJurado;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.EvaluacionCuantitativaJuradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemsEvaluacionCuantitativaJuradoRegistradosFinderImpl
        implements ItemsEvaluacionCuantitativaJuradoRegistradosFinder {

    private final EvaluacionCuantitativaJuradoOutputPort evaluacionCuantitativaJuradoOutputPort;

    @Override
    public Set<UUID> obtener(ItemsPorEvaluacionCuantitativaJurado criterio) {
        return evaluacionCuantitativaJuradoOutputPort.consultarItemsRegistrados(
                criterio.evaluacionJurado(), criterio.items());
    }
}
