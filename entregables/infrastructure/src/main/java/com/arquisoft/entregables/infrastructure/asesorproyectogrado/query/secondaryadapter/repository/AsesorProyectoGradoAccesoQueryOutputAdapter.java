package com.arquisoft.entregables.infrastructure.asesorproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.entregables.application.asesorproyectogrado.query.secondaryport.AsesorProyectoGradoAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorProyectoGradoAccesoQueryOutputAdapter implements AsesorProyectoGradoAccesoQueryOutputPort {

    private final AsesorProyectoGradoAccesoQueryRepository repository;

    @Override
    public Optional<UUID> obtenerAsesorVigentePorProyectoGrado(UUID proyectoGrado) {
        return repository.buscarAsesorVigentePorProyectoGrado(proyectoGrado);
    }
}
