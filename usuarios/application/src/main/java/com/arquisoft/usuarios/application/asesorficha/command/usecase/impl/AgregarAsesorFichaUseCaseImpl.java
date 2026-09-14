package com.arquisoft.usuarios.application.asesorficha.command.usecase.impl;

import com.arquisoft.usuarios.application.asesorficha.command.finder.AsesorFichaUsuarioExisteFinder;
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
    private final AsesorFichaUsuarioExisteFinder asesorFichaUsuarioExisteFinder;
    private final AgregarAsesorFichaValidator agregarAsesorFichaValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(UsuarioDomain usuario) {
        var yaEsAsesorFicha = asesorFichaUsuarioExisteFinder.obtener(usuario.getId());
        logger.debug(AgregarAsesorFichaKey.LOG_VERIFICACION_AGREGAR, usuario.getId(), yaEsAsesorFicha);

        agregarAsesorFichaValidator.validar(usuario.getId(), yaEsAsesorFicha);

        var asesorFicha = AsesorFichaDomain.crear(usuario.getId());
        asesorFichaOutputPort.guardar(AsesorFichaMapper.toEntity(asesorFicha));

        eventPublisher.publish(new AsesorFichaAgregadoEvent(
                usuario.getId(), usuario.getIdentificador(), usuario.getNombre(), usuario.getEmail()));
    }
}
