package com.arquisoft.fichas.application.estudiantefichaperfil.command.model;

import java.util.UUID;

public record RemoverEstudianteFichaPerfilCommand(
        UUID fichaPerfilId,
        UUID estudianteId
) {}
