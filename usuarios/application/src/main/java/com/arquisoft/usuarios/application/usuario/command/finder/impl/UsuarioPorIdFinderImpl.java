package com.arquisoft.usuarios.application.usuario.command.finder.impl;

import com.arquisoft.usuarios.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.mapper.UsuarioMapper;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UsuarioPorIdFinderImpl implements UsuarioPorIdFinder {

    private final UsuarioOutputPort usuarioOutputPort;

    @Override
    public UsuarioDomain obtener(UUID id) {
        return usuarioOutputPort.obtenerPorId(id).map(UsuarioMapper::toDomain)
                .orElse(UsuarioDomain.VACIO);
    }
}
