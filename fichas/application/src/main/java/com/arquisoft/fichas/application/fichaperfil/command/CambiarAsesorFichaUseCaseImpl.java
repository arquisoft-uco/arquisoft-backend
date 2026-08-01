package com.arquisoft.fichas.application.fichaperfil.command;

import com.arquisoft.fichas.application.estadofichaperfil.query.port.out.EstadoFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.command.model.CambiarAsesorFichaCommand;
import com.arquisoft.fichas.application.fichaperfil.command.port.in.CambiarAsesorFichaUseCase;
import com.arquisoft.fichas.application.fichaperfil.command.validator.FichaPerfilValidator;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.FichasMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CambiarAsesorFichaUseCaseImpl implements CambiarAsesorFichaUseCase {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final EstadoFichaPerfilQueryOutputPort estadoFichaPerfilQueryOutputPort;
    private final FichaPerfilValidator fichaPerfilValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(CambiarAsesorFichaCommand command) {
        var ficha = fichaPerfilOutputPort.buscarPorId(command.fichaPerfil())
                .orElseThrow(() -> new FichaPerfilNoEncontradaException(command.fichaPerfil()));

        fichaPerfilValidator.validarAsesorExiste(command.nuevoAsesorFicha());

        var estadoActual = estadoFichaPerfilQueryOutputPort.obtenerEstadoActual(command.fichaPerfil())
                .orElseThrow(() -> new FichaPerfilNoEncontradaException(command.fichaPerfil()));

        ficha.cambiarAsesorFicha(command.nuevoAsesorFicha(), estadoActual);

        fichaPerfilOutputPort.guardar(ficha);

        logger.info(FichasMessages.FichaPerfil.LOG_ASESOR_CAMBIADO, ficha.getId(), command.nuevoAsesorFicha());
    }
}
