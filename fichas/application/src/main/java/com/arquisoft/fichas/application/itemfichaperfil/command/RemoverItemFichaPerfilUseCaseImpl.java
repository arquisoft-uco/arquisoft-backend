package com.arquisoft.fichas.application.itemfichaperfil.command;

import com.arquisoft.fichas.application.fichaperfil.command.validator.FichaPerfilValidator;
import com.arquisoft.fichas.application.fichaperfil.query.criteria.PropietarioFichaCriteria;
import com.arquisoft.fichas.application.itemfichaperfil.command.model.RemoverItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.port.in.RemoverItemFichaPerfilUseCase;
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
    private final FichaPerfilValidator fichaPerfilValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(RemoverItemFichaPerfilCommand entrada) {
        var item = itemOutputPort.buscarPorId(entrada.item())
                .orElseThrow(() -> new ItemFichaPerfilNoEncontradoException(entrada.item()));

        fichaPerfilValidator.validarEstudiantePropietario(
                new PropietarioFichaCriteria(item.getFichaPerfilId(), entrada.estudiante()));

        long totalRevisiones = revisionQueryPort.contarPorItem(entrada.item());
        item.removerse(totalRevisiones);

        itemOutputPort.eliminarPorId(entrada.item());

        logger.info(FichasMessages.ItemFichaPerfil.LOG_REMOVIDO, entrada.item(), item.getFichaPerfilId());
    }
}
