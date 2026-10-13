package com.arquisoft.repositorio_artefactos.application.administrador.command.usecase.impl;

import com.arquisoft.repositorio_artefactos.application.administrador.command.finder.AdministradorPorIdFinder;
import com.arquisoft.repositorio_artefactos.application.administrador.command.result.AgregacionAdministradorResult;
import com.arquisoft.repositorio_artefactos.application.administrador.command.result.mapper.AgregacionAdministradorResultMapper;
import com.arquisoft.repositorio_artefactos.application.administrador.command.secondaryport.AdministradorOutputPort;
import com.arquisoft.repositorio_artefactos.application.administrador.command.secondaryport.mapper.AdministradorMapper;
import com.arquisoft.repositorio_artefactos.application.administrador.command.usecase.AgregarAdministradorUseCase;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.AdministradorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarAdministradorUseCaseImpl implements AgregarAdministradorUseCase {

    private final AdministradorOutputPort administradorOutputPort;
    private final AdministradorPorIdFinder administradorPorIdFinder;
    private final AppLogger logger;

    @Override
    public AgregacionAdministradorResult ejecutar(AdministradorDomain administrador) {
        var vigente = administradorPorIdFinder.obtener(administrador.getId());
        logger.debug(AdministradorKey.LOG_VERIFICACION_AGREGAR, administrador.getId(), !vigente.esVacio());

        if (vigente.esVacio()) {
            administradorOutputPort.guardar(AdministradorMapper.toEntity(administrador));
            return AgregacionAdministradorResultMapper.toResultAgregada(administrador);
        }

        if (!administrador.getOcurridoEn().isAfter(vigente.getOcurridoEn())) {
            return AgregacionAdministradorResultMapper.toResultDescartada(administrador, vigente.getOcurridoEn());
        }

        if (!vigente.estaEliminado()) {
            return AgregacionAdministradorResultMapper.toResultDuplicada(administrador);
        }

        vigente.reactivar(administrador.getIdentificador(), administrador.getNombre(), administrador.getEmail(),
                administrador.getOcurridoEn());
        administradorOutputPort.reactivar(AdministradorMapper.toEntity(vigente));
        return AgregacionAdministradorResultMapper.toResultReactivada(vigente);
    }
}
