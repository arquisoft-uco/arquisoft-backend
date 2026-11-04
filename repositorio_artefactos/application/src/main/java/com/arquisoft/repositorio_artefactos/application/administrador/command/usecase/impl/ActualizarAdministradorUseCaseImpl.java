package com.arquisoft.repositorio_artefactos.application.administrador.command.usecase.impl;

import com.arquisoft.repositorio_artefactos.application.administrador.command.finder.AdministradorPorIdFinder;
import com.arquisoft.repositorio_artefactos.application.administrador.command.result.ActualizacionAdministradorResult;
import com.arquisoft.repositorio_artefactos.application.administrador.command.result.mapper.ActualizacionAdministradorResultMapper;
import com.arquisoft.repositorio_artefactos.application.administrador.command.secondaryport.AdministradorOutputPort;
import com.arquisoft.repositorio_artefactos.application.administrador.command.secondaryport.mapper.AdministradorMapper;
import com.arquisoft.repositorio_artefactos.application.administrador.command.usecase.ActualizarAdministradorUseCase;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.AdministradorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActualizarAdministradorUseCaseImpl implements ActualizarAdministradorUseCase {

    private final AdministradorOutputPort administradorOutputPort;
    private final AdministradorPorIdFinder administradorPorIdFinder;
    private final AppLogger logger;

    @Override
    public ActualizacionAdministradorResult ejecutar(AdministradorDomain entrada) {
        var vigente = administradorPorIdFinder.obtener(entrada.getId());
        logger.debug(AdministradorKey.LOG_VERIFICACION_ACTUALIZAR, entrada.getId(), !vigente.esVacio());

        if (vigente.esVacio()) {
            return ActualizacionAdministradorResultMapper.toResultNoReplicado(entrada);
        }

        if (!entrada.getOcurridoEn().isAfter(vigente.getOcurridoEn())) {
            return ActualizacionAdministradorResultMapper.toResultDescartada(entrada, vigente.getOcurridoEn());
        }

        vigente.actualizar(entrada.getIdentificador(), entrada.getNombre(), entrada.getEmail(),
                entrada.getOcurridoEn());
        administradorOutputPort.actualizar(AdministradorMapper.toEntity(vigente));
        return ActualizacionAdministradorResultMapper.toResultActualizada(vigente);
    }
}
