package com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.JuradosEvaluadosFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.model.CriterioJuradosEvaluacion;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JuradosEvaluadosFinderImpl implements JuradosEvaluadosFinder {

    private final EvaluacionJuradoOutputPort evaluacionJuradoOutputPort;

    @Override
    public Set<UUID> obtener(CriterioJuradosEvaluacion criterio) {
        return evaluacionJuradoOutputPort.consultarJuradosEvaluados(criterio.evaluacion(), criterio.jurados());
    }
}
