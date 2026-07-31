package com.arquisoft.fichas.application.fichaperfil.command.model;

import java.util.UUID;

public record CambiarAsesorFichaCommand(
        UUID fichaPerfil,
        UUID nuevoAsesorFicha
) {}
