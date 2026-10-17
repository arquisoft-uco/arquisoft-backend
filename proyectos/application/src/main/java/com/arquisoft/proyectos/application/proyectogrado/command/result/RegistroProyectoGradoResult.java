package com.arquisoft.proyectos.application.proyectogrado.command.result;

import java.util.UUID;

public sealed interface RegistroProyectoGradoResult {

    record Registrado(UUID proyectoGrado, UUID fichaPerfil) implements RegistroProyectoGradoResult {}

    record Duplicado(UUID fichaPerfil) implements RegistroProyectoGradoResult {}
}
