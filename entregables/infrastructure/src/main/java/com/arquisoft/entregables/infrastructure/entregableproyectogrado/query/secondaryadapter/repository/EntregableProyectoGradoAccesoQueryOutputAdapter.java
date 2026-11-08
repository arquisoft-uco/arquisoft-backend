package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.entregables.application.entregableproyectogrado.query.secondaryport.EntregableProyectoGradoAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EntregableProyectoGradoAccesoQueryOutputAdapter implements EntregableProyectoGradoAccesoQueryOutputPort {

    private final EntregableProyectoGradoAccesoQueryRepository repository;
    private final EstudianteEntregableAccesoQueryRepository estudianteEntregableAccesoQueryRepository;

    @Override
    public Optional<UUID> obtenerAsesorPorId(UUID entregableProyectoGrado) {
        return repository.findById(entregableProyectoGrado)
                .map(EntregableProyectoGradoAccesoJpaQueryEntity::getAsesorId);
    }

    @Override
    public boolean estudianteParticipa(UUID entregableProyectoGrado, UUID estudiante) {
        return estudianteEntregableAccesoQueryRepository.existsByEntregableProyectoGradoIdAndEstudianteId(
                entregableProyectoGrado, estudiante);
    }
}
