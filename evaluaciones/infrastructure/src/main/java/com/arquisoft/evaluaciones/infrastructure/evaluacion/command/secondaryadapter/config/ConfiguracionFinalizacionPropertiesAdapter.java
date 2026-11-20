package com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.config;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.ConfiguracionFinalizacionOutputPort;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.PesosFinalizacionEntity;
import com.arquisoft.evaluaciones.infrastructure.config.EvaluacionesFinalizacionProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfiguracionFinalizacionPropertiesAdapter implements ConfiguracionFinalizacionOutputPort {

    private final EvaluacionesFinalizacionProperties properties;

    @Override
    public PesosFinalizacionEntity obtenerPesos() {
        return new PesosFinalizacionEntity(properties.getPesoAsesor(), properties.getPesoJurado());
    }
}
