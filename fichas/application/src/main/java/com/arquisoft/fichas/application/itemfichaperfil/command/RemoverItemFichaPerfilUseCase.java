package com.arquisoft.fichas.application.itemfichaperfil.command;

import com.arquisoft.fichas.application.fichaperfil.exception.FichaNoPropietarioException;
import com.arquisoft.fichas.application.fichaperfil.query.port.out.FichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.itemfichaperfil.command.model.RemoverItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.port.in.RemoverItemFichaPerfilInputPort;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.application.revisionitem.query.port.out.RevisionItemQueryOutputPort;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.shared.message.FichasMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(transactionManager = "fichasTransactionManager")
public class RemoverItemFichaPerfilUseCase implements RemoverItemFichaPerfilInputPort {

    private final ItemFichaPerfilOutputPort itemOutputPort;
    private final FichaPerfilQueryOutputPort fichaQueryPort;
    private final RevisionItemQueryOutputPort revisionQueryPort;

    @Override
    public void ejecutar(RemoverItemFichaPerfilCommand command) {
        var item = itemOutputPort.buscarPorId(command.itemId())
                .orElseThrow(() -> new ItemFichaPerfilNoEncontradoException(command.itemId()));

        if (!fichaQueryPort.esEstudiantePropietario(item.getFichaPerfilId(), command.estudianteId())) {
            throw new FichaNoPropietarioException(item.getFichaPerfilId(), command.estudianteId());
        }

        long totalRevisiones = revisionQueryPort.contarPorItem(command.itemId());
        item.removerse(totalRevisiones);

        itemOutputPort.eliminarPorId(command.itemId());

        log.info(FichasMessages.ItemFichaPerfil.LOG_REMOVIDO, command.itemId(), item.getFichaPerfilId());
    }
}
