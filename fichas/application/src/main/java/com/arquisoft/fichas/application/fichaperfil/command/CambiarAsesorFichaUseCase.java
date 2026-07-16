package com.arquisoft.fichas.application.fichaperfil.command;

import com.arquisoft.fichas.application.estadofichaperfil.query.port.out.EstadoFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.command.model.CambiarAsesorFichaCommand;
import com.arquisoft.fichas.application.fichaperfil.command.port.in.CambiarAsesorFichaInputPort;
import com.arquisoft.fichas.application.fichaperfil.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.application.asesorficha.query.port.out.AsesorFichaQueryOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.message.FichasMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(transactionManager = "fichasTransactionManager")
public class CambiarAsesorFichaUseCase implements CambiarAsesorFichaInputPort {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final AsesorFichaQueryOutputPort asesorFichaQueryOutputPort;
    private final EstadoFichaPerfilQueryOutputPort estadoFichaPerfilQueryOutputPort;

    @Override
    public void ejecutar(CambiarAsesorFichaCommand command) {
        var ficha = fichaPerfilOutputPort.buscarPorId(command.fichaPerfilId())
                .orElseThrow(() -> new FichaPerfilNoEncontradaException(command.fichaPerfilId()));

        if (!asesorFichaQueryOutputPort.existsById(command.nuevoAsesorFichaId())) {
            throw new AsesorFichaNoEncontradoException(command.nuevoAsesorFichaId());
        }

        var estadoActual = estadoFichaPerfilQueryOutputPort.obtenerEstadoActual(command.fichaPerfilId())
                .orElseThrow(() -> new FichaPerfilNoEncontradaException(command.fichaPerfilId()));

        ficha.cambiarAsesorFicha(command.nuevoAsesorFichaId(), estadoActual);

        fichaPerfilOutputPort.guardar(ficha);

        log.info(FichasMessages.FichaPerfil.LOG_ASESOR_CAMBIADO, ficha.getId(), command.nuevoAsesorFichaId());
    }
}
