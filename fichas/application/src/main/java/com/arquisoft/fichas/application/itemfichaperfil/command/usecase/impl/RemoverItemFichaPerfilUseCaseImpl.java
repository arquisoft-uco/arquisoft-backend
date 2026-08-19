package com.arquisoft.fichas.application.itemfichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.VinculoEstudianteFichaExisteFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.finder.FichaPerfilDelItemFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.usecase.RemoverItemFichaPerfilUseCase;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.RemoverItemFichaPerfilValidator;
import com.arquisoft.fichas.application.revisionitem.command.finder.RevisionesDelItemFinder;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.VinculoEstudianteFicha;
import com.arquisoft.fichas.domain.itemfichaperfil.RemocionItemFichaPerfilDomain;
import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.ItemFichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RemoverItemFichaPerfilUseCaseImpl implements RemoverItemFichaPerfilUseCase {

    private final ItemFichaPerfilOutputPort itemOutputPort;
    private final FichaPerfilDelItemFinder fichaPerfilDelItemFinder;
    private final VinculoEstudianteFichaExisteFinder vinculoEstudianteFichaExisteFinder;
    private final RevisionesDelItemFinder revisionesDelItemFinder;
    private final RemoverItemFichaPerfilValidator removerItemFichaPerfilValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(RemocionItemFichaPerfilDomain entrada) {
        var fichaEncontrada = fichaPerfilDelItemFinder.obtener(entrada.getItem());

        boolean itemExiste = fichaEncontrada.isPresent();
        UUID fichaDelItem = fichaEncontrada.orElse(UtilUUID.obtenerUUIDPorDefecto());

        boolean esPropietario = fichaEncontrada
                .map(ficha -> vinculoEstudianteFichaExisteFinder.obtener(
                        new VinculoEstudianteFicha(ficha, entrada.getEstudiante())))
                .orElse(false);

        long totalRevisiones = revisionesDelItemFinder.obtener(entrada.getItem());

        removerItemFichaPerfilValidator.validar(entrada.getItem(), entrada.getEstudiante(),
                fichaDelItem, itemExiste, esPropietario, totalRevisiones);

        itemOutputPort.removerItem(entrada.getItem());

        logger.info(Mensajes.obtener(ItemFichaPerfilKey.LOG_REMOVIDO), entrada.getItem());
    }
}
