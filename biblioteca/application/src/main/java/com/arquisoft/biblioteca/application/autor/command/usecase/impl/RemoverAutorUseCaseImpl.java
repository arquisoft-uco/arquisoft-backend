package com.arquisoft.biblioteca.application.autor.command.usecase.impl;

import com.arquisoft.biblioteca.application.autor.command.finder.AutorPorIdFinder;
import com.arquisoft.biblioteca.application.autor.command.result.RemocionAutorResult;
import com.arquisoft.biblioteca.application.autor.command.result.mapper.RemocionAutorResultMapper;
import com.arquisoft.biblioteca.application.autor.command.secondaryport.AutorOutputPort;
import com.arquisoft.biblioteca.application.autor.command.secondaryport.mapper.AutorMapper;
import com.arquisoft.biblioteca.application.autor.command.usecase.RemoverAutorUseCase;
import com.arquisoft.biblioteca.domain.autor.AutorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.AutorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverAutorUseCaseImpl implements RemoverAutorUseCase {

    private final AutorOutputPort autorOutputPort;
    private final AutorPorIdFinder autorPorIdFinder;
    private final AppLogger logger;

    @Override
    public RemocionAutorResult ejecutar(AutorDomain entrada) {
        var vigente = autorPorIdFinder.obtener(entrada.getId());
        logger.debug(AutorKey.LOG_VERIFICACION_REMOVER, entrada.getId(), !vigente.esVacio());

        if (vigente.esVacio()) {
            entrada.remover(entrada.getOcurridoEn());
            autorOutputPort.guardar(AutorMapper.toEntity(entrada));
            return RemocionAutorResultMapper.toResultLapida(entrada);
        }

        if (!entrada.getOcurridoEn().isAfter(vigente.getOcurridoEn())) {
            return RemocionAutorResultMapper.toResultDescartada(entrada, vigente.getOcurridoEn());
        }

        vigente.remover(entrada.getOcurridoEn());
        autorOutputPort.eliminarLogica(vigente.getId(), vigente.getEliminadoEn());
        return RemocionAutorResultMapper.toResultRemovida(vigente);
    }
}
