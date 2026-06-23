package com.arquisoft.fichas.application.fichaperfil.command.model;

import java.util.List;
import java.util.UUID;

public record RegistrarFichaPerfilCommand(
        String tituloProyecto,
        UUID asesorFichaId,
        List<UUID> estudiantesIds
) {}
