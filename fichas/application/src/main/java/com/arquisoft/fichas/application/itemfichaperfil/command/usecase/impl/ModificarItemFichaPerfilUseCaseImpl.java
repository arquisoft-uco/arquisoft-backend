package com.arquisoft.fichas.application.itemfichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.finder.PertenenciaItemFichaPerfilFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.ItemFichaPerfilOutputPort;
import com.arquisoft.fichas.application.itemfichaperfil.command.usecase.ModificarItemFichaPerfilUseCase;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.ModificarItemFichaPerfilValidator;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.ObservacionItemOutputPort;
import com.arquisoft.fichas.application.revisionitem.command.finder.RevisionDelItemFinder;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import com.arquisoft.fichas.domain.estadoobservacionrevision.EstadoObservacionRevision;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.itemfichaperfil.ModificacionItemFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.model.ItemDeEstudiante;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarItemFichaPerfilUseCaseImpl implements ModificarItemFichaPerfilUseCase {

    private final ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;
    private final PertenenciaItemFichaPerfilFinder pertenenciaItemFichaPerfilFinder;
    private final RevisionDelItemFinder revisionDelItemFinder;
    private final ModificarItemFichaPerfilValidator modificarItemFichaPerfilValidator;
    private final ObservacionItemOutputPort observacionItemOutputPort;
    private final RevisionItemOutputPort revisionItemOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(ModificacionItemFichaPerfilDomain entrada) {
        logger.info(ItemFichaPerfilKey.LOG_MODIFICANDO,
                entrada.getItem(), entrada.getEstudiante());

        var pertenencia = pertenenciaItemFichaPerfilFinder.obtener(
                new ItemDeEstudiante(entrada.getItem(), entrada.getEstudiante()));
        var revision = revisionDelItemFinder.obtener(entrada.getItem());
        var estadoRevision = revision.getEstadoRevision();
        var itemExiste = !pertenencia.esVacio();

        logger.debug(ItemFichaPerfilKey.LOG_VERIFICACION_MODIFICAR,
                itemExiste, pertenencia.esPropietario(), pertenencia.fichaPerfil(), estadoRevision.getId());

        modificarItemFichaPerfilValidator.validar(entrada.getItem(), entrada.getEstudiante(),
                pertenencia.fichaPerfil(), itemExiste, pertenencia.esPropietario(),
                pertenencia.estadoActual());

        itemFichaPerfilOutputPort.actualizarContenido(entrada.getItem(), entrada.getContenido());

        if (estadoRevision.atiendeObservaciones()) {
            observacionItemOutputPort.actualizarEstadoPorRevision(revision.getId(),
                    EstadoObservacionRevision.PENDIENTE.getId(), EstadoObservacionRevision.EN_PROGRESO.getId());
        }

        if (estadoRevision.retornaAEnProgresoAlModificarItem()) {
            revisionItemOutputPort.actualizarEstado(revision.getId(),
                    estadoRevision.getId(), EstadoRevision.EN_PROGRESO.getId());
        }

        logger.info(ItemFichaPerfilKey.LOG_MODIFICADO, entrada.getItem());
    }
}
