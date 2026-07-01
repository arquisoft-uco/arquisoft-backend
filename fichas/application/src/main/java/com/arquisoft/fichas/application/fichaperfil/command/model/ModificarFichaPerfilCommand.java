package com.arquisoft.fichas.application.fichaperfil.command.model;

import java.util.UUID;

public record ModificarFichaPerfilCommand(
        UUID fichaId,
        UUID estudianteId,
        String tituloProyecto
) {}
