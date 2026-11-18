package com.arquisoft.entregables.application.entregableproyectogrado.query.finder.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.query.finder.EstudianteParticipaEntregableQueryFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.query.finder.model.EstudianteEntregable;
import com.arquisoft.entregables.application.entregableproyectogrado.query.secondaryport.EntregableProyectoGradoAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstudianteParticipaEntregableQueryFinderImpl implements EstudianteParticipaEntregableQueryFinder {

    private final EntregableProyectoGradoAccesoQueryOutputPort entregableProyectoGradoAccesoQueryOutputPort;

    @Override
    public Boolean obtener(EstudianteEntregable entrada) {
        return entregableProyectoGradoAccesoQueryOutputPort.estudianteParticipa(
                entrada.entregableProyectoGrado(), entrada.estudiante());
    }
}
