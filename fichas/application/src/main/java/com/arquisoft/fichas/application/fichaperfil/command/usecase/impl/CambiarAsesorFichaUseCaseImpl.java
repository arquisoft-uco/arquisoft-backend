package com.arquisoft.fichas.application.fichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.estadofichaperfil.query.port.out.EstadoFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.command.model.CambiarAsesorFichaCommand;
import com.arquisoft.fichas.application.fichaperfil.command.usecase.CambiarAsesorFichaUseCase;
import com.arquisoft.fichas.application.fichaperfil.command.validator.CambiarAsesorFichaValidator;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
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
    private final CambiarAsesorFichaValidator cambiarAsesorFichaValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(CambiarAsesorFichaCommand entrada) {
        var ficha = fichaPerfilOutputPort.buscarPorId(entrada.fichaPerfil())
                .orElseThrow(() -> new FichaPerfilNoEncontradaException(entrada.fichaPerfil()));

        cambiarAsesorFichaValidator.validar(entrada.nuevoAsesorFicha());

        var estadoActual = estadoFichaPerfilQueryOutputPort.obtenerEstadoActual(entrada.fichaPerfil())
                .orElseThrow(() -> new FichaPerfilNoEncontradaException(entrada.fichaPerfil()));

        ficha.cambiarAsesorFicha(entrada.nuevoAsesorFicha(), estadoActual);

        fichaPerfilOutputPort.guardar(ficha);

        logger.info(FichasMessages.FichaPerfil.LOG_ASESOR_CAMBIADO, ficha.getId(), entrada.nuevoAsesorFicha());
    }
}
