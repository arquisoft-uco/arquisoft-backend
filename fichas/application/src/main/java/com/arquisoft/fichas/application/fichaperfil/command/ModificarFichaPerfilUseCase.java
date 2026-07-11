package com.arquisoft.fichas.application.fichaperfil.command;

import com.arquisoft.fichas.application.fichaperfil.command.model.ModificarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.command.port.in.ModificarFichaPerfilInputPort;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaNoEncontradaException;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaNoPropietarioException;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaTituloDuplicadoException;
import com.arquisoft.fichas.application.fichaperfil.query.port.out.FichaPerfilQueryOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.message.FichasMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ModificarFichaPerfilUseCase implements ModificarFichaPerfilInputPort {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final FichaPerfilQueryOutputPort fichaPerfilQueryOutputPort;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public void ejecutar(ModificarFichaPerfilCommand command) {
        if (!fichaPerfilQueryOutputPort.esEstudiantePropietario(
                command.fichaId(), command.estudianteId())) {
            throw new FichaNoPropietarioException(command.fichaId(), command.estudianteId());
        }

        var ficha = fichaPerfilOutputPort.buscarPorId(command.fichaId())
            .orElseThrow(() -> new FichaNoEncontradaException(command.fichaId()));

        if (!ficha.getTituloProyecto().equals(command.tituloProyecto())
                && fichaPerfilOutputPort.existsByTituloProyecto(command.tituloProyecto())) {
            throw new FichaTituloDuplicadoException(command.tituloProyecto());
        }

        ficha.actualizarTitulo(command.tituloProyecto());
        fichaPerfilOutputPort.guardar(ficha);

        log.info(FichasMessages.FichaPerfil.LOG_MODIFICADA, ficha.getId());
    }
}
