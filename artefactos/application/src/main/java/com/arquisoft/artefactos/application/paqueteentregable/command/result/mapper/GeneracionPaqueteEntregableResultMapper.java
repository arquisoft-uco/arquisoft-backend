package com.arquisoft.artefactos.application.paqueteentregable.command.result.mapper;

import com.arquisoft.artefactos.application.paqueteentregable.command.result.GeneracionPaqueteEntregableResult;
import com.arquisoft.artefactos.domain.paqueteentregable.GeneracionPaqueteEntregableDomain;
import com.arquisoft.artefactos.domain.paqueteentregable.PaqueteEntregableDomain;

public final class GeneracionPaqueteEntregableResultMapper {

    private GeneracionPaqueteEntregableResultMapper() {}

    public static GeneracionPaqueteEntregableResult.Generado toResultGenerado(PaqueteEntregableDomain paquete) {
        return new GeneracionPaqueteEntregableResult.Generado(
                paquete.getId(), paquete.getEntregableProyectoGrado(), paquete.getTamanoBytes());
    }

    public static GeneracionPaqueteEntregableResult.Duplicado toResultDuplicado(
            GeneracionPaqueteEntregableDomain generacion) {
        return new GeneracionPaqueteEntregableResult.Duplicado(generacion.getEntregableProyectoGrado());
    }
}
