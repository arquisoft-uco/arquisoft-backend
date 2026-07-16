package com.arquisoft.fichas.application.fichaperfil.command.model;

import java.util.UUID;

public record CambiarAsesorFichaCommand(
        UUID fichaPerfilId,
        UUID nuevoAsesorFichaId
) {}
