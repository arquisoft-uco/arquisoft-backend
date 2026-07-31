package com.arquisoft.fichas.application.estudiantefichaperfil.command.model;

import java.util.UUID;

public record RemoverEstudianteFichaPerfilCommand(
        UUID fichaPerfil,
        UUID estudiante
) {}
