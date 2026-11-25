package com.arquisoft.fichas.application.evaluacionfichaperfil.command.result.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.result.InicioEvaluacionFichaPerfilResult;

import java.util.UUID;

public final class InicioEvaluacionFichaPerfilResultMapper {

    private InicioEvaluacionFichaPerfilResultMapper() {}

    public static InicioEvaluacionFichaPerfilResult toResultRegistrada(UUID evaluacion) {
        return new InicioEvaluacionFichaPerfilResult.Registrada(evaluacion);
    }

    public static InicioEvaluacionFichaPerfilResult toResultReactivada(UUID evaluacion) {
        return new InicioEvaluacionFichaPerfilResult.Reactivada(evaluacion);
    }
}
