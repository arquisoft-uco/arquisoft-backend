package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorProyectoGradoReadModel;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.secondaryport.AsesorProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.secondaryadapter.repository.mapper.AsesorProyectoGradoQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorProyectoGradoQueryOutputAdapter implements AsesorProyectoGradoQueryOutputPort {

    private final AsesorProyectoGradoQueryRepository repository;

    @Override
    public List<AsesorProyectoGradoReadModel> consultarPorProyecto(UUID proyectoGrado) {
        return repository.findByProyectoGradoIdOrderByFechaAsignadoDescIdDesc(proyectoGrado).stream()
                .map(AsesorProyectoGradoQueryMapper::toReadModel)
                .toList();
    }
}
