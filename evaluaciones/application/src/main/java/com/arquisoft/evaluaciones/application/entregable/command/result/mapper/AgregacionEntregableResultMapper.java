package com.arquisoft.evaluaciones.application.entregable.command.result.mapper;

import com.arquisoft.evaluaciones.application.entregable.command.result.AgregacionEntregableResult;
import com.arquisoft.evaluaciones.domain.entregable.EntregableDomain;

public final class AgregacionEntregableResultMapper {

    private AgregacionEntregableResultMapper() {}

    public static AgregacionEntregableResult.Agregado toResultAgregado(EntregableDomain entregable) {
        return new AgregacionEntregableResult.Agregado(entregable.getId());
    }

    public static AgregacionEntregableResult.Duplicado toResultDuplicado(EntregableDomain entregable) {
        return new AgregacionEntregableResult.Duplicado(entregable.getId());
    }
}
