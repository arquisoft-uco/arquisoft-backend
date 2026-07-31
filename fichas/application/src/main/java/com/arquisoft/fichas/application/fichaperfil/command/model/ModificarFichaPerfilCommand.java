package com.arquisoft.fichas.application.fichaperfil.command.model;

import java.util.UUID;

public record ModificarFichaPerfilCommand(
        UUID fichaPerfil,
        UUID estudiante,
        String tituloProyecto
) {}
