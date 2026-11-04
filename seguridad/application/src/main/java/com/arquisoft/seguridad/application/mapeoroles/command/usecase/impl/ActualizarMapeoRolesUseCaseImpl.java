package com.arquisoft.seguridad.application.mapeoroles.command.usecase.impl;

import com.arquisoft.seguridad.application.mapeoroles.command.finder.MapeoRolesVigenteFinder;
import com.arquisoft.seguridad.application.mapeoroles.command.result.ActualizacionMapeoRolesResult;
import com.arquisoft.seguridad.application.mapeoroles.command.result.mapper.ActualizacionMapeoRolesResultMapper;
import com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.FuenteMapeoRolesOutputPort;
import com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.MapeoRolesOutputPort;
import com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.model.ObtencionMapeoRoles;
import com.arquisoft.seguridad.application.mapeoroles.command.usecase.ActualizarMapeoRolesUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.MapeoRolesKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActualizarMapeoRolesUseCaseImpl implements ActualizarMapeoRolesUseCase {

    private final MapeoRolesVigenteFinder mapeoRolesVigenteFinder;
    private final MapeoRolesOutputPort mapeoRolesOutputPort;
    private final FuenteMapeoRolesOutputPort fuenteMapeoRolesOutputPort;
    private final AppLogger logger;

    @Override
    public ActualizacionMapeoRolesResult ejecutar() {
        var vigente = mapeoRolesVigenteFinder.obtener();
        logger.debug(MapeoRolesKey.LOG_VERIFICACION_VIGENCIA, vigente);

        if (vigente) {
            return ActualizacionMapeoRolesResultMapper.toResultVigente();
        }
        if (!mapeoRolesOutputPort.reservarActualizacion()) {
            logger.debug(MapeoRolesKey.LOG_ACTUALIZACION_OMITIDA);
            return ActualizacionMapeoRolesResultMapper.toResultOmitida();
        }

        logger.info(MapeoRolesKey.LOG_ACTUALIZANDO);
        var obtencion = fuenteMapeoRolesOutputPort.obtener();

        return switch (obtencion) {
            case ObtencionMapeoRoles.Obtenida obtenida -> actualizar(obtenida);
            case ObtencionMapeoRoles.NoDisponible noDisponible -> conservar(noDisponible);
        };
    }

    private ActualizacionMapeoRolesResult actualizar(ObtencionMapeoRoles.Obtenida obtenida) {
        mapeoRolesOutputPort.guardar(obtenida.mapeo());

        var resultado = ActualizacionMapeoRolesResultMapper.toResultActualizada(obtenida.mapeo());
        logger.info(MapeoRolesKey.LOG_ACTUALIZADA, resultado.rolesRealm(), resultado.rolesCliente());
        return resultado;
    }

    private ActualizacionMapeoRolesResult conservar(ObtencionMapeoRoles.NoDisponible noDisponible) {
        logger.warn(MapeoRolesKey.LOG_MAPEO_CONSERVADO, noDisponible.motivo());
        return ActualizacionMapeoRolesResultMapper.toResultFallida(noDisponible.motivo());
    }
}
