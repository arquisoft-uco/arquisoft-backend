package com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.finder.impl;

import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.finder.ContactosEstudiantesEvaluacionFinder;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.secondaryport.mapper.EstudianteProyectoGradoMapper;
import com.arquisoft.evaluaciones.domain.estudianteproyectogrado.model.ContactoEstudiante;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ContactosEstudiantesEvaluacionFinderImpl implements ContactosEstudiantesEvaluacionFinder {

    private final EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;

    @Override
    public List<ContactoEstudiante> obtener(UUID evaluacion) {
        return estudianteProyectoGradoOutputPort.consultarContactosVigentesPorEvaluacion(evaluacion).stream()
                .map(EstudianteProyectoGradoMapper::toDomain)
                .map(vinculo -> new ContactoEstudiante(
                        vinculo.getEstudiante(), vinculo.getNombre(), vinculo.getEmail()))
                .toList();
    }
}
