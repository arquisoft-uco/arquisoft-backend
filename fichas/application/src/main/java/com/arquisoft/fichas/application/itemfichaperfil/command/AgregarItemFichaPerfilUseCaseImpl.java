package com.arquisoft.fichas.application.itemfichaperfil.command;

import com.arquisoft.fichas.application.fichaperfil.command.validator.FichaPerfilValidator;
import com.arquisoft.fichas.application.itemfichaperfil.command.model.AgregarItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.port.in.AgregarItemFichaPerfilUseCase;
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
public class AgregarItemFichaPerfilUseCaseImpl implements AgregarItemFichaPerfilUseCase {

    private final ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;
    private final FichaPerfilValidator fichaPerfilValidator;
    private final ItemFichaPerfilValidator itemFichaPerfilValidator;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(AgregarItemFichaPerfilCommand entrada) {
        fichaPerfilValidator.validarFichaExiste(entrada.fichaPerfil());
        itemFichaPerfilValidator.validarFichaPropia(entrada.fichaPerfil(), entrada.estudiante());
        itemFichaPerfilValidator.validarTipoNoDuplicado(entrada.fichaPerfil(), entrada.tipoItem());

        var item = ItemFichaPerfilAggregate.crear(
                entrada.fichaPerfil(),
                entrada.tipoItem(),
                entrada.contenido()
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
