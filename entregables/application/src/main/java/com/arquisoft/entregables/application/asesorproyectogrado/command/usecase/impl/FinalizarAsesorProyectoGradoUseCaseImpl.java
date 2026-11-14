package com.arquisoft.entregables.application.asesorproyectogrado.command.usecase.impl;

import com.arquisoft.entregables.application.asesorproyectogrado.command.finder.AsesorProyectoGradoPorVinculoFinder;
import com.arquisoft.entregables.application.asesorproyectogrado.command.finder.model.VinculoAsesorProyectoGrado;
import com.arquisoft.entregables.application.asesorproyectogrado.command.result.FinalizacionAsesorProyectoGradoResult;
import com.arquisoft.entregables.application.asesorproyectogrado.command.result.mapper.FinalizacionAsesorProyectoGradoResultMapper;
import com.arquisoft.entregables.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.entregables.application.asesorproyectogrado.command.secondaryport.mapper.AsesorProyectoGradoMapper;
import com.arquisoft.entregables.application.asesorproyectogrado.command.usecase.FinalizarAsesorProyectoGradoUseCase;
import com.arquisoft.entregables.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.AsesorProyectoGradoKey;
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
