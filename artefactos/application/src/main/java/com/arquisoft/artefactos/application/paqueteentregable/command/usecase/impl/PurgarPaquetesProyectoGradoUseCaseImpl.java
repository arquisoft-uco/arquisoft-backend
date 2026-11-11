package com.arquisoft.artefactos.application.paqueteentregable.command.usecase.impl;

import com.arquisoft.artefactos.application.paqueteentregable.command.finder.ClavesPaquetesProyectoGradoFinder;
import com.arquisoft.artefactos.application.paqueteentregable.command.result.PurgaPaquetesProyectoGradoResult;
import com.arquisoft.artefactos.application.paqueteentregable.command.result.mapper.PurgaPaquetesProyectoGradoResultMapper;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.AlmacenamientoPaqueteOutputPort;
import com.arquisoft.artefactos.application.paqueteentregable.command.usecase.PurgarPaquetesProyectoGradoUseCase;
import com.arquisoft.artefactos.domain.paqueteentregable.PurgaPaquetesProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.PaqueteEntregableKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PurgarPaquetesProyectoGradoUseCaseImpl implements PurgarPaquetesProyectoGradoUseCase {

    private final AlmacenamientoPaqueteOutputPort almacenamientoPaqueteOutputPort;
    private final ClavesPaquetesProyectoGradoFinder clavesPaquetesProyectoGradoFinder;
    private final AppLogger logger;

    @Override
    public PurgaPaquetesProyectoGradoResult ejecutar(PurgaPaquetesProyectoGradoDomain purga) {
        var claves = clavesPaquetesProyectoGradoFinder.obtener(purga);
        logger.debug(PaqueteEntregableKey.LOG_VERIFICACION_PURGAR_PROYECTO, purga.getProyectoGrado(), claves.size());

        claves.forEach(almacenamientoPaqueteOutputPort::eliminar);

        return PurgaPaquetesProyectoGradoResultMapper.toResult(purga, claves.size());
    }
}
