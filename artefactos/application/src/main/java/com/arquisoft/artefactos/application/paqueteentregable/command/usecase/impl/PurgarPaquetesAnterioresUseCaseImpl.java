package com.arquisoft.artefactos.application.paqueteentregable.command.usecase.impl;

import com.arquisoft.artefactos.application.paqueteentregable.command.finder.ClavesPaquetesAnterioresFinder;
import com.arquisoft.artefactos.application.paqueteentregable.command.result.PurgaPaquetesAnterioresResult;
import com.arquisoft.artefactos.application.paqueteentregable.command.result.mapper.PurgaPaquetesAnterioresResultMapper;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.AlmacenamientoPaqueteOutputPort;
import com.arquisoft.artefactos.application.paqueteentregable.command.usecase.PurgarPaquetesAnterioresUseCase;
import com.arquisoft.artefactos.domain.paqueteentregable.PurgaPaquetesAnterioresDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.PaqueteEntregableKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PurgarPaquetesAnterioresUseCaseImpl implements PurgarPaquetesAnterioresUseCase {

    private final AlmacenamientoPaqueteOutputPort almacenamientoPaqueteOutputPort;
    private final ClavesPaquetesAnterioresFinder clavesPaquetesAnterioresFinder;
    private final AppLogger logger;

    @Override
    public PurgaPaquetesAnterioresResult ejecutar(PurgaPaquetesAnterioresDomain purga) {
        var claves = clavesPaquetesAnterioresFinder.obtener(purga);
        logger.debug(PaqueteEntregableKey.LOG_VERIFICACION_PURGAR_ANTERIORES, purga.getProyectoGrado(), claves.size());

        claves.forEach(almacenamientoPaqueteOutputPort::eliminar);

        return PurgaPaquetesAnterioresResultMapper.toResult(purga, claves.size());
    }
}
