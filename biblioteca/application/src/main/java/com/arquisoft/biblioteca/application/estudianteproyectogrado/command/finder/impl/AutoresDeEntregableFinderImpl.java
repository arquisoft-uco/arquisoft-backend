package com.arquisoft.biblioteca.application.estudianteproyectogrado.command.finder.impl;

import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.finder.AutoresDeEntregableFinder;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.secondaryport.mapper.EstudianteProyectoGradoMapper;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.model.AutorDeEntregable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AutoresDeEntregableFinderImpl implements AutoresDeEntregableFinder {

    private final EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;

    @Override
    public List<AutorDeEntregable> obtener(UUID entregableProyectoGrado) {
        return estudianteProyectoGradoOutputPort.consultarAutoresPorEntregable(entregableProyectoGrado).stream()
                .map(EstudianteProyectoGradoMapper::toAutorDeEntregable)
                .toList();
    }
}
