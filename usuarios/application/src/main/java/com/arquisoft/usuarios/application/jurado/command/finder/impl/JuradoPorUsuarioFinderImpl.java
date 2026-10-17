package com.arquisoft.usuarios.application.jurado.command.finder.impl;

import com.arquisoft.usuarios.application.jurado.command.finder.JuradoPorUsuarioFinder;
import com.arquisoft.usuarios.application.jurado.command.secondaryport.JuradoOutputPort;
import com.arquisoft.usuarios.application.jurado.command.secondaryport.mapper.JuradoMapper;
import com.arquisoft.usuarios.domain.jurado.JuradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JuradoPorUsuarioFinderImpl implements JuradoPorUsuarioFinder {

    private final JuradoOutputPort juradoOutputPort;

    @Override
    public JuradoDomain obtener(UUID usuario) {
        return juradoOutputPort.obtenerPorUsuario(usuario).map(JuradoMapper::toDomain)
                .orElse(JuradoDomain.VACIO);
    }
}
