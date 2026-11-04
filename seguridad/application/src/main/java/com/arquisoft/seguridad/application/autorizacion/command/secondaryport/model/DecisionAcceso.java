package com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model;

public sealed interface DecisionAcceso {

    record Permitida(String idDecision) implements DecisionAcceso {}

    record NoAutenticada() implements DecisionAcceso {}

    record Denegada() implements DecisionAcceso {}
}
