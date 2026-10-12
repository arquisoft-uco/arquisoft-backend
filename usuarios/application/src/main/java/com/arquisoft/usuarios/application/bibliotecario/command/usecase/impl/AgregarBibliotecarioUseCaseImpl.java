package com.arquisoft.usuarios.application.bibliotecario.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.AgregarBibliotecarioKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.usuarios.application.bibliotecario.command.finder.BibliotecarioPorUsuarioFinder;
import com.arquisoft.usuarios.application.bibliotecario.command.secondaryport.BibliotecarioOutputPort;
import com.arquisoft.usuarios.application.bibliotecario.command.secondaryport.mapper.BibliotecarioMapper;
import com.arquisoft.usuarios.application.bibliotecario.command.usecase.AgregarBibliotecarioUseCase;
import com.arquisoft.usuarios.application.bibliotecario.command.validator.AgregarBibliotecarioValidator;
import com.arquisoft.usuarios.domain.bibliotecario.BibliotecarioDomain;
import com.arquisoft.usuarios.domain.bibliotecario.event.BibliotecarioAgregadoEvent;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarBibliotecarioUseCaseImpl implements AgregarBibliotecarioUseCase {

    private final BibliotecarioOutputPort bibliotecarioOutputPort;
    private final BibliotecarioPorUsuarioFinder bibliotecarioPorUsuarioFinder;
    private final AgregarBibliotecarioValidator agregarBibliotecarioValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(UsuarioDomain usuario) {
        var bibliotecario = bibliotecarioPorUsuarioFinder.obtener(usuario.getId());
        logger.debug(AgregarBibliotecarioKey.LOG_VERIFICACION_AGREGAR,
                usuario.getId(), !bibliotecario.esVacio(), bibliotecario.estaEliminado());

        agregarBibliotecarioValidator.validar(usuario.getId(), bibliotecario);

        if (bibliotecario.esVacio()) {
            bibliotecarioOutputPort.guardar(
                    BibliotecarioMapper.toEntity(BibliotecarioDomain.crear(usuario.getId())));
        } else {
            reactivar(bibliotecario);
        }

        eventPublisher.publish(new BibliotecarioAgregadoEvent(
                usuario.getId(), usuario.getIdentificador(), usuario.getNombre(), usuario.getEmail()));
    }

    private void reactivar(BibliotecarioDomain bibliotecario) {
        bibliotecario.reactivar();
        bibliotecarioOutputPort.reactivar(bibliotecario.getUsuario());
        logger.info(AgregarBibliotecarioKey.LOG_REACTIVADO, bibliotecario.getUsuario());
    }
}
