package com.arquisoft.usuarios.application.coordinador.command.finder.impl;

import com.arquisoft.usuarios.application.coordinador.command.finder.CoordinadorPorUsuarioFinder;
import com.arquisoft.usuarios.application.coordinador.command.secondaryport.CoordinadorOutputPort;
import com.arquisoft.usuarios.application.coordinador.command.secondaryport.mapper.CoordinadorMapper;
import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CoordinadorPorUsuarioFinderImpl implements CoordinadorPorUsuarioFinder {

    private final CoordinadorOutputPort coordinadorOutputPort;

    @Override
    public CoordinadorDomain obtener(UUID usuario) {
        return coordinadorOutputPort.obtenerPorUsuario(usuario).map(CoordinadorMapper::toDomain)
                .orElse(CoordinadorDomain.VACIO);
    }
}
