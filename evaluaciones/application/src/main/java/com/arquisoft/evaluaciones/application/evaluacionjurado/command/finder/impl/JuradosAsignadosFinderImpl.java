package com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.JuradosAsignadosFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.model.CriterioJuradosEvaluacion;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JuradosAsignadosFinderImpl implements JuradosAsignadosFinder {

    private final EvaluacionJuradoOutputPort evaluacionJuradoOutputPort;

    @Override
    public Set<UUID> obtener(CriterioJuradosEvaluacion criterio) {
        return evaluacionJuradoOutputPort.consultarJuradosAsignados(criterio.evaluacion(), criterio.jurados());
    }
}
