package com.arquisoft.usuarios.application.administrador.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.AgregarAdministradorKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.usuarios.application.administrador.command.finder.AdministradorPorUsuarioFinder;
import com.arquisoft.usuarios.application.administrador.command.secondaryport.AdministradorOutputPort;
import com.arquisoft.usuarios.application.administrador.command.secondaryport.mapper.AdministradorMapper;
import com.arquisoft.usuarios.application.administrador.command.usecase.AgregarAdministradorUseCase;
import com.arquisoft.usuarios.application.administrador.command.validator.AgregarAdministradorValidator;
import com.arquisoft.usuarios.domain.administrador.AdministradorDomain;
import com.arquisoft.usuarios.domain.administrador.event.AdministradorAgregadoEvent;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarAdministradorUseCaseImpl implements AgregarAdministradorUseCase {

    private final AdministradorOutputPort administradorOutputPort;
    private final AdministradorPorUsuarioFinder administradorPorUsuarioFinder;
    private final AgregarAdministradorValidator agregarAdministradorValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(UsuarioDomain usuario) {
        var administrador = administradorPorUsuarioFinder.obtener(usuario.getId());
        logger.debug(AgregarAdministradorKey.LOG_VERIFICACION_AGREGAR,
                usuario.getId(), !administrador.esVacio(), administrador.estaEliminado());

        agregarAdministradorValidator.validar(usuario.getId(), administrador);

        if (administrador.esVacio()) {
            administradorOutputPort.guardar(
                    AdministradorMapper.toEntity(AdministradorDomain.crear(usuario.getId())));
        } else {
            reactivar(administrador);
        }

        eventPublisher.publish(new AdministradorAgregadoEvent(
                usuario.getId(), usuario.getIdentificador(), usuario.getNombre(), usuario.getEmail()));
    }

    private void reactivar(AdministradorDomain administrador) {
        administrador.reactivar();
        administradorOutputPort.reactivar(administrador.getUsuario());
        logger.info(AgregarAdministradorKey.LOG_REACTIVADO, administrador.getUsuario());
    }
}
