package com.arquisoft.usuarios.application.estudiante.command.finder.impl;

import com.arquisoft.usuarios.application.estudiante.command.finder.EstudiantePorUsuarioFinder;
import com.arquisoft.usuarios.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.usuarios.application.estudiante.command.secondaryport.mapper.EstudianteMapper;
import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudiantePorUsuarioFinderImpl implements EstudiantePorUsuarioFinder {

    private final EstudianteOutputPort estudianteOutputPort;

    @Override
    public EstudianteDomain obtener(UUID usuario) {
        return estudianteOutputPort.obtenerPorUsuario(usuario).map(EstudianteMapper::toDomain)
                .orElse(EstudianteDomain.VACIO);
    }
}
