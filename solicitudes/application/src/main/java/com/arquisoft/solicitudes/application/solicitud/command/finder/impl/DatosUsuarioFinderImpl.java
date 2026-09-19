package com.arquisoft.solicitudes.application.solicitud.command.finder.impl;

import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosUsuarioFinder;
import com.arquisoft.solicitudes.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.solicitudes.application.usuario.command.secondaryport.mapper.UsuarioMapper;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DatosUsuarioFinderImpl implements DatosUsuarioFinder {

    private final UsuarioOutputPort usuarioOutputPort;

    @Override
    public Optional<UsuarioDomain> obtener(UUID usuario) {
        return usuarioOutputPort.buscarPorId(usuario).map(UsuarioMapper::toDomain);
    }
}
