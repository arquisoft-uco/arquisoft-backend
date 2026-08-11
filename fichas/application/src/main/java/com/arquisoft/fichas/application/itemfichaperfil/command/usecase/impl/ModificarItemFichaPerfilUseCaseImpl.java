package com.arquisoft.fichas.application.itemfichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.fichas.application.itemfichaperfil.command.usecase.ModificarItemFichaPerfilUseCase;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.ModificarItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.itemfichaperfil.ModificacionItemFichaPerfilDomain;
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
    private final CatalogoMensajes catalogo;

    @Override
    public void ejecutar(ModificacionItemFichaPerfilDomain entrada) {
        modificarItemFichaPerfilValidator.validar(entrada.getItem(), entrada.getEstudiante());

        itemFichaPerfilOutputPort.actualizarContenido(entrada.getItem(), entrada.getContenido());

        logger.info(catalogo.obtener(ItemFichaPerfilKey.LOG_MODIFICADO), entrada.getItem());
    }
}
