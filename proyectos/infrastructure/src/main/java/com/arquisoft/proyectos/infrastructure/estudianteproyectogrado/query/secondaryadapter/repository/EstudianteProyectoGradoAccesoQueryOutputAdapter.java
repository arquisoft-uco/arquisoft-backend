package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.secondaryport.EstudianteProyectoGradoAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteProyectoGradoAccesoQueryOutputAdapter implements EstudianteProyectoGradoAccesoQueryOutputPort {

    private final EstudianteProyectoGradoQueryRepository repository;

    @Override
    public boolean existeVinculoVigente(UUID proyectoGrado, UUID estudiante) {
        return repository.existsByProyectoGradoIdAndEstudianteIdAndVigenteTrue(proyectoGrado, estudiante);
    }
}
