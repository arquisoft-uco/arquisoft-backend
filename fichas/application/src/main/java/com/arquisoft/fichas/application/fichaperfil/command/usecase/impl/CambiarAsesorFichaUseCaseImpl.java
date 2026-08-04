package com.arquisoft.fichas.application.fichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.asesorficha.query.port.out.AsesorFichaQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.command.model.CambiarAsesorFichaCommand;
import com.arquisoft.fichas.application.fichaperfil.command.usecase.CambiarAsesorFichaUseCase;
import com.arquisoft.fichas.application.fichaperfil.command.validator.CambiarAsesorFichaValidator;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.event.AsesorFichaCambiadoEvent;
import com.arquisoft.fichas.domain.fichaperfil.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
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
    private final AsesorFichaQueryOutputPort asesorFichaQueryOutputPort;
    private final CambiarAsesorFichaValidator cambiarAsesorFichaValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;
    private final MessageCatalog catalog;

    @Override
    public void ejecutar(CambiarAsesorFichaCommand entrada) {
        UUID fichaPerfil =  entrada.fichaPerfil();
        UUID nuevoAsesorFicha = entrada.nuevoAsesorFicha();

        FichaPerfilAggregate ficha = FichaPerfilAggregate.cambiarAsesorFicha(
                fichaPerfil,
                nuevoAsesorFicha
        );

        // Llevar a un validator, esto lo pueden usar más use case
        var fichaActual = fichaPerfilOutputPort.buscarPorId(fichaPerfil)
                .orElseThrow(() -> new FichaPerfilNoEncontradaException(entrada.fichaPerfil()));

        cambiarAsesorFichaValidator.validar(ficha, fichaActual.getAsesorFicha());

        // Llevar a un validator, esto lo pueden usar más use case
        var asesorFichaContacto = asesorFichaQueryOutputPort.buscarContactoPorId(entrada.nuevoAsesorFicha())
                .orElseThrow(() -> new AsesorFichaNoEncontradoException(entrada.nuevoAsesorFicha()));

        FichaPerfilAggregate fichaActualizada = FichaPerfilAggregate.reconstruir(
                fichaActual.getId(), fichaActual.getTituloProyecto(), nuevoAsesorFicha);

        fichaActualizada.publishEvent(new AsesorFichaCambiadoEvent(fichaActualizada.getId(), fichaActualizada.getTituloProyecto(),
                        asesorFichaContacto.id(), asesorFichaContacto.nombre(), asesorFichaContacto.email()));

        fichaPerfilOutputPort.guardar(fichaActualizada);

        fichaActualizada.drainUnPublishedEvents().forEach(eventPublisher::publish);

        logger.info(catalog.obtener(FichasKeys.FichaPerfil.LOG_ASESOR_CAMBIADO), fichaActualizada.getId(), entrada.nuevoAsesorFicha());
    }
}
