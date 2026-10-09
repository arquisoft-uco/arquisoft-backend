package com.arquisoft.usuarios.application.usuario.query.finder.impl;

import com.arquisoft.usuarios.application.usuario.query.finder.UsuarioPorIdQueryFinder;
import com.arquisoft.usuarios.application.usuario.query.secondaryport.UsuarioAccesoQueryOutputPort;
import com.arquisoft.usuarios.application.usuario.query.secondaryport.mapper.UsuarioAccesoMapper;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UsuarioPorIdQueryFinderImpl implements UsuarioPorIdQueryFinder {

    private final UsuarioAccesoQueryOutputPort usuarioAccesoQueryOutputPort;

    @Override
    public UsuarioDomain obtener(UUID usuario) {
        return usuarioAccesoQueryOutputPort.obtenerPorId(usuario).map(UsuarioAccesoMapper::toDomain)
                .orElse(UsuarioDomain.VACIO);
    }
}
