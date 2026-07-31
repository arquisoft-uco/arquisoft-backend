package com.arquisoft.fichas.application.itemfichaperfil.command;

import com.arquisoft.fichas.application.fichaperfil.command.validator.FichaPerfilValidator;
import com.arquisoft.fichas.application.itemfichaperfil.command.model.AgregarItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.ItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.ItemFichaPerfilAggregate;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.FichasMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarItemFichaPerfilUseCase {

    private final ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;
    private final FichaPerfilValidator fichaPerfilValidator;
    private final ItemFichaPerfilValidator itemFichaPerfilValidator;
    private final AppLogger logger;

    public UUID ejecutar(AgregarItemFichaPerfilCommand command) {
        fichaPerfilValidator.validarFichaExiste(command.fichaPerfil());
        itemFichaPerfilValidator.validarFichaPropia(command.fichaPerfil(), command.estudiante());
        itemFichaPerfilValidator.validarTipoNoDuplicado(command.fichaPerfil(), command.tipoItem());

        var item = ItemFichaPerfilAggregate.crear(
                command.fichaPerfil(),
                command.tipoItem(),
                command.contenido()
        );

        itemFichaPerfilOutputPort.guardar(item);

        logger.info(
                FichasMessages.ItemFichaPerfil.LOG_AGREGADO,
                item.getId(),
                item.getFichaPerfilId(),
                item.getTipoItem()
        );

        return item.getId();
    }
}
