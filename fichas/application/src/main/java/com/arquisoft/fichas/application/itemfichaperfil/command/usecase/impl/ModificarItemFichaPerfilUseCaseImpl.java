package com.arquisoft.fichas.application.itemfichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import com.arquisoft.fichas.application.estadofichaperfil.command.finder.EstadoActualFichaPerfilFinder;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.VinculoEstudianteFichaExisteFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.finder.FichaPerfilDelItemFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.usecase.ModificarItemFichaPerfilUseCase;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.ModificarItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.VinculoEstudianteFicha;
import com.arquisoft.fichas.domain.itemfichaperfil.ModificacionItemFichaPerfilDomain;
import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.ItemFichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ModificarItemFichaPerfilUseCaseImpl implements ModificarItemFichaPerfilUseCase {

    private final ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;
    private final FichaPerfilDelItemFinder fichaPerfilDelItemFinder;
    private final VinculoEstudianteFichaExisteFinder vinculoEstudianteFichaExisteFinder;
    private final EstadoActualFichaPerfilFinder estadoActualFichaPerfilFinder;
    private final ModificarItemFichaPerfilValidator modificarItemFichaPerfilValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(ModificacionItemFichaPerfilDomain entrada) {
        logger.info(ItemFichaPerfilKey.LOG_MODIFICANDO,
                entrada.getItem(), entrada.getEstudiante());

        var fichaEncontrada = fichaPerfilDelItemFinder.obtener(entrada.getItem());

        boolean itemExiste = fichaEncontrada.isPresent();
        UUID fichaDelItem = fichaEncontrada.orElse(UtilUUID.obtenerUUIDPorDefecto());

        boolean esPropietario = fichaEncontrada
                .map(ficha -> vinculoEstudianteFichaExisteFinder.obtener(
                        new VinculoEstudianteFicha(ficha, entrada.getEstudiante())))
                .orElse(false);

        var estadoActual = fichaEncontrada.flatMap(estadoActualFichaPerfilFinder::obtener)
                .orElse(EstadoFichaPerfilDomain.VACIO);

        logger.debug(ItemFichaPerfilKey.LOG_VERIFICACION_MODIFICAR,
                itemExiste, esPropietario, fichaDelItem);

        modificarItemFichaPerfilValidator.validar(entrada.getItem(), entrada.getEstudiante(),
                fichaDelItem, itemExiste, esPropietario, estadoActual);

        itemFichaPerfilOutputPort.actualizarContenido(entrada.getItem(), entrada.getContenido());

        logger.info(ItemFichaPerfilKey.LOG_MODIFICADO, entrada.getItem());
    }
}
