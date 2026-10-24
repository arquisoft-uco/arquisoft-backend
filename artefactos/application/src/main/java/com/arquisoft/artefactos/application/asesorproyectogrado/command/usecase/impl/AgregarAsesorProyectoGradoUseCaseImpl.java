package com.arquisoft.artefactos.application.asesorproyectogrado.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.usecase.TraspasarArtefactosUseCase;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.AsesorProyectoGradoPorVinculoFinder;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.UltimoOcurridoEnProyectoGradoFinder;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.model.VinculoAsesorProyectoGrado;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.result.AgregacionAsesorProyectoGradoResult;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.result.mapper.AgregacionAsesorProyectoGradoResultMapper;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.mapper.AsesorProyectoGradoMapper;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.usecase.AgregarAsesorProyectoGradoUseCase;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AgregacionAsesorProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.AsesorProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class AgregarAsesorProyectoGradoUseCaseImpl implements AgregarAsesorProyectoGradoUseCase {

    private final AsesorProyectoGradoOutputPort asesorProyectoGradoOutputPort;
    private final AsesorProyectoGradoPorVinculoFinder asesorProyectoGradoPorVinculoFinder;
    private final UltimoOcurridoEnProyectoGradoFinder ultimoOcurridoEnProyectoGradoFinder;
    private final TraspasarArtefactosUseCase traspasarArtefactosUseCase;
    private final AppLogger logger;

    @Override
    public AgregacionAsesorProyectoGradoResult ejecutar(AgregacionAsesorProyectoGradoDomain agregacion) {
        var entrada = agregacion.getVinculo();
        var existente = asesorProyectoGradoPorVinculoFinder.obtener(
                new VinculoAsesorProyectoGrado(entrada.getAsesor(), entrada.getProyectoGrado()));
        var ultimoOcurridoEn = ultimoOcurridoEnProyectoGradoFinder.obtener(entrada.getProyectoGrado());
        logger.debug(AsesorProyectoGradoKey.LOG_VERIFICACION_AGREGAR,
                entrada.getProyectoGrado(), entrada.getAsesor(), !existente.esVacio(), ultimoOcurridoEn);

        if (existente.esVacio()) {
            asesorProyectoGradoOutputPort.guardar(AsesorProyectoGradoMapper.toEntity(entrada));
            traspasarSiEsElMasReciente(agregacion, ultimoOcurridoEn);
            return AgregacionAsesorProyectoGradoResultMapper.toResultAgregada(entrada);
        }
        if (!entrada.getOcurridoEn().isAfter(existente.getOcurridoEn())) {
            return AgregacionAsesorProyectoGradoResultMapper.toResultDescartada(entrada, existente.getOcurridoEn());
        }
        if (!existente.estaFinalizado()) {
            return AgregacionAsesorProyectoGradoResultMapper.toResultDuplicada(entrada);
        }

        existente.reactivar(entrada.getOcurridoEn());
        asesorProyectoGradoOutputPort.guardar(AsesorProyectoGradoMapper.toEntity(existente));
        traspasarSiEsElMasReciente(agregacion, ultimoOcurridoEn);
        return AgregacionAsesorProyectoGradoResultMapper.toResultReactivada(existente);
    }

    private void traspasarSiEsElMasReciente(AgregacionAsesorProyectoGradoDomain agregacion, Instant ultimoOcurridoEn) {
        if (agregacion.esVinculoMasReciente(ultimoOcurridoEn)) {
            traspasarArtefactosUseCase.ejecutar(agregacion.getTraspaso());
        }
    }
}
