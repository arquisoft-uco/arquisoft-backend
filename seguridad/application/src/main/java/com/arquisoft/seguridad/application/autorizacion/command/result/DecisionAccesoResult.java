package com.arquisoft.seguridad.application.autorizacion.command.result;

public sealed interface DecisionAccesoResult {

    record Permitida(String idDecision) implements DecisionAccesoResult {}

    record NoAutenticada() implements DecisionAccesoResult {}

    record Denegada() implements DecisionAccesoResult {}
}
