package com.arquisoft.fichas.application.itemfichaperfil.command;

import com.arquisoft.fichas.application.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.application.estadofichaperfil.query.port.out.EstadoFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.query.port.out.FichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.itemfichaperfil.command.model.ModificarItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.port.in.ModificarItemFichaPerfilInputPort;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemFichaNoPropiaException;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemNoEncontradoException;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.shared.message.FichasMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ModificarItemFichaPerfilUseCase implements ModificarItemFichaPerfilInputPort {

    private final ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;
    private final FichaPerfilQueryOutputPort fichaPerfilQueryOutputPort;
    private final EstadoFichaPerfilQueryOutputPort estadoFichaPerfilQueryOutputPort;

    @Override
    public void ejecutar(ModificarItemFichaPerfilCommand command) {
        if (!itemFichaPerfilOutputPort.existsById(command.itemId())) {
            throw new ItemNoEncontradoException(command.itemId());
        }

        var item = itemFichaPerfilOutputPort.buscarPorId(command.itemId())
                .orElseThrow(() -> new ItemNoEncontradoException(command.itemId()));

        if (!fichaPerfilQueryOutputPort.esEstudiantePropietario(item.getFichaPerfilId(), command.estudianteId())) {
            throw new ItemFichaNoPropiaException(item.getFichaPerfilId());
        }

        var estadoActual = estadoFichaPerfilQueryOutputPort.obtenerEstadoActual(item.getFichaPerfilId())
                .orElseThrow(() -> new FichaPerfilNoEncontradaException(item.getFichaPerfilId()));

        item.modificarContenido(command.contenido(), estadoActual);

        itemFichaPerfilOutputPort.guardar(item);

        log.info(FichasMessages.ItemFichaPerfil.LOG_MODIFICADO, command.itemId());
    }
}
