package com.arquisoft.entregables.application.artefacto.command.result;

import java.util.UUID;

public sealed interface RegistroVersionCerradaArtefactoResult {

    record Registrada(UUID artefacto) implements RegistroVersionCerradaArtefactoResult {}

    record Obsoleta(UUID artefacto) implements RegistroVersionCerradaArtefactoResult {}

    record ProyectoCerrado(UUID artefacto) implements RegistroVersionCerradaArtefactoResult {}
}
