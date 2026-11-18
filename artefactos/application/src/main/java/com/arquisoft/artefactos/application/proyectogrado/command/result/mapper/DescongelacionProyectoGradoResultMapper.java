package com.arquisoft.artefactos.application.proyectogrado.command.result.mapper;

import com.arquisoft.artefactos.application.proyectogrado.command.result.DescongelacionProyectoGradoResult;
import com.arquisoft.artefactos.domain.proyectogrado.DescongelacionProyectoGradoDomain;

public final class DescongelacionProyectoGradoResultMapper {

    private DescongelacionProyectoGradoResultMapper() {}

    public static DescongelacionProyectoGradoResult.Descongelado toResultDescongelado(
            DescongelacionProyectoGradoDomain descongelacion) {
        return new DescongelacionProyectoGradoResult.Descongelado(descongelacion.getProyectoGrado());
    }

    public static DescongelacionProyectoGradoResult.Omitido toResultOmitido(
            DescongelacionProyectoGradoDomain descongelacion) {
        return new DescongelacionProyectoGradoResult.Omitido(descongelacion.getProyectoGrado());
    }
}
