package com.arquisoft.entregables.infrastructure.proyectogrado.query.secondaryadapter.repository;

import com.arquisoft.entregables.application.proyectogrado.query.secondaryport.EstudianteProyectoGradoAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteProyectoGradoAccesoQueryOutputAdapter implements EstudianteProyectoGradoAccesoQueryOutputPort {

    private final EstudianteProyectoGradoAccesoQueryRepository repository;

    @Override
    public boolean perteneceAlProyecto(UUID estudiante, UUID proyectoGrado) {
        return repository.existsByEstudianteIdAndProyectoGradoId(estudiante, proyectoGrado);
    }
}
