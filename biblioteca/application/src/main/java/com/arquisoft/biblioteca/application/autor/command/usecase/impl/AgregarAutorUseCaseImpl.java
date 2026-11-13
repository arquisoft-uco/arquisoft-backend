package com.arquisoft.biblioteca.application.autor.command.usecase.impl;

import com.arquisoft.biblioteca.application.autor.command.finder.AutorPorIdFinder;
import com.arquisoft.biblioteca.application.autor.command.result.AgregacionAutorResult;
import com.arquisoft.biblioteca.application.autor.command.result.mapper.AgregacionAutorResultMapper;
import com.arquisoft.biblioteca.application.autor.command.secondaryport.AutorOutputPort;
import com.arquisoft.biblioteca.application.autor.command.secondaryport.mapper.AutorMapper;
import com.arquisoft.biblioteca.application.autor.command.usecase.AgregarAutorUseCase;
import com.arquisoft.biblioteca.domain.autor.AutorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.AutorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarAutorUseCaseImpl implements AgregarAutorUseCase {

    private final AutorOutputPort autorOutputPort;
    private final AutorPorIdFinder autorPorIdFinder;
    private final AppLogger logger;

    @Override
    public AgregacionAutorResult ejecutar(AutorDomain autor) {
        var vigente = autorPorIdFinder.obtener(autor.getId());
        logger.debug(AutorKey.LOG_VERIFICACION_AGREGAR, autor.getId(), !vigente.esVacio());

        if (vigente.esVacio()) {
            autorOutputPort.guardar(AutorMapper.toEntity(autor));
            return AgregacionAutorResultMapper.toResultAgregada(autor);
        }

        if (!autor.getOcurridoEn().isAfter(vigente.getOcurridoEn())) {
            return AgregacionAutorResultMapper.toResultDescartada(autor, vigente.getOcurridoEn());
        }

        if (!vigente.estaEliminado()) {
            return AgregacionAutorResultMapper.toResultDuplicada(autor);
        }

        vigente.reactivar(autor.getIdentificador(), autor.getNombre(), autor.getEmail(),
                autor.getOcurridoEn());
        autorOutputPort.reactivar(AutorMapper.toEntity(vigente));
        return AgregacionAutorResultMapper.toResultReactivada(vigente);
    }
}
