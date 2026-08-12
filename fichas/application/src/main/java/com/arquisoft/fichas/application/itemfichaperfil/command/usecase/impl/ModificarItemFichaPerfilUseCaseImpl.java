package com.arquisoft.fichas.application.itemfichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.fichas.application.estadofichaperfil.command.finder.EstadoActualFichaPerfilFinder;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.VinculoEstudianteFichaExisteFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.finder.FichaPerfilDelItemFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.usecase.ModificarItemFichaPerfilUseCase;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.ModificarItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.VinculoEstudianteFicha;
import com.arquisoft.fichas.domain.itemfichaperfil.ModificacionItemFichaPerfilDomain;
import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.ItemFichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarItemFichaPerfilUseCaseImpl implements ModificarItemFichaPerfilUseCase {

    private final ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;
    private final FichaPerfilDelItemFinder fichaPerfilDelItemFinder;
    private final VinculoEstudianteFichaExisteFinder vinculoEstudianteFichaExisteFinder;
    private final EstadoActualFichaPerfilFinder estadoActualFichaPerfilFinder;
    private final ModificarItemFichaPerfilValidator modificarItemFichaPerfilValidator;
    private final AppLogger logger;
    private final CatalogoMensajes catalogo;

    @Override
    public void ejecutar(ModificacionItemFichaPerfilDomain entrada) {
        var fichaDelItem = fichaPerfilDelItemFinder.obtener(entrada.getItem());

        boolean esPropietario = fichaDelItem
                .map(ficha -> vinculoEstudianteFichaExisteFinder.obtener(
                        new VinculoEstudianteFicha(ficha, entrada.getEstudiante())))
                .orElse(false);

        var estadoActual = fichaDelItem.flatMap(estadoActualFichaPerfilFinder::obtener);

        modificarItemFichaPerfilValidator.validar(
                entrada.getItem(), entrada.getEstudiante(), fichaDelItem, esPropietario, estadoActual);

        itemFichaPerfilOutputPort.actualizarContenido(entrada.getItem(), entrada.getContenido());

        logger.info(catalogo.obtener(ItemFichaPerfilKey.LOG_MODIFICADO), entrada.getItem());
    }
}
