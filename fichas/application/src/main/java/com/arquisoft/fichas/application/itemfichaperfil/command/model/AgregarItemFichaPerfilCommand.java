package com.arquisoft.fichas.application.itemfichaperfil.command.model;

import java.util.UUID;

public record AgregarItemFichaPerfilCommand(
        UUID fichaPerfil,
        String tipoItem,
        String contenido,
        UUID estudiante
) {}
