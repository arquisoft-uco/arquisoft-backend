package com.arquisoft.usuarios.application.usuario.query.finder.impl;

import com.arquisoft.usuarios.application.usuario.query.finder.UsuarioPorCorreoQueryFinder;
import com.arquisoft.usuarios.application.usuario.query.secondaryport.UsuarioAccesoQueryOutputPort;
import com.arquisoft.usuarios.application.usuario.query.secondaryport.mapper.UsuarioAccesoMapper;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioPorCorreoQueryFinderImpl implements UsuarioPorCorreoQueryFinder {

    private final UsuarioAccesoQueryOutputPort usuarioAccesoQueryOutputPort;

    @Override
    public UsuarioDomain obtener(String correo) {
        return usuarioAccesoQueryOutputPort.obtenerPorCorreo(correo).map(UsuarioAccesoMapper::toDomain)
                .orElse(UsuarioDomain.VACIO);
    }
}
