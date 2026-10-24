package com.arquisoft.artefactos.application.asesorproyectogrado.command.usecase.impl;

import com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.AsesorProyectoGradoPorVinculoFinder;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.model.VinculoAsesorProyectoGrado;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.result.FinalizacionAsesorProyectoGradoResult;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.result.mapper.FinalizacionAsesorProyectoGradoResultMapper;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.mapper.AsesorProyectoGradoMapper;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.usecase.FinalizarAsesorProyectoGradoUseCase;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.AsesorProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FinalizarAsesorProyectoGradoUseCaseImpl implements FinalizarAsesorProyectoGradoUseCase {

    private final AsesorProyectoGradoOutputPort asesorProyectoGradoOutputPort;
    private final AsesorProyectoGradoPorVinculoFinder asesorProyectoGradoPorVinculoFinder;
    private final AppLogger logger;

    @Override
    public FinalizacionAsesorProyectoGradoResult ejecutar(AsesorProyectoGradoDomain entrada) {
        var existente = asesorProyectoGradoPorVinculoFinder.obtener(
                new VinculoAsesorProyectoGrado(entrada.getAsesor(), entrada.getProyectoGrado()));
        logger.debug(AsesorProyectoGradoKey.LOG_VERIFICACION_FINALIZAR,
                entrada.getProyectoGrado(), entrada.getAsesor(), !existente.esVacio());

        if (existente.esVacio()) {
            entrada.finalizar(entrada.getOcurridoEn());
            asesorProyectoGradoOutputPort.guardar(AsesorProyectoGradoMapper.toEntity(entrada));
            return FinalizacionAsesorProyectoGradoResultMapper.toResultLapida(entrada);
        }
        if (!entrada.getOcurridoEn().isAfter(existente.getOcurridoEn())) {
            return FinalizacionAsesorProyectoGradoResultMapper.toResultDescartada(
                    entrada, existente.getOcurridoEn());
        }

        existente.finalizar(entrada.getOcurridoEn());
        asesorProyectoGradoOutputPort.guardar(AsesorProyectoGradoMapper.toEntity(existente));
        return FinalizacionAsesorProyectoGradoResultMapper.toResultFinalizada(existente);
    }
}
