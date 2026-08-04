package com.arquisoft.fichas.application.itemfichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.fichas.application.estadofichaperfil.query.port.out.EstadoFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.itemfichaperfil.command.model.ModificarItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.usecase.ModificarItemFichaPerfilUseCase;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.ModificarItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.application.itemfichaperfil.command.finder.ItemFichaPerfilFinder;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarItemFichaPerfilUseCaseImpl implements ModificarItemFichaPerfilUseCase {

    private final ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;
    private final ItemFichaPerfilFinder itemFichaPerfilFinder;
    private final EstadoFichaPerfilQueryOutputPort estadoFichaPerfilQueryOutputPort;
    private final ModificarItemFichaPerfilValidator modificarItemFichaPerfilValidator;
    private final AppLogger logger;
    private final MessageCatalog catalog;

    @Override
    public void ejecutar(ModificarItemFichaPerfilCommand entrada) {
        var item = itemFichaPerfilFinder.obtener(entrada.item());

        modificarItemFichaPerfilValidator.validar(item.getFichaPerfilId(), entrada.estudiante());

        var estadoActual = estadoFichaPerfilQueryOutputPort.obtenerEstadoActual(item.getFichaPerfilId())
                .orElseThrow(() -> new FichaPerfilNoEncontradaException(item.getFichaPerfilId()));

        item.modificarContenido(entrada.contenido(), estadoActual);

        itemFichaPerfilOutputPort.guardar(item);

        logger.info(catalog.obtener(FichasKeys.ItemFichaPerfil.LOG_MODIFICADO), entrada.item());
    }
}
