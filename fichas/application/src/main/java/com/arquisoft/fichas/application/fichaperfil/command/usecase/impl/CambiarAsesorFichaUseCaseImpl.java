package com.arquisoft.fichas.application.fichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.asesorficha.query.finder.AsesorContactoFinder;
import com.arquisoft.fichas.application.fichaperfil.command.usecase.CambiarAsesorFichaUseCase;
import com.arquisoft.fichas.application.fichaperfil.command.validator.CambiarAsesorFichaValidator;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.event.AsesorFichaCambiadoEvent;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.events.EventPublisher;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.shared.message.MessageCatalog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CambiarAsesorFichaUseCaseImpl implements CambiarAsesorFichaUseCase {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final FichaPerfilFinder fichaPerfilFinder;
    private final AsesorContactoFinder asesorContactoFinder;
    private final CambiarAsesorFichaValidator cambiarAsesorFichaValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;
    private final MessageCatalog catalog;

    @Override
    public void ejecutar(FichaPerfilDomain ficha) {
        UUID fichaPerfil = ficha.getId();
        UUID nuevoAsesorFicha = ficha.getAsesorFicha();

        var fichaActual = fichaPerfilFinder.obtener(fichaPerfil);

        cambiarAsesorFichaValidator.validar(ficha, fichaActual.getAsesorFicha());

        var asesorFichaContacto = asesorContactoFinder.obtener(nuevoAsesorFicha);

        FichaPerfilDomain fichaActualizada = FichaPerfilDomain.reconstruir(
                fichaActual.getId(), fichaActual.getTituloProyecto(), nuevoAsesorFicha);

        fichaActualizada.publishEvent(new AsesorFichaCambiadoEvent(fichaActualizada.getId(), fichaActualizada.getTituloProyecto(),
                        asesorFichaContacto.id(), asesorFichaContacto.nombre(), asesorFichaContacto.email()));

        fichaPerfilOutputPort.guardar(fichaActualizada);

        fichaActualizada.drainUnPublishedEvents().forEach(eventPublisher::publish);

        logger.info(catalog.obtener(FichasKeys.FichaPerfil.LOG_ASESOR_CAMBIADO), fichaActualizada.getId(), nuevoAsesorFicha);
    }
}
