package com.arquisoft.fichas.application.itemfichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.finder.PertenenciaItemFichaPerfilFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.ItemFichaPerfilOutputPort;
import com.arquisoft.fichas.application.itemfichaperfil.command.usecase.ModificarItemFichaPerfilUseCase;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.ModificarItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.itemfichaperfil.ModificacionItemFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.model.ItemDeEstudiante;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarItemFichaPerfilUseCaseImpl implements ModificarItemFichaPerfilUseCase {

    private final ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;
    private final PertenenciaItemFichaPerfilFinder pertenenciaItemFichaPerfilFinder;
    private final ModificarItemFichaPerfilValidator modificarItemFichaPerfilValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(ModificacionItemFichaPerfilDomain entrada) {
        logger.info(ItemFichaPerfilKey.LOG_MODIFICANDO,
                entrada.getItem(), entrada.getEstudiante());

        var pertenencia = pertenenciaItemFichaPerfilFinder.obtener(
                new ItemDeEstudiante(entrada.getItem(), entrada.getEstudiante()));
        var itemExiste = !pertenencia.esVacio();

        logger.debug(ItemFichaPerfilKey.LOG_VERIFICACION_MODIFICAR,
                itemExiste, pertenencia.esPropietario(), pertenencia.fichaPerfil());

        modificarItemFichaPerfilValidator.validar(entrada.getItem(), entrada.getEstudiante(),
                pertenencia.fichaPerfil(), itemExiste, pertenencia.esPropietario(),
                pertenencia.estadoActual());

        itemFichaPerfilOutputPort.actualizarContenido(entrada.getItem(), entrada.getContenido());

        logger.info(ItemFichaPerfilKey.LOG_MODIFICADO, entrada.getItem());
    }
}
