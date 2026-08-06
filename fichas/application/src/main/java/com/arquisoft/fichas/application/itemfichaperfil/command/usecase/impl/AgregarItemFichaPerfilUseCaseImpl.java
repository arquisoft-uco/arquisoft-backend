package com.arquisoft.fichas.application.itemfichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.fichas.application.itemfichaperfil.command.usecase.AgregarItemFichaPerfilUseCase;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.AgregarItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.AgregarItemFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarItemFichaPerfilUseCaseImpl implements AgregarItemFichaPerfilUseCase {

    private final ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;
    private final AgregarItemFichaPerfilValidator agregarItemFichaPerfilValidator;
    private final AppLogger logger;
    private final MessageCatalog catalog;

    @Override
    public UUID ejecutar(AgregarItemFichaPerfilDomain entrada) {
        var item = entrada.getItem();

        agregarItemFichaPerfilValidator.validar(item, entrada.getEstudiante());

        itemFichaPerfilOutputPort.registrarItem(item);

        logger.info(
                catalog.obtener(ItemFichaPerfilKey.LOG_AGREGADO),
                item.getId(),
                item.getFichaPerfilId(),
                item.getTipoItem()
        );

        return item.getId();
    }
}
