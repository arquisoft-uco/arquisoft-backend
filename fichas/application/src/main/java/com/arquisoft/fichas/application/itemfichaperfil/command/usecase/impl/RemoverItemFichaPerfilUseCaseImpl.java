package com.arquisoft.fichas.application.itemfichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.fichas.application.itemfichaperfil.command.usecase.RemoverItemFichaPerfilUseCase;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.RemoverItemFichaPerfilValidator;
import com.arquisoft.fichas.application.revisionitem.query.port.out.RevisionItemQueryOutputPort;
import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.RemoverItemFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.model.RevisionesItemCriteria;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemSinRevisionesRule;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverItemFichaPerfilUseCaseImpl implements RemoverItemFichaPerfilUseCase {

    private final ItemFichaPerfilOutputPort itemOutputPort;
    private final RevisionItemQueryOutputPort revisionQueryPort;
    private final RemoverItemFichaPerfilValidator removerItemFichaPerfilValidator;
    private final ItemSinRevisionesRule itemSinRevisionesRule;
    private final AppLogger logger;
    private final MessageCatalog catalog;

    @Override
    public void ejecutar(RemoverItemFichaPerfilDomain entrada) {
        removerItemFichaPerfilValidator.validar(entrada.getItem(), entrada.getEstudiante());

        long totalRevisiones = revisionQueryPort.contarPorItem(entrada.getItem());
        itemSinRevisionesRule.validar(new RevisionesItemCriteria(entrada.getItem(), totalRevisiones));

        itemOutputPort.removerItem(entrada.getItem());

        logger.info(catalog.obtener(FichasKeys.ItemFichaPerfil.LOG_REMOVIDO), entrada.getItem());
    }
}
