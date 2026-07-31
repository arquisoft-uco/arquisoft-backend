package com.arquisoft.fichas.application.itemfichaperfil.command;

import com.arquisoft.fichas.application.estadofichaperfil.query.port.out.EstadoFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.application.itemfichaperfil.command.model.ModificarItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.ItemFichaPerfilValidator;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemNoEncontradoException;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.FichasMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarItemFichaPerfilUseCase {

    private final ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;
    private final EstadoFichaPerfilQueryOutputPort estadoFichaPerfilQueryOutputPort;
    private final ItemFichaPerfilValidator itemFichaPerfilValidator;
    private final AppLogger logger;

    public void ejecutar(ModificarItemFichaPerfilCommand command) {
        var item = itemFichaPerfilOutputPort.buscarPorId(command.item())
                .orElseThrow(() -> new ItemNoEncontradoException(command.item()));

        itemFichaPerfilValidator.validarFichaPropia(item.getFichaPerfilId(), command.estudiante());

        var estadoActual = estadoFichaPerfilQueryOutputPort.obtenerEstadoActual(item.getFichaPerfilId())
                .orElseThrow(() -> new FichaPerfilNoEncontradaException(item.getFichaPerfilId()));

        item.modificarContenido(command.contenido(), estadoActual);

        itemFichaPerfilOutputPort.guardar(item);

        logger.info(FichasMessages.ItemFichaPerfil.LOG_MODIFICADO, command.item());
    }
}
