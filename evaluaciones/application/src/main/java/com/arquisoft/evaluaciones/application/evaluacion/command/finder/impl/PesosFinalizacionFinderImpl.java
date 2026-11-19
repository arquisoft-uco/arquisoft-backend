package com.arquisoft.evaluaciones.application.evaluacion.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.finder.PesosFinalizacionFinder;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.ConfiguracionFinalizacionOutputPort;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.mapper.ConsolidacionNotasEvaluacionMapper;
import com.arquisoft.evaluaciones.domain.evaluacion.model.PesosFinalizacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PesosFinalizacionFinderImpl implements PesosFinalizacionFinder {

    private final ConfiguracionFinalizacionOutputPort configuracionFinalizacionOutputPort;

    @Override
    public PesosFinalizacion obtener() {
        return ConsolidacionNotasEvaluacionMapper.toDomain(configuracionFinalizacionOutputPort.obtenerPesos());
    }
}
