package com.arquisoft.fichas.application.itemfichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.itemfichaperfil.query.criteria.ItemFichaPerfilAsesorCriteria;
import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.fichas.application.itemfichaperfil.query.secondaryport.ItemFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.itemfichaperfil.query.usecase.ConsultarItemsFichaPerfilAsesorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarItemsFichaPerfilAsesorUseCaseImpl implements ConsultarItemsFichaPerfilAsesorUseCase {

    private final ItemFichaPerfilQueryOutputPort itemFichaPerfilQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<ItemFichaPerfilReadModel> ejecutar(ItemFichaPerfilAsesorCriteria entrada) {
        logger.debug(ItemFichaPerfilKey.LOG_CONSULTANDO_ASESOR, entrada.fichaPerfil());

        var items = itemFichaPerfilQueryOutputPort.consultarPorFichaYAsesor(
                entrada.fichaPerfil(), entrada.asesorFicha());

        logger.debug(ItemFichaPerfilKey.LOG_CONSULTA_ASESOR_COMPLETADA, items.size());
        return items;
    }
}
