package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.EstudianteProyectoGradoReadModel;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.secondaryport.EstudianteProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.secondaryadapter.repository.mapper.EstudianteProyectoGradoQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteProyectoGradoQueryOutputAdapter implements EstudianteProyectoGradoQueryOutputPort {

    private final EstudianteProyectoGradoQueryRepository repository;

    @Override
    public List<EstudianteProyectoGradoReadModel> consultarPorProyecto(UUID proyectoGrado) {
        return repository.findByProyectoGradoIdOrderByNombreAscIdAsc(proyectoGrado).stream()
                .map(EstudianteProyectoGradoQueryMapper::toReadModel)
                .toList();
    }
}
