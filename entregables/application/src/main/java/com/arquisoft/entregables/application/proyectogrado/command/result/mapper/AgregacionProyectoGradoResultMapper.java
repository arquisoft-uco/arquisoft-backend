package com.arquisoft.entregables.application.proyectogrado.command.result.mapper;

import com.arquisoft.entregables.application.proyectogrado.command.result.AgregacionProyectoGradoResult;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;

public final class AgregacionProyectoGradoResultMapper {

    private AgregacionProyectoGradoResultMapper() {}

    public static AgregacionProyectoGradoResult.Agregado toResultAgregado(ProyectoGradoDomain proyecto) {
        return new AgregacionProyectoGradoResult.Agregado(proyecto.getId());
    }

    public static AgregacionProyectoGradoResult.Duplicado toResultDuplicado(ProyectoGradoDomain proyecto) {
        return new AgregacionProyectoGradoResult.Duplicado(proyecto.getId());
    }
}
