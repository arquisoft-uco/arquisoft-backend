package com.arquisoft.fichas.application.itemfichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.itemfichaperfil.query.criteria.ItemFichaPerfilCoordinadorCriteria;
import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.fichas.application.itemfichaperfil.query.secondaryport.ItemFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.itemfichaperfil.query.usecase.ConsultarItemsFichaPerfilCoordinadorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarItemsFichaPerfilCoordinadorUseCaseImpl implements ConsultarItemsFichaPerfilCoordinadorUseCase {

    private final ItemFichaPerfilQueryOutputPort itemFichaPerfilQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<ItemFichaPerfilReadModel> ejecutar(ItemFichaPerfilCoordinadorCriteria entrada) {
        logger.debug(ItemFichaPerfilKey.LOG_CONSULTANDO_COORDINADOR, entrada.fichaPerfil());

        var items = itemFichaPerfilQueryOutputPort.consultarPorFicha(entrada.fichaPerfil());

        logger.debug(ItemFichaPerfilKey.LOG_CONSULTA_COORDINADOR_COMPLETADA, items.size());
        return items;
    }
}
