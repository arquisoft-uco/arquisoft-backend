package com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.result.mapper;

import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.result.AgregacionCoordinadorProyectoGradoResult;
import com.arquisoft.evaluaciones.domain.coordinadorproyectogrado.CoordinadorProyectoGradoDomain;

public final class AgregacionCoordinadorProyectoGradoResultMapper {

    private AgregacionCoordinadorProyectoGradoResultMapper() {}

    public static AgregacionCoordinadorProyectoGradoResult.Agregada toResultAgregada(
            CoordinadorProyectoGradoDomain coordinador) {
        return new AgregacionCoordinadorProyectoGradoResult.Agregada(coordinador.getProyectoGrado());
    }

    public static AgregacionCoordinadorProyectoGradoResult.Duplicada toResultDuplicada(
            CoordinadorProyectoGradoDomain coordinador) {
        return new AgregacionCoordinadorProyectoGradoResult.Duplicada(coordinador.getProyectoGrado());
    }
}
