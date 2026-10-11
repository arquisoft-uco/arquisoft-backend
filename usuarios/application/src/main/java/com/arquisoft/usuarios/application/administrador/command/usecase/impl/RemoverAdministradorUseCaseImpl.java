package com.arquisoft.usuarios.application.administrador.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.UsuariosRealmRoles;
import com.arquisoft.shared.message.key.usuarios.RemoverAdministradorKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.application.administrador.command.finder.AdministradorPorUsuarioFinder;
import com.arquisoft.usuarios.application.administrador.command.finder.AdministradoresVigentesCountFinder;
import com.arquisoft.usuarios.application.administrador.command.secondaryport.AdministradorOutputPort;
import com.arquisoft.usuarios.application.administrador.command.usecase.RemoverAdministradorUseCase;
import com.arquisoft.usuarios.application.administrador.command.validator.RemoverAdministradorValidator;
import com.arquisoft.usuarios.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import com.arquisoft.usuarios.domain.administrador.RemocionAdministradorDomain;
import com.arquisoft.usuarios.domain.administrador.event.AdministradorRemovidoEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverAdministradorUseCaseImpl implements RemoverAdministradorUseCase {

    private final AdministradorOutputPort administradorOutputPort;
    private final ProveedorIdentidadOutputPort proveedorIdentidadOutputPort;
    private final AdministradorPorUsuarioFinder administradorPorUsuarioFinder;
    private final UsuarioPorIdFinder usuarioPorIdFinder;
    private final AdministradoresVigentesCountFinder administradoresVigentesCountFinder;
    private final RemoverAdministradorValidator removerAdministradorValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(RemocionAdministradorDomain entrada) {
        logger.info(RemoverAdministradorKey.LOG_REMOVIENDO, entrada.getUsuario());

        var administrador = administradorPorUsuarioFinder.obtener(entrada.getUsuario());
        var usuario = usuarioPorIdFinder.obtener(entrada.getUsuario());
        var administradoresVigentes = administradoresVigentesCountFinder.obtener();
        logger.debug(RemoverAdministradorKey.LOG_VERIFICACION_REMOVER, entrada.getUsuario(),
                !administrador.esVacio(), administrador.estaEliminado(), administradoresVigentes);

        removerAdministradorValidator.validar(entrada.getActor(), entrada.getUsuario(), administrador,
                administradoresVigentes);

        administrador.remover(UtilFecha.generarInstanteActual());
        administradorOutputPort.eliminarLogica(administrador.getUsuario(), administrador.getEliminadoEn());
        proveedorIdentidadOutputPort.revocarRealmRole(administrador.getUsuario(), UsuariosRealmRoles.ADMINISTRADOR);

        eventPublisher.publish(new AdministradorRemovidoEvent(
                usuario.getId(), usuario.getIdentificador(), usuario.getNombre(), usuario.getEmail()));

        logger.info(RemoverAdministradorKey.LOG_REMOVIDO, entrada.getUsuario());
    }
}
