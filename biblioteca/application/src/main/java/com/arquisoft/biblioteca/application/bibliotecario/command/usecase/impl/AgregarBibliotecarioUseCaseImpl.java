package com.arquisoft.biblioteca.application.bibliotecario.command.usecase.impl;

import com.arquisoft.biblioteca.application.bibliotecario.command.finder.BibliotecarioPorIdFinder;
import com.arquisoft.biblioteca.application.bibliotecario.command.result.AgregacionBibliotecarioResult;
import com.arquisoft.biblioteca.application.bibliotecario.command.result.mapper.AgregacionBibliotecarioResultMapper;
import com.arquisoft.biblioteca.application.bibliotecario.command.secondaryport.BibliotecarioOutputPort;
import com.arquisoft.biblioteca.application.bibliotecario.command.secondaryport.mapper.BibliotecarioMapper;
import com.arquisoft.biblioteca.application.bibliotecario.command.usecase.AgregarBibliotecarioUseCase;
import com.arquisoft.biblioteca.domain.bibliotecario.BibliotecarioDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.BibliotecarioKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarBibliotecarioUseCaseImpl implements AgregarBibliotecarioUseCase {

    private final BibliotecarioOutputPort bibliotecarioOutputPort;
    private final BibliotecarioPorIdFinder bibliotecarioPorIdFinder;
    private final AppLogger logger;

    @Override
    public AgregacionBibliotecarioResult ejecutar(BibliotecarioDomain bibliotecario) {
        var vigente = bibliotecarioPorIdFinder.obtener(bibliotecario.getId());
        logger.debug(BibliotecarioKey.LOG_VERIFICACION_AGREGAR, bibliotecario.getId(), !vigente.esVacio());

        if (vigente.esVacio()) {
            bibliotecarioOutputPort.guardar(BibliotecarioMapper.toEntity(bibliotecario));
            return AgregacionBibliotecarioResultMapper.toResultAgregada(bibliotecario);
        }

        if (!bibliotecario.getOcurridoEn().isAfter(vigente.getOcurridoEn())) {
            return AgregacionBibliotecarioResultMapper.toResultDescartada(bibliotecario, vigente.getOcurridoEn());
        }

        if (!vigente.estaEliminado()) {
            return AgregacionBibliotecarioResultMapper.toResultDuplicada(bibliotecario);
        }

        vigente.reactivar(bibliotecario.getIdentificador(), bibliotecario.getNombre(), bibliotecario.getEmail(),
                bibliotecario.getOcurridoEn());
        bibliotecarioOutputPort.reactivar(BibliotecarioMapper.toEntity(vigente));
        return AgregacionBibliotecarioResultMapper.toResultReactivada(vigente);
    }
}
