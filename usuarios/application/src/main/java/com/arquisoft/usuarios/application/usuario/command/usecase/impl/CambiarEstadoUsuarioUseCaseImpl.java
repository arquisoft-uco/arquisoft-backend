package com.arquisoft.usuarios.application.usuario.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.CambiarEstadoUsuarioKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.usuarios.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.usuarios.application.usuario.command.usecase.CambiarEstadoUsuarioUseCase;
import com.arquisoft.usuarios.application.usuario.command.validator.CambiarEstadoUsuarioValidator;
import com.arquisoft.usuarios.domain.usuario.CambioEstadoUsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.event.UsuarioEstadoCambiadoEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CambiarEstadoUsuarioUseCaseImpl implements CambiarEstadoUsuarioUseCase {

    private final UsuarioOutputPort usuarioOutputPort;
    private final ProveedorIdentidadOutputPort proveedorIdentidadOutputPort;
    private final UsuarioPorIdFinder usuarioPorIdFinder;
    private final CambiarEstadoUsuarioValidator cambiarEstadoUsuarioValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(CambioEstadoUsuarioDomain cambio) {
        logger.info(CambiarEstadoUsuarioKey.LOG_CAMBIANDO_ESTADO, cambio.getUsuario(), cambio.getEstado().getId());

        var usuario = usuarioPorIdFinder.obtener(cambio.getUsuario());

        logger.debug(CambiarEstadoUsuarioKey.LOG_VERIFICACION_CAMBIAR_ESTADO, cambio.getUsuario(),
                !usuario.esVacio(), usuario.getEstado().getId(), usuario.estaEliminado());

        cambiarEstadoUsuarioValidator.validar(cambio, usuario);

        var estabaEliminado = usuario.estaEliminado();
        usuario.cambiarEstado(cambio.getEstado());
        usuarioOutputPort.cambiarEstado(usuario.getId(), usuario.getEstado().getId(), usuario.getEliminadoEn());
        proveedorIdentidadOutputPort.cambiarHabilitacion(usuario.getId(), usuario.getEstado().habilitaAcceso());
        eventPublisher.publish(new UsuarioEstadoCambiadoEvent(usuario.getId(), usuario.getNombre(),
                usuario.getEmail(), usuario.getEstado().getId(), usuario.getEstado().getNombre()));

        logger.info(CambiarEstadoUsuarioKey.LOG_ESTADO_CAMBIADO, usuario.getId(), usuario.getEstado().getId(),
                estabaEliminado && !usuario.estaEliminado());
    }
}
