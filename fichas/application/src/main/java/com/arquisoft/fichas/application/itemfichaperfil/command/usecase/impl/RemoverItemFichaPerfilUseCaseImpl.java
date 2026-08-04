package com.arquisoft.fichas.application.itemfichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.fichas.application.itemfichaperfil.command.model.RemoverItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.usecase.RemoverItemFichaPerfilUseCase;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.RemoverItemFichaPerfilValidator;
import com.arquisoft.fichas.application.revisionitem.query.port.out.RevisionItemQueryOutputPort;
import com.arquisoft.fichas.application.itemfichaperfil.command.finder.ItemFichaPerfilFinder;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverItemFichaPerfilUseCaseImpl implements RemoverItemFichaPerfilUseCase {

    private final ItemFichaPerfilOutputPort itemOutputPort;
    private final ItemFichaPerfilFinder itemFichaPerfilFinder;
    private final RevisionItemQueryOutputPort revisionQueryPort;
    private final RemoverItemFichaPerfilValidator removerItemFichaPerfilValidator;
    private final AppLogger logger;
    private final MessageCatalog catalog;

    @Override
    public void ejecutar(RemoverItemFichaPerfilCommand entrada) {
        var item = itemFichaPerfilFinder.obtener(entrada.item());

        removerItemFichaPerfilValidator.validar(item.getFichaPerfilId(), entrada.estudiante());

        long totalRevisiones = revisionQueryPort.contarPorItem(entrada.item());
        item.removerse(totalRevisiones);

        itemOutputPort.eliminarPorId(entrada.item());

        logger.info(catalog.obtener(FichasKeys.ItemFichaPerfil.LOG_REMOVIDO), entrada.item(), item.getFichaPerfilId());
    }
}
