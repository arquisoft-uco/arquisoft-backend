package com.arquisoft.entregables.application.paqueteentregable.command.result.mapper;

import com.arquisoft.entregables.application.paqueteentregable.command.result.AgregacionPaqueteEntregableResult;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;

public final class AgregacionPaqueteEntregableResultMapper {

    private AgregacionPaqueteEntregableResultMapper() {}

    public static AgregacionPaqueteEntregableResult.Agregado toResultAgregado(PaqueteEntregableDomain paquete) {
        return new AgregacionPaqueteEntregableResult.Agregado(paquete.getEntregableProyectoGrado());
    }

    public static AgregacionPaqueteEntregableResult.Duplicado toResultDuplicado(PaqueteEntregableDomain paquete) {
        return new AgregacionPaqueteEntregableResult.Duplicado(paquete.getEntregableProyectoGrado());
    }
}
