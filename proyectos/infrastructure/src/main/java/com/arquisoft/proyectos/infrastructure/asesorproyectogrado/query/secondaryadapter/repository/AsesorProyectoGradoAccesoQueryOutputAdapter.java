package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.secondaryport.AsesorProyectoGradoAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorProyectoGradoAccesoQueryOutputAdapter implements AsesorProyectoGradoAccesoQueryOutputPort {

    private final AsesorProyectoGradoQueryRepository repository;

    @Override
    public boolean existeAsignacionVigente(UUID asesor, UUID proyectoGrado, String estadoAsesor) {
        return repository.existsByProyectoGradoIdAndAsesorIdAndEstadoAsesorAndActualTrue(
                proyectoGrado, asesor, estadoAsesor);
    }
}
