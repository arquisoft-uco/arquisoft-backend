package com.arquisoft.biblioteca.application.bibliotecario.command.usecase.impl;

import com.arquisoft.biblioteca.application.bibliotecario.command.finder.BibliotecarioPorIdFinder;
import com.arquisoft.biblioteca.application.bibliotecario.command.result.RemocionBibliotecarioResult;
import com.arquisoft.biblioteca.application.bibliotecario.command.result.mapper.RemocionBibliotecarioResultMapper;
import com.arquisoft.biblioteca.application.bibliotecario.command.secondaryport.BibliotecarioOutputPort;
import com.arquisoft.biblioteca.application.bibliotecario.command.secondaryport.mapper.BibliotecarioMapper;
import com.arquisoft.biblioteca.application.bibliotecario.command.usecase.RemoverBibliotecarioUseCase;
import com.arquisoft.biblioteca.domain.bibliotecario.BibliotecarioDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.BibliotecarioKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverBibliotecarioUseCaseImpl implements RemoverBibliotecarioUseCase {

    private final BibliotecarioOutputPort bibliotecarioOutputPort;
    private final BibliotecarioPorIdFinder bibliotecarioPorIdFinder;
    private final AppLogger logger;

    @Override
    public RemocionBibliotecarioResult ejecutar(BibliotecarioDomain entrada) {
        var vigente = bibliotecarioPorIdFinder.obtener(entrada.getId());
        logger.debug(BibliotecarioKey.LOG_VERIFICACION_REMOVER, entrada.getId(), !vigente.esVacio());

        if (vigente.esVacio()) {
            entrada.remover(entrada.getOcurridoEn());
            bibliotecarioOutputPort.guardar(BibliotecarioMapper.toEntity(entrada));
            return RemocionBibliotecarioResultMapper.toResultLapida(entrada);
        }

        if (!entrada.getOcurridoEn().isAfter(vigente.getOcurridoEn())) {
            return RemocionBibliotecarioResultMapper.toResultDescartada(entrada, vigente.getOcurridoEn());
        }

        vigente.remover(entrada.getOcurridoEn());
        bibliotecarioOutputPort.eliminarLogica(vigente.getId(), vigente.getEliminadoEn());
        return RemocionBibliotecarioResultMapper.toResultRemovida(vigente);
    }
}
