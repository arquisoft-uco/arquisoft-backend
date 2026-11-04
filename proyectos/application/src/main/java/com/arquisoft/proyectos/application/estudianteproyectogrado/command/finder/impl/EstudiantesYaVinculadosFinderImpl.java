package com.arquisoft.proyectos.application.estudianteproyectogrado.command.finder.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.finder.EstudiantesYaVinculadosFinder;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudiantesYaVinculadosFinderImpl implements EstudiantesYaVinculadosFinder {

    private final EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;

    @Override
    public List<UUID> obtener(AgregacionEstudiantesProyectoGradoDomain entrada) {
        return estudianteProyectoGradoOutputPort.obtenerVinculados(entrada.getProyectoGrado(), entrada.getEstudiantes());
    }
}
