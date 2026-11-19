package com.arquisoft.proyectos.application.proyectogrado.command.result.mapper;

import com.arquisoft.proyectos.application.proyectogrado.command.result.AtrasoProyectoGradoResult;
import com.arquisoft.proyectos.domain.proyectogrado.AtrasoProyectoGradoDomain;

public final class AtrasoProyectoGradoResultMapper {

    private AtrasoProyectoGradoResultMapper() {}

    public static AtrasoProyectoGradoResult.Atrasado toResultAtrasado(AtrasoProyectoGradoDomain atraso) {
        return new AtrasoProyectoGradoResult.Atrasado(atraso.getProyectoGrado());
    }

    public static AtrasoProyectoGradoResult.Omitido toResultOmitido(
            AtrasoProyectoGradoDomain atraso, String estadoActual) {
        return new AtrasoProyectoGradoResult.Omitido(atraso.getProyectoGrado(), estadoActual);
    }
}
