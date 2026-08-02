package com.arquisoft.fichas.application.itemfichaperfil.command;

import com.arquisoft.fichas.application.itemfichaperfil.command.model.AgregarItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.port.in.AgregarItemFichaPerfilUseCase;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.AgregarItemFichaPerfilValidator;
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
    private final AgregarItemFichaPerfilValidator agregarItemFichaPerfilValidator;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(AgregarItemFichaPerfilCommand entrada) {
        var item = ItemFichaPerfilAggregate.crear(
                entrada.fichaPerfil(),
                entrada.tipoItem(),
                entrada.contenido()
        );

        agregarItemFichaPerfilValidator.validar(item, entrada.estudiante());

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
