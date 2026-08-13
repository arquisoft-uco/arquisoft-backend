package com.arquisoft.fichas.application.fichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.fichas.application.asesorficha.command.finder.AsesorFichaFinder;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.mapper.AsesorFichaMapper;
import com.arquisoft.fichas.application.estadofichaperfil.command.finder.EstadoActualFichaPerfilFinder;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.application.fichaperfil.command.usecase.CambiarAsesorFichaUseCase;
import com.arquisoft.fichas.application.fichaperfil.command.validator.CambiarAsesorFichaValidator;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.CambioAsesorFichaDomain;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
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

        var ficha = fichaPerfilFinder.obtener(fichaPerfil).orElse(FichaPerfilDomain.VACIO);
        var asesorFicha = asesorFichaFinder.obtener(nuevoAsesorFicha).orElse(AsesorFichaDomain.VACIO);
        var estadoActual = estadoActualFichaPerfilFinder.obtener(fichaPerfil)
                .orElse(EstadoFichaPerfilDomain.VACIO);

        cambiarAsesorFichaValidator.validar(cambio, ficha, asesorFicha, estadoActual);

        fichaPerfilOutputPort.actualizarAsesor(
                fichaPerfil, AsesorFichaMapper.toReferencia(nuevoAsesorFicha));

        eventPublisher.publish(new AsesorFichaCambiadoEvent(fichaPerfil, ficha.getTituloProyecto(),
                asesorFicha.getId(), asesorFicha.getNombre(), asesorFicha.getEmail()));

        logger.info(catalogo.obtener(FichaPerfilKey.LOG_ASESOR_CAMBIADO), fichaPerfil, nuevoAsesorFicha);
    }
}
