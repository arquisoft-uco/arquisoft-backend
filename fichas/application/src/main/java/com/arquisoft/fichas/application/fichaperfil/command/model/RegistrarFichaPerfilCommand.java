package com.arquisoft.fichas.application.fichaperfil.command.model;

import java.util.UUID;

public record RegistrarFichaPerfilCommand(
        UUID id,
        String tituloProyecto,
        UUID asesorFichaId
) {}
