package com.arquisoft.fichas.application.itemfichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.itemfichaperfil.query.criteria.ItemFichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.fichas.application.itemfichaperfil.query.secondaryport.ItemFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.itemfichaperfil.query.usecase.ConsultarItemsFichaPerfilEstudianteUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarItemsFichaPerfilEstudianteUseCaseImpl implements ConsultarItemsFichaPerfilEstudianteUseCase {

    private final ItemFichaPerfilQueryOutputPort itemFichaPerfilQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<ItemFichaPerfilReadModel> ejecutar(ItemFichaPerfilEstudianteCriteria entrada) {
        logger.debug(ItemFichaPerfilKey.LOG_CONSULTANDO_ESTUDIANTE, entrada.fichaPerfil());

        var items = itemFichaPerfilQueryOutputPort.consultarPorFichaYEstudiante(
                entrada.fichaPerfil(), entrada.estudiante());

        logger.debug(ItemFichaPerfilKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA, items.size());
        return items;
    }
}
