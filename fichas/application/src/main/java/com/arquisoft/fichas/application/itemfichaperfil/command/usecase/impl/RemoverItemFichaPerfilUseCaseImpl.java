package com.arquisoft.fichas.application.itemfichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.model.RemoverItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.usecase.RemoverItemFichaPerfilUseCase;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.RemoverItemFichaPerfilValidator;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.application.revisionitem.query.port.out.RevisionItemQueryOutputPort;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.FichasMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverItemFichaPerfilUseCaseImpl implements RemoverItemFichaPerfilUseCase {

    private final ItemFichaPerfilOutputPort itemOutputPort;
    private final RevisionItemQueryOutputPort revisionQueryPort;
    private final RemoverItemFichaPerfilValidator removerItemFichaPerfilValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(RemoverItemFichaPerfilCommand entrada) {
        var item = itemOutputPort.buscarPorId(entrada.item())
                .orElseThrow(() -> new ItemFichaPerfilNoEncontradoException(entrada.item()));

        removerItemFichaPerfilValidator.validar(item.getFichaPerfilId(), entrada.estudiante());

        long totalRevisiones = revisionQueryPort.contarPorItem(entrada.item());
        item.removerse(totalRevisiones);

        itemOutputPort.eliminarPorId(entrada.item());

        logger.info(FichasMessages.ItemFichaPerfil.LOG_REMOVIDO, entrada.item(), item.getFichaPerfilId());
    }
}
