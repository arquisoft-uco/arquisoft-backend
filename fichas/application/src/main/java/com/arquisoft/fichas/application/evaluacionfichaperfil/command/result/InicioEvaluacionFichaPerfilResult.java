package com.arquisoft.fichas.application.evaluacionfichaperfil.command.result;

import java.util.UUID;

public sealed interface InicioEvaluacionFichaPerfilResult {

    record Registrada(UUID evaluacion) implements InicioEvaluacionFichaPerfilResult {}

    record Reactivada(UUID evaluacion) implements InicioEvaluacionFichaPerfilResult {}
}
