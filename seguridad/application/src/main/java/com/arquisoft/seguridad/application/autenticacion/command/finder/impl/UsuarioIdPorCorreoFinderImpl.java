package com.arquisoft.seguridad.application.autenticacion.command.finder.impl;

import com.arquisoft.seguridad.application.autenticacion.command.finder.UsuarioIdPorCorreoFinder;
import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.UsuarioPorCorreoOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UsuarioIdPorCorreoFinderImpl implements UsuarioIdPorCorreoFinder {

    private final UsuarioPorCorreoOutputPort usuarioPorCorreoOutputPort;

    @Override
    public UUID obtener(String correo) {
        return usuarioPorCorreoOutputPort.obtenerIdPorCorreo(correo).orElse(UtilUUID.obtenerUUIDPorDefecto());
    }
}
