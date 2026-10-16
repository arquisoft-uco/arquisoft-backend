package com.arquisoft.repositorio_artefactos.application.administrador.command.usecase.impl;

import com.arquisoft.repositorio_artefactos.application.administrador.command.finder.AdministradorPorIdFinder;
import com.arquisoft.repositorio_artefactos.application.administrador.command.result.RemocionAdministradorResult;
import com.arquisoft.repositorio_artefactos.application.administrador.command.result.mapper.RemocionAdministradorResultMapper;
import com.arquisoft.repositorio_artefactos.application.administrador.command.secondaryport.AdministradorOutputPort;
import com.arquisoft.repositorio_artefactos.application.administrador.command.secondaryport.mapper.AdministradorMapper;
import com.arquisoft.repositorio_artefactos.application.administrador.command.usecase.RemoverAdministradorUseCase;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.AdministradorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverAdministradorUseCaseImpl implements RemoverAdministradorUseCase {

    private final AdministradorOutputPort administradorOutputPort;
    private final AdministradorPorIdFinder administradorPorIdFinder;
    private final AppLogger logger;

    @Override
    public RemocionAdministradorResult ejecutar(AdministradorDomain entrada) {
        var vigente = administradorPorIdFinder.obtener(entrada.getId());
        logger.debug(AdministradorKey.LOG_VERIFICACION_REMOVER, entrada.getId(), !vigente.esVacio());

        if (vigente.esVacio()) {
            entrada.remover(entrada.getOcurridoEn());
            administradorOutputPort.guardar(AdministradorMapper.toEntity(entrada));
            return RemocionAdministradorResultMapper.toResultLapida(entrada);
        }

        if (!entrada.getOcurridoEn().isAfter(vigente.getOcurridoEn())) {
            return RemocionAdministradorResultMapper.toResultDescartada(entrada, vigente.getOcurridoEn());
        }

        vigente.remover(entrada.getOcurridoEn());
        administradorOutputPort.eliminarLogica(vigente.getId(), vigente.getEliminadoEn());
        return RemocionAdministradorResultMapper.toResultRemovida(vigente);
    }
}
