package com.arquisoft.fichas.application.itemfichaperfil.command.model;

import java.util.UUID;

public record ModificarItemFichaPerfilCommand(
        UUID itemId,
        String contenido,
        UUID estudianteId
) {}
