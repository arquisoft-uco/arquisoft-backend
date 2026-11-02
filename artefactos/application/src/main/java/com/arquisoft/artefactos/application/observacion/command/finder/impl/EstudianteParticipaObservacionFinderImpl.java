package com.arquisoft.artefactos.application.observacion.command.finder.impl;

import com.arquisoft.artefactos.application.observacion.command.finder.EstudianteParticipaObservacionFinder;
import com.arquisoft.artefactos.application.observacion.command.finder.model.ParticipacionEstudianteObservacion;
import com.arquisoft.artefactos.application.observacion.command.secondaryport.ObservacionOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstudianteParticipaObservacionFinderImpl implements EstudianteParticipaObservacionFinder {

    private final ObservacionOutputPort observacionOutputPort;

    @Override
    public Boolean obtener(ParticipacionEstudianteObservacion entrada) {
        return observacionOutputPort.existeParticipacionEstudiante(entrada.observacion(), entrada.estudiante());
    }
}
