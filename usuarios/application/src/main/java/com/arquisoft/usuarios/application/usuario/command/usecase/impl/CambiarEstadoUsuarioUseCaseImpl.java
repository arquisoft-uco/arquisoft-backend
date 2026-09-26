package com.arquisoft.usuarios.application.usuario.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.CambiarEstadoUsuarioKey;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.usuarios.application.usuario.command.usecase.CambiarEstadoUsuarioUseCase;
import com.arquisoft.usuarios.domain.usuario.CambioEstadoUsuarioDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CambiarEstadoUsuarioUseCaseImpl implements CambiarEstadoUsuarioUseCase {

    private final UsuarioOutputPort usuarioOutputPort;
    private final ProveedorIdentidadOutputPort proveedorIdentidadOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(CambioEstadoUsuarioDomain cambio) {
        usuarioOutputPort.cambiarEstado(cambio.getUsuario(), cambio.getEstado().getId());
        proveedorIdentidadOutputPort.cambiarHabilitacion(cambio.getUsuario(), cambio.getEstado().habilitaAcceso());
        logger.debug(CambiarEstadoUsuarioKey.LOG_ESTADO_CAMBIADO, cambio.getUsuario(), cambio.getEstado().getId());
    }
}
