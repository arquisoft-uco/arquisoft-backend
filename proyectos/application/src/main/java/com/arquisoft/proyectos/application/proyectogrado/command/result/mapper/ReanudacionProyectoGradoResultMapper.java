package com.arquisoft.proyectos.application.proyectogrado.command.result.mapper;

import com.arquisoft.proyectos.application.proyectogrado.command.result.ReanudacionProyectoGradoResult;
import com.arquisoft.proyectos.domain.proyectogrado.ReanudacionProyectoGradoDomain;

public final class ReanudacionProyectoGradoResultMapper {

    private ReanudacionProyectoGradoResultMapper() {}

    public static ReanudacionProyectoGradoResult.Reanudado toResultReanudado(
            ReanudacionProyectoGradoDomain reanudacion) {
        return new ReanudacionProyectoGradoResult.Reanudado(reanudacion.getProyectoGrado());
    }

    public static ReanudacionProyectoGradoResult.Omitido toResultOmitido(
            ReanudacionProyectoGradoDomain reanudacion, String estadoActual) {
        return new ReanudacionProyectoGradoResult.Omitido(
                reanudacion.getProyectoGrado(), estadoActual, reanudacion.plazoVigente());
    }
}
