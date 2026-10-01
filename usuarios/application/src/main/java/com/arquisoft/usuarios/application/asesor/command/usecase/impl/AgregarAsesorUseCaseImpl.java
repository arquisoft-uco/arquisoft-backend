package com.arquisoft.usuarios.application.asesor.command.usecase.impl;

import com.arquisoft.usuarios.application.asesor.command.finder.AsesorPorUsuarioFinder;
import com.arquisoft.usuarios.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.usuarios.application.asesor.command.secondaryport.mapper.AsesorMapper;
import com.arquisoft.usuarios.application.asesor.command.usecase.AgregarAsesorUseCase;
import com.arquisoft.usuarios.application.asesor.command.validator.AgregarAsesorValidator;
import com.arquisoft.usuarios.domain.asesor.AsesorDomain;
import com.arquisoft.usuarios.domain.asesor.event.AsesorAgregadoEvent;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.AgregarAsesorKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarAsesorUseCaseImpl implements AgregarAsesorUseCase {

    private final AsesorOutputPort asesorOutputPort;
    private final AsesorPorUsuarioFinder asesorPorUsuarioFinder;
    private final AgregarAsesorValidator agregarAsesorValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(UsuarioDomain usuario) {
        var asesor = asesorPorUsuarioFinder.obtener(usuario.getId());
        logger.debug(AgregarAsesorKey.LOG_VERIFICACION_AGREGAR,
                usuario.getId(), !asesor.esVacio(), asesor.estaEliminado());

        agregarAsesorValidator.validar(usuario.getId(), asesor);

        if (asesor.esVacio()) {
            asesorOutputPort.guardar(AsesorMapper.toEntity(AsesorDomain.crear(usuario.getId())));
        } else {
            reactivar(asesor);
        }

        eventPublisher.publish(new AsesorAgregadoEvent(
                usuario.getId(), usuario.getIdentificador(), usuario.getNombre(), usuario.getEmail()));
    }

    private void reactivar(AsesorDomain asesor) {
        asesor.reactivar();
        asesorOutputPort.reactivar(asesor.getUsuario());
        logger.info(AgregarAsesorKey.LOG_REACTIVADO, asesor.getUsuario());
    }
}
