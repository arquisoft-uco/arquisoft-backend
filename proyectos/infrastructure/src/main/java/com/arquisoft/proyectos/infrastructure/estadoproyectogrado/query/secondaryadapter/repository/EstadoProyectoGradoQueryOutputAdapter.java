package com.arquisoft.proyectos.infrastructure.estadoproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.readmodel.EstadoProyectoGradoReadModel;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.secondaryport.EstadoProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.infrastructure.estadoproyectogrado.query.secondaryadapter.repository.mapper.EstadoProyectoGradoQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstadoProyectoGradoQueryOutputAdapter implements EstadoProyectoGradoQueryOutputPort {

    private final EstadoProyectoGradoQueryRepository repository;

    @Override
    public List<EstadoProyectoGradoReadModel> consultarPorProyecto(UUID proyectoGrado) {
        return repository.findByProyectoGradoIdOrderByFechaActualizacionDescIdDesc(proyectoGrado).stream()
                .map(EstadoProyectoGradoQueryMapper::toReadModel)
                .toList();
    }
}
