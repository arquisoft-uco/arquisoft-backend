package com.arquisoft.entregables.application.asesorproyectogrado.command.usecase.impl;

import com.arquisoft.entregables.application.asesorproyectogrado.command.finder.AsesorProyectoGradoPorVinculoFinder;
import com.arquisoft.entregables.application.asesorproyectogrado.command.finder.model.VinculoAsesorProyectoGrado;
import com.arquisoft.entregables.application.asesorproyectogrado.command.result.AgregacionAsesorProyectoGradoResult;
import com.arquisoft.entregables.application.asesorproyectogrado.command.result.mapper.AgregacionAsesorProyectoGradoResultMapper;
import com.arquisoft.entregables.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.entregables.application.asesorproyectogrado.command.secondaryport.mapper.AsesorProyectoGradoMapper;
import com.arquisoft.entregables.application.asesorproyectogrado.command.usecase.AgregarAsesorProyectoGradoUseCase;
import com.arquisoft.entregables.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.AsesorProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarAsesorProyectoGradoUseCaseImpl implements AgregarAsesorProyectoGradoUseCase {

    private final AsesorProyectoGradoOutputPort asesorProyectoGradoOutputPort;
    private final AsesorProyectoGradoPorVinculoFinder asesorProyectoGradoPorVinculoFinder;
    private final AppLogger logger;

    @Override
    public AgregacionAsesorProyectoGradoResult ejecutar(AsesorProyectoGradoDomain entrada) {
        var existente = asesorProyectoGradoPorVinculoFinder.obtener(
                new VinculoAsesorProyectoGrado(entrada.getAsesor(), entrada.getProyectoGrado()));
        logger.debug(AsesorProyectoGradoKey.LOG_VERIFICACION_AGREGAR,
                entrada.getProyectoGrado(), entrada.getAsesor(), !existente.esVacio());

        if (existente.esVacio()) {
            asesorProyectoGradoOutputPort.guardar(AsesorProyectoGradoMapper.toEntity(entrada));
            return AgregacionAsesorProyectoGradoResultMapper.toResultAgregada(entrada);
        }
        if (!entrada.getOcurridoEn().isAfter(existente.getOcurridoEn())) {
            return AgregacionAsesorProyectoGradoResultMapper.toResultDescartada(entrada, existente.getOcurridoEn());
        }

        var estabaFinalizado = existente.estaFinalizado();
        existente.reactivar(entrada.getOcurridoEn());
        asesorProyectoGradoOutputPort.guardar(AsesorProyectoGradoMapper.toEntity(existente));
        if (estabaFinalizado) {
            return AgregacionAsesorProyectoGradoResultMapper.toResultReactivada(existente);
        }
        return AgregacionAsesorProyectoGradoResultMapper.toResultDuplicada(entrada);
    }
}
