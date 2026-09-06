package com.arquisoft.fichas.application.itemfichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.itemfichaperfil.query.criteria.ItemFichaPerfilRepresentanteCriteria;
import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.fichas.application.itemfichaperfil.query.secondaryport.ItemFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.itemfichaperfil.query.usecase.ConsultarItemsFichaPerfilRepresentanteUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarItemsFichaPerfilRepresentanteUseCaseImpl implements ConsultarItemsFichaPerfilRepresentanteUseCase {

    private final ItemFichaPerfilQueryOutputPort itemFichaPerfilQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<ItemFichaPerfilReadModel> ejecutar(ItemFichaPerfilRepresentanteCriteria entrada) {
        logger.debug(ItemFichaPerfilKey.LOG_CONSULTANDO_REPRESENTANTE, entrada.fichaPerfil());

        var items = itemFichaPerfilQueryOutputPort.consultarPorFichaYRepresentante(
                entrada.fichaPerfil(), entrada.representanteComite());

        logger.debug(ItemFichaPerfilKey.LOG_CONSULTA_REPRESENTANTE_COMPLETADA, items.size());
        return items;
    }
}
