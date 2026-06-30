package com.arquisoft.fichas.application.itemfichaperfil.command.model;

import java.util.UUID;

public record AgregarItemFichaPerfilCommand(
        UUID fichaPerfilId,
        String tipoItemCode,
        String contenido,
        UUID estudianteId
) {}
