package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.biblioteca.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.biblioteca.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;
import com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.command.secondaryadapter.mapper.EntregableProyectoGradoJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EntregableProyectoGradoCommandOutputAdapter implements EntregableProyectoGradoOutputPort {

    private final EntregableProyectoGradoCommandRepository entregableProyectoGradoCommandRepository;

    @Override
    public Optional<EntregableProyectoGradoEntity> obtenerBloqueadoPorId(UUID id) {
        return entregableProyectoGradoCommandRepository.buscarBloqueadoPorId(id)
                .map(EntregableProyectoGradoJpaMapper::toEntity);
    }
}
