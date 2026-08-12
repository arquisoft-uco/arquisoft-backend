package com.arquisoft.fichas.application.fichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.fichas.application.asesorficha.command.finder.AsesorFichaFinder;
import com.arquisoft.fichas.application.estadofichaperfil.command.finder.EstadoActualFichaPerfilFinder;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.application.fichaperfil.command.usecase.CambiarAsesorFichaUseCase;
import com.arquisoft.fichas.application.fichaperfil.command.validator.CambiarAsesorFichaValidator;
import com.arquisoft.fichas.domain.fichaperfil.CambioAsesorFichaDomain;
import com.arquisoft.fichas.domain.fichaperfil.event.AsesorFichaCambiadoEvent;
import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.FichaPerfilOutputPort;
import com.arquisoft.shared.events.EventPublisher;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.CatalogoMensajes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CambiarAsesorFichaUseCaseImpl implements CambiarAsesorFichaUseCase {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final FichaPerfilFinder fichaPerfilFinder;
    private final AsesorFichaFinder asesorFichaFinder;
    private final EstadoActualFichaPerfilFinder estadoActualFichaPerfilFinder;
    private final CambiarAsesorFichaValidator cambiarAsesorFichaValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;
    private final CatalogoMensajes catalogo;

    @Override
    public void ejecutar(CambioAsesorFichaDomain cambio) {
        UUID fichaPerfil = cambio.getFichaPerfil();
        UUID nuevoAsesorFicha = cambio.getNuevoAsesorFicha();

        var ficha = fichaPerfilFinder.obtener(fichaPerfil);
        var asesorFicha = asesorFichaFinder.obtener(nuevoAsesorFicha);
        var estadoActual = estadoActualFichaPerfilFinder.obtener(fichaPerfil);

        cambiarAsesorFichaValidator.validar(cambio, ficha, asesorFicha.isPresent(), estadoActual);

        var fichaActual = ficha.orElseThrow();
        var asesorFichaContacto = asesorFicha.orElseThrow();

        fichaPerfilOutputPort.actualizarAsesor(fichaPerfil, nuevoAsesorFicha);

        eventPublisher.publish(new AsesorFichaCambiadoEvent(fichaPerfil, fichaActual.getTituloProyecto(),
                asesorFichaContacto.id(), asesorFichaContacto.nombre(), asesorFichaContacto.email()));

        logger.info(catalogo.obtener(FichaPerfilKey.LOG_ASESOR_CAMBIADO), fichaPerfil, nuevoAsesorFicha);
    }
}
