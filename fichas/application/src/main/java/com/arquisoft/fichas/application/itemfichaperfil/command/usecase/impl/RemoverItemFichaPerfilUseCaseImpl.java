package com.arquisoft.fichas.application.itemfichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.finder.PertenenciaItemFichaPerfilFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.ItemFichaPerfilOutputPort;
import com.arquisoft.fichas.application.itemfichaperfil.command.usecase.RemoverItemFichaPerfilUseCase;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.RemoverItemFichaPerfilValidator;
import com.arquisoft.fichas.application.revisionitem.command.finder.RevisionesDelItemFinder;
import com.arquisoft.fichas.domain.itemfichaperfil.RemocionItemFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.model.ItemDeEstudiante;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverItemFichaPerfilUseCaseImpl implements RemoverItemFichaPerfilUseCase {

    private final ItemFichaPerfilOutputPort itemOutputPort;
    private final PertenenciaItemFichaPerfilFinder pertenenciaItemFichaPerfilFinder;
    private final RevisionesDelItemFinder revisionesDelItemFinder;
    private final RemoverItemFichaPerfilValidator removerItemFichaPerfilValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(RemocionItemFichaPerfilDomain entrada) {
        logger.info(ItemFichaPerfilKey.LOG_REMOVIENDO,
                entrada.getItem(), entrada.getEstudiante());

        var pertenencia = pertenenciaItemFichaPerfilFinder.obtener(
                new ItemDeEstudiante(entrada.getItem(), entrada.getEstudiante()));
        var itemExiste = !pertenencia.esVacio();
        var totalRevisiones = revisionesDelItemFinder.obtener(entrada.getItem());

        logger.debug(ItemFichaPerfilKey.LOG_VERIFICACION_REMOVER,
                itemExiste, pertenencia.esPropietario(), totalRevisiones);

        removerItemFichaPerfilValidator.validar(entrada.getItem(), entrada.getEstudiante(),
                pertenencia.fichaPerfil(), itemExiste, pertenencia.esPropietario(), totalRevisiones);

        itemOutputPort.removerItem(entrada.getItem());

        logger.info(ItemFichaPerfilKey.LOG_REMOVIDO, entrada.getItem());
    }
}
