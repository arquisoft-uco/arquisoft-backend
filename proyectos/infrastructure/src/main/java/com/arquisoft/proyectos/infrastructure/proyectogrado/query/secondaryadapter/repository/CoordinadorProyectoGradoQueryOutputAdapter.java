package com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.CoordinadorProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.CoordinadorProyectoGradoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.secondaryport.CoordinadorProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository.mapper.CoordinadorProyectoGradoQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CoordinadorProyectoGradoQueryOutputAdapter implements CoordinadorProyectoGradoQueryOutputPort {

    private final CoordinadorProyectoGradoQueryRepository repository;

    @Override
    public List<CoordinadorProyectoGradoReadModel> consultarPorEstudiante(
            CoordinadorProyectoGradoEstudianteCriteria criteria) {
        return repository.findByEstudianteIdOrderByTituloProyectoAscProyectoGradoIdAsc(criteria.estudiante())
                .stream()
                .map(CoordinadorProyectoGradoQueryMapper::toReadModel)
                .toList();
    }
}
