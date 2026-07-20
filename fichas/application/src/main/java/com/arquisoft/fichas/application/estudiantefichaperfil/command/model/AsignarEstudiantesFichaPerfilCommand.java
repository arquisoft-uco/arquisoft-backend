package com.arquisoft.fichas.application.estudiantefichaperfil.command.model;

import java.util.List;
import java.util.UUID;

public record AsignarEstudiantesFichaPerfilCommand(
        UUID fichaPerfilId,
        List<UUID> estudiantesIds
) {
}
