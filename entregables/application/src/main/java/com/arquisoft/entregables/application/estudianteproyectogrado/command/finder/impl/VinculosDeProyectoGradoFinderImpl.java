package com.arquisoft.entregables.application.estudianteproyectogrado.command.finder.impl;

import com.arquisoft.entregables.application.estudianteproyectogrado.command.finder.VinculosDeProyectoGradoFinder;
import com.arquisoft.entregables.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.entregables.application.estudianteproyectogrado.command.secondaryport.mapper.EstudianteProyectoGradoMapper;
import com.arquisoft.entregables.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VinculosDeProyectoGradoFinderImpl implements VinculosDeProyectoGradoFinder {

    private final EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;

    @Override
    public List<EstudianteProyectoGradoDomain> obtener(UUID proyectoGrado) {
        return estudianteProyectoGradoOutputPort.consultarPorProyectoGrado(proyectoGrado).stream()
                .map(EstudianteProyectoGradoMapper::toDomain)
                .toList();
    }
}
