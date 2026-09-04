package com.arquisoft.fichas.application.itemfichaperfil.query.secondaryport;

import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;

import java.util.List;
import java.util.UUID;

public interface ItemFichaPerfilQueryOutputPort {

    List<ItemFichaPerfilReadModel> consultarPorFichaYAsesor(UUID fichaPerfil, UUID asesorFicha);
}
