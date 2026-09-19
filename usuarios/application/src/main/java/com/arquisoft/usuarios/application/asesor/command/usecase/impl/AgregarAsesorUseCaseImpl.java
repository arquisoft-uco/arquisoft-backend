package com.arquisoft.usuarios.application.asesor.command.usecase.impl;

import com.arquisoft.usuarios.application.asesor.command.finder.AsesorUsuarioExisteFinder;
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
    private final AsesorUsuarioExisteFinder asesorUsuarioExisteFinder;
    private final AgregarAsesorValidator agregarAsesorValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(UsuarioDomain usuario) {
        var yaEsAsesor = asesorUsuarioExisteFinder.obtener(usuario.getId());
        logger.debug(AgregarAsesorKey.LOG_VERIFICACION_AGREGAR, usuario.getId(), yaEsAsesor);

        agregarAsesorValidator.validar(usuario.getId(), yaEsAsesor);

        var asesor = AsesorDomain.crear(usuario.getId());
        asesorOutputPort.guardar(AsesorMapper.toEntity(asesor));

        eventPublisher.publish(new AsesorAgregadoEvent(
                usuario.getId(), usuario.getIdentificador(), usuario.getNombre(), usuario.getEmail()));
    }
}
