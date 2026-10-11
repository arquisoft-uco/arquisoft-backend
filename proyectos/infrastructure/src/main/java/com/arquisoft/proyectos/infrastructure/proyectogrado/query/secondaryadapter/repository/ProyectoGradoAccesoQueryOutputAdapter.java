package com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository;

import com.arquisoft.proyectos.application.proyectogrado.query.secondaryport.ProyectoGradoAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProyectoGradoAccesoQueryOutputAdapter implements ProyectoGradoAccesoQueryOutputPort {

    private final ProyectoGradoAccesoQueryRepository repository;

    @Override
    public Optional<UUID> obtenerCoordinadorPorId(UUID proyectoGrado) {
        return repository.obtenerCoordinadorPorId(proyectoGrado);
    }
}
