package com.arquisoft.mapas_ruta.infrastructure.proyectogrado.command.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.mapas_ruta.infrastructure.proyectogrado.command.secondaryadapter.mapper.ProyectoGradoJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProyectoGradoCommandOutputAdapter implements ProyectoGradoOutputPort {

    private final ProyectoGradoCommandRepository repository;

    @Override
    public Optional<ProyectoGradoEntity> obtenerPorId(UUID proyectoGrado) {
        return repository.findById(proyectoGrado).map(ProyectoGradoJpaMapper::toEntity);
    }
}
