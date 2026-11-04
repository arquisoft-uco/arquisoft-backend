package com.arquisoft.seguridad.application.autorizacion.command.result.mapper;

import com.arquisoft.seguridad.application.autorizacion.command.result.DecisionAccesoResult;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.DecisionAcceso;

public final class DecisionAccesoResultMapper {

    private DecisionAccesoResultMapper() {}

    public static DecisionAccesoResult toResult(DecisionAcceso decision) {
        return switch (decision) {
            case DecisionAcceso.Permitida permitida -> toResultPermitida(permitida.idDecision());
            case DecisionAcceso.NoAutenticada ignorada -> toResultNoAutenticada();
            case DecisionAcceso.Denegada ignorada -> toResultDenegada();
        };
    }

    public static DecisionAccesoResult toResultPermitida(String idDecision) {
        return new DecisionAccesoResult.Permitida(idDecision);
    }

    public static DecisionAccesoResult toResultNoAutenticada() {
        return new DecisionAccesoResult.NoAutenticada();
    }

    public static DecisionAccesoResult toResultDenegada() {
        return new DecisionAccesoResult.Denegada();
    }
}
