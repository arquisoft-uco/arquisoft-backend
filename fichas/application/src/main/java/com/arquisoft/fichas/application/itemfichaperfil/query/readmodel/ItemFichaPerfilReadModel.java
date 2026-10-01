package com.arquisoft.fichas.application.itemfichaperfil.query.readmodel;

import java.util.UUID;

public record ItemFichaPerfilReadModel(
        UUID id,
        UUID fichaPerfilId,
        String tipoItem,
        String tipoItemNombre,
        String contenido
) {
}
