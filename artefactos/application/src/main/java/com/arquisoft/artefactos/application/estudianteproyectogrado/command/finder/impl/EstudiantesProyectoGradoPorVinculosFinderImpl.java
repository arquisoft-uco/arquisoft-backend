package com.arquisoft.artefactos.application.estudianteproyectogrado.command.finder.impl;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.finder.EstudiantesProyectoGradoPorVinculosFinder;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.mapper.EstudianteProyectoGradoMapper;
import com.arquisoft.artefactos.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.artefactos.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EstudiantesProyectoGradoPorVinculosFinderImpl implements EstudiantesProyectoGradoPorVinculosFinder {

    private final EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;

    @Override
    public List<EstudianteProyectoGradoDomain> obtener(AgregacionEstudiantesProyectoGradoDomain agregacion) {
        return estudianteProyectoGradoOutputPort
                .obtenerPorProyectoGradoYEstudiantes(agregacion.getProyectoGrado(), agregacion.getEstudiantes())
                .stream()
                .map(EstudianteProyectoGradoMapper::toDomain)
                .toList();
    }
}
