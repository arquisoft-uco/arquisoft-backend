package com.arquisoft.fichas.application.itemfichaperfil.command.model;

import java.util.UUID;

public record ModificarItemFichaPerfilCommand(
        UUID item,
        String contenido,
        UUID estudiante
) {}
