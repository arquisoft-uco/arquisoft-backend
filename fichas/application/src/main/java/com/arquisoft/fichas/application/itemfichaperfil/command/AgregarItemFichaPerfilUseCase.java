package com.arquisoft.fichas.application.itemfichaperfil.command;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.port.out.EstudianteFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.application.itemfichaperfil.command.model.AgregarItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.port.in.AgregarItemFichaPerfilInputPort;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemFichaNoPropiaException;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemTipoDuplicadoException;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.ItemFichaPerfilAggregate;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.shared.message.FichasMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AgregarItemFichaPerfilUseCase implements AgregarItemFichaPerfilInputPort {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final EstudianteFichaPerfilQueryOutputPort estudianteFichaPerfilQueryOutputPort;
    private final ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public UUID ejecutar(AgregarItemFichaPerfilCommand agregarItemFichaPerfilCommand) {
        if (!fichaPerfilOutputPort.existsById(agregarItemFichaPerfilCommand.fichaPerfilId())) {
            throw new FichaPerfilNoEncontradaException(agregarItemFichaPerfilCommand.fichaPerfilId());
        }

        if (!estudianteFichaPerfilQueryOutputPort.existePorEstudianteYFicha(agregarItemFichaPerfilCommand
                .estudianteId(), agregarItemFichaPerfilCommand.fichaPerfilId())) {
            throw new ItemFichaNoPropiaException(agregarItemFichaPerfilCommand.fichaPerfilId());
        }

        if (itemFichaPerfilOutputPort.existsPorFichaYTipoItem(agregarItemFichaPerfilCommand.fichaPerfilId(),
                agregarItemFichaPerfilCommand.tipoItemCode())) {
            throw new ItemTipoDuplicadoException(agregarItemFichaPerfilCommand.tipoItemCode());
        }

        var itemFichaPerfilAggregate = ItemFichaPerfilAggregate.crear(
                agregarItemFichaPerfilCommand.fichaPerfilId(),
                agregarItemFichaPerfilCommand.tipoItemCode(),
                agregarItemFichaPerfilCommand.contenido()
        );

        itemFichaPerfilOutputPort.guardar(itemFichaPerfilAggregate);

        log.info(
                FichasMessages.ItemFichaPerfil.LOG_AGREGADO,
                itemFichaPerfilAggregate.getId(),
                itemFichaPerfilAggregate.getFichaPerfilId(),
                itemFichaPerfilAggregate.getTipoItem()
        );

        return itemFichaPerfilAggregate.getId();
    }
}
