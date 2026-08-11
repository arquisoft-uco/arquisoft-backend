package com.arquisoft.fichas.application.fichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.fichas.application.asesorficha.query.finder.AsesorFichaFinder;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.application.fichaperfil.command.usecase.CambiarAsesorFichaUseCase;
import com.arquisoft.fichas.application.fichaperfil.command.validator.CambiarAsesorFichaValidator;
import com.arquisoft.fichas.domain.fichaperfil.CambioAsesorFichaDomain;
import com.arquisoft.fichas.domain.fichaperfil.event.AsesorFichaCambiadoEvent;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
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
    private final CambiarAsesorFichaValidator cambiarAsesorFichaValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;
    private final CatalogoMensajes catalogo;

    @Override
    public void ejecutar(CambioAsesorFichaDomain cambio) {
        UUID fichaPerfil = cambio.getFichaPerfil();
        UUID nuevoAsesorFicha = cambio.getNuevoAsesorFicha();

        var fichaActual = fichaPerfilFinder.obtener(fichaPerfil);

        cambiarAsesorFichaValidator.validar(cambio, fichaActual.getAsesorFicha());

        var asesorFichaContacto = asesorFichaFinder.obtener(nuevoAsesorFicha);

        fichaPerfilOutputPort.actualizarAsesor(fichaPerfil, nuevoAsesorFicha);

        eventPublisher.publish(new AsesorFichaCambiadoEvent(fichaPerfil, fichaActual.getTituloProyecto(),
                asesorFichaContacto.id(), asesorFichaContacto.nombre(), asesorFichaContacto.email()));

        logger.info(catalogo.obtener(FichaPerfilKey.LOG_ASESOR_CAMBIADO), fichaPerfil, nuevoAsesorFicha);
    }
}
