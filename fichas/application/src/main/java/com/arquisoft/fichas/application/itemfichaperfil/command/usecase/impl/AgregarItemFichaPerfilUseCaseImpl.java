package com.arquisoft.fichas.application.itemfichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.fichas.application.itemfichaperfil.command.model.AgregarItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.usecase.AgregarItemFichaPerfilUseCase;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.AgregarItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.ItemFichaPerfilDomain;
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
    public UUID ejecutar(AgregarItemFichaPerfilCommand entrada) {
        var item = ItemFichaPerfilDomain.crear(
                entrada.fichaPerfil(),
                entrada.tipoItem(),
                entrada.contenido()
        );

        agregarItemFichaPerfilValidator.validar(item, entrada.estudiante());

        itemFichaPerfilOutputPort.guardar(item);

        logger.info(
                catalog.obtener(FichasKeys.ItemFichaPerfil.LOG_AGREGADO),
                item.getId(),
                item.getFichaPerfilId(),
                item.getTipoItem()
        );

        return item.getId();
    }
}
