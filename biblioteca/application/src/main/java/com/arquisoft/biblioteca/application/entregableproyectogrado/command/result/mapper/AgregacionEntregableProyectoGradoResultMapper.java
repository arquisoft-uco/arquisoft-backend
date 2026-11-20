package com.arquisoft.biblioteca.application.entregableproyectogrado.command.result.mapper;

import com.arquisoft.biblioteca.application.entregableproyectogrado.command.result.AgregacionEntregableProyectoGradoResult;
import com.arquisoft.biblioteca.domain.entregableproyectogrado.EntregableProyectoGradoDomain;

public final class AgregacionEntregableProyectoGradoResultMapper {

    private AgregacionEntregableProyectoGradoResultMapper() {}

    public static AgregacionEntregableProyectoGradoResult.Agregado toResultAgregado(
            EntregableProyectoGradoDomain entregable) {
        return new AgregacionEntregableProyectoGradoResult.Agregado(entregable.getId(), entregable.getProyectoGrado());
    }

    public static AgregacionEntregableProyectoGradoResult.Descartado toResultDescartado(
            EntregableProyectoGradoDomain entregable) {
        return new AgregacionEntregableProyectoGradoResult.Descartado(entregable.getId());
    }
}
