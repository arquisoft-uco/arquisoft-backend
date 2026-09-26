package com.arquisoft.usuarios.application.asesorficha.command.usecase.impl;

import com.arquisoft.usuarios.application.asesorficha.command.finder.AsesorFichaPorUsuarioFinder;
import com.arquisoft.usuarios.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.usuarios.application.asesorficha.command.secondaryport.mapper.AsesorFichaMapper;
import com.arquisoft.usuarios.application.asesorficha.command.usecase.AgregarAsesorFichaUseCase;
import com.arquisoft.usuarios.application.asesorficha.command.validator.AgregarAsesorFichaValidator;
import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.usuarios.domain.asesorficha.event.AsesorFichaAgregadoEvent;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.AgregarAsesorFichaKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarAsesorFichaUseCaseImpl implements AgregarAsesorFichaUseCase {

    private final AsesorFichaOutputPort asesorFichaOutputPort;
    private final AsesorFichaPorUsuarioFinder asesorFichaPorUsuarioFinder;
    private final AgregarAsesorFichaValidator agregarAsesorFichaValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(UsuarioDomain usuario) {
        var asesorFicha = asesorFichaPorUsuarioFinder.obtener(usuario.getId());
        logger.debug(AgregarAsesorFichaKey.LOG_VERIFICACION_AGREGAR,
                usuario.getId(), !asesorFicha.esVacio(), asesorFicha.estaEliminado());

        agregarAsesorFichaValidator.validar(usuario.getId(), asesorFicha);

        if (asesorFicha.esVacio()) {
            asesorFichaOutputPort.guardar(AsesorFichaMapper.toEntity(AsesorFichaDomain.crear(usuario.getId())));
        } else {
            reactivar(asesorFicha);
        }

        eventPublisher.publish(new AsesorFichaAgregadoEvent(
                usuario.getId(), usuario.getIdentificador(), usuario.getNombre(), usuario.getEmail()));
    }

    private void reactivar(AsesorFichaDomain asesorFicha) {
        asesorFicha.reactivar();
        asesorFichaOutputPort.reactivar(asesorFicha.getUsuario());
        logger.info(AgregarAsesorFichaKey.LOG_REACTIVADO, asesorFicha.getUsuario());
    }
}
