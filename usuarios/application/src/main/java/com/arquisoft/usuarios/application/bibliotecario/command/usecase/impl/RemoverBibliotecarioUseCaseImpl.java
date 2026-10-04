package com.arquisoft.usuarios.application.bibliotecario.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.UsuariosRealmRoles;
import com.arquisoft.shared.message.key.usuarios.RemoverBibliotecarioKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.application.bibliotecario.command.finder.BibliotecarioPorUsuarioFinder;
import com.arquisoft.usuarios.application.bibliotecario.command.secondaryport.BibliotecarioOutputPort;
import com.arquisoft.usuarios.application.bibliotecario.command.usecase.RemoverBibliotecarioUseCase;
import com.arquisoft.usuarios.application.bibliotecario.command.validator.RemoverBibliotecarioValidator;
import com.arquisoft.usuarios.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import com.arquisoft.usuarios.domain.bibliotecario.BibliotecarioDomain;
import com.arquisoft.usuarios.domain.bibliotecario.event.BibliotecarioRemovidoEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverBibliotecarioUseCaseImpl implements RemoverBibliotecarioUseCase {

    private final BibliotecarioOutputPort bibliotecarioOutputPort;
    private final ProveedorIdentidadOutputPort proveedorIdentidadOutputPort;
    private final BibliotecarioPorUsuarioFinder bibliotecarioPorUsuarioFinder;
    private final UsuarioPorIdFinder usuarioPorIdFinder;
    private final RemoverBibliotecarioValidator removerBibliotecarioValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(BibliotecarioDomain entrada) {
        logger.info(RemoverBibliotecarioKey.LOG_REMOVIENDO, entrada.getUsuario());

        var bibliotecario = bibliotecarioPorUsuarioFinder.obtener(entrada.getUsuario());
        var usuario = usuarioPorIdFinder.obtener(entrada.getUsuario());
        logger.debug(RemoverBibliotecarioKey.LOG_VERIFICACION_REMOVER,
                entrada.getUsuario(), !bibliotecario.esVacio(), bibliotecario.estaEliminado());

        removerBibliotecarioValidator.validar(entrada.getUsuario(), bibliotecario);

        bibliotecario.remover(UtilFecha.generarInstanteActual());
        bibliotecarioOutputPort.eliminarLogica(bibliotecario.getUsuario(), bibliotecario.getEliminadoEn());
        proveedorIdentidadOutputPort.revocarRealmRole(bibliotecario.getUsuario(), UsuariosRealmRoles.BIBLIOTECARIO);

        eventPublisher.publish(new BibliotecarioRemovidoEvent(
                usuario.getId(), usuario.getIdentificador(), usuario.getNombre(), usuario.getEmail()));

        logger.info(RemoverBibliotecarioKey.LOG_REMOVIDO, entrada.getUsuario());
    }
}
