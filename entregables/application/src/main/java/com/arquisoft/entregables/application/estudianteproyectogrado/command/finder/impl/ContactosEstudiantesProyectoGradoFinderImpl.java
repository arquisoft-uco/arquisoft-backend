package com.arquisoft.entregables.application.estudianteproyectogrado.command.finder.impl;

import com.arquisoft.entregables.application.estudianteproyectogrado.command.finder.ContactosEstudiantesProyectoGradoFinder;
import com.arquisoft.entregables.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.entregables.application.estudianteproyectogrado.command.secondaryport.mapper.EstudianteProyectoGradoMapper;
import com.arquisoft.entregables.domain.estudianteproyectogrado.model.ContactoEstudiante;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ContactosEstudiantesProyectoGradoFinderImpl implements ContactosEstudiantesProyectoGradoFinder {

    private final EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;

    @Override
    public List<ContactoEstudiante> obtener(UUID proyectoGrado) {
        return estudianteProyectoGradoOutputPort.consultarContactosVigentesPorProyectoGrado(proyectoGrado).stream()
                .map(EstudianteProyectoGradoMapper::toContacto)
                .toList();
    }
}
