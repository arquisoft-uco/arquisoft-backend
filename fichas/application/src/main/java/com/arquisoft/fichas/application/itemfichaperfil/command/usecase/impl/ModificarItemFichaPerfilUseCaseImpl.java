package com.arquisoft.fichas.application.itemfichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.fichas.application.itemfichaperfil.command.usecase.ModificarItemFichaPerfilUseCase;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.ModificarItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.ModificarItemFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarItemFichaPerfilUseCaseImpl implements ModificarItemFichaPerfilUseCase {

    private final ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;
    private final ModificarItemFichaPerfilValidator modificarItemFichaPerfilValidator;
    private final AppLogger logger;
    private final MessageCatalog catalog;

    @Override
    public void ejecutar(ModificarItemFichaPerfilDomain entrada) {
        modificarItemFichaPerfilValidator.validar(entrada.getItem(), entrada.getEstudiante());

        itemFichaPerfilOutputPort.actualizarContenido(entrada.getItem(), entrada.getContenido());

        logger.info(catalog.obtener(FichasKeys.ItemFichaPerfil.LOG_MODIFICADO), entrada.getItem());
    }
}
