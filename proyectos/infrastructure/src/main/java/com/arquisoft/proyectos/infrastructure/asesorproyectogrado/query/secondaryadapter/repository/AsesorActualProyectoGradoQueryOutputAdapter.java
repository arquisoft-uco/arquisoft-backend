package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.criteria.AsesorActualProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorActualProyectoGradoReadModel;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.secondaryport.AsesorActualProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.secondaryadapter.repository.mapper.AsesorActualProyectoGradoQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AsesorActualProyectoGradoQueryOutputAdapter implements AsesorActualProyectoGradoQueryOutputPort {

    private final AsesorActualProyectoGradoQueryRepository repository;

    @Override
    public List<AsesorActualProyectoGradoReadModel> consultarPorEstudiante(
            AsesorActualProyectoGradoEstudianteCriteria criteria) {
        return repository.findByEstudianteIdOrderByTituloProyectoAscProyectoGradoIdAsc(criteria.estudiante())
                .stream()
                .map(AsesorActualProyectoGradoQueryMapper::toReadModel)
                .toList();
    }
}
