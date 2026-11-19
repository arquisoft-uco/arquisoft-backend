package com.arquisoft.fichas.application.observacionitem.command.usecase.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.ContactosDeFichaFinder;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.application.observacionitem.command.finder.ContextoObservacionItemFinder;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.ObservacionItemOutputPort;
import com.arquisoft.fichas.application.observacionitem.command.validator.ReabrirObservacionItemValidator;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import com.arquisoft.fichas.domain.estadoobservacionrevision.EstadoObservacionRevision;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ContactoEstudiante;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.observacionitem.ReaperturaObservacionItemDomain;
import com.arquisoft.fichas.domain.observacionitem.event.ObservacionItemReabiertaEvent;
import com.arquisoft.fichas.domain.observacionitem.exception.ObservacionItemNoCerradaException;
import com.arquisoft.fichas.domain.observacionitem.model.ContextoObservacionItem;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ObservacionItemKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReabrirObservacionItemUseCaseImplTest {

    @Mock
    private ContextoObservacionItemFinder contextoObservacionItemFinder;

    @Mock
    private FichaPerfilFinder fichaPerfilFinder;

    @Mock
    private ContactosDeFichaFinder contactosDeFichaFinder;

    @Mock
    private ReabrirObservacionItemValidator reabrirObservacionItemValidator;

    @Mock
    private ObservacionItemOutputPort observacionItemOutputPort;

    @Mock
    private RevisionItemOutputPort revisionItemOutputPort;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ReabrirObservacionItemUseCaseImpl reabrirObservacionItemUseCase;

    private final UUID observacionItem = UtilUUID.generarNuevoUUID();
    private final UUID asesorFicha = UtilUUID.generarNuevoUUID();
    private final UUID revisionItem = UtilUUID.generarNuevoUUID();
    private final UUID fichaPerfil = UtilUUID.generarNuevoUUID();

    @Test
    void debeReabrirObservacionYRevisionYPublicar_cuandoLaReaperturaProspera() {
        // Arrange
        var entrada = ReaperturaObservacionItemDomain.crear(observacionItem, asesorFicha);
        var contexto = contextoCerrado();
        var ficha = FichaPerfilDomain.crear("Sistema de gestión", asesorFicha);
        var estudiantes = List.of(
                new ContactoEstudiante("Ana Gomez", "ana.gomez@uco.edu.co"),
                new ContactoEstudiante("Luis Perez", "luis.perez@uco.edu.co"),
                new ContactoEstudiante("Eva Ruiz", "eva.ruiz@uco.edu.co"));
        when(contextoObservacionItemFinder.obtener(observacionItem)).thenReturn(contexto);
        when(fichaPerfilFinder.obtener(fichaPerfil)).thenReturn(ficha);
        when(contactosDeFichaFinder.obtener(fichaPerfil)).thenReturn(estudiantes);

        // Act
        reabrirObservacionItemUseCase.ejecutar(entrada);

        // Assert
        verify(contextoObservacionItemFinder, times(1)).obtener(observacionItem);
        verify(fichaPerfilFinder, times(1)).obtener(fichaPerfil);
        verify(contactosDeFichaFinder, times(1)).obtener(fichaPerfil);

        var orden = inOrder(contextoObservacionItemFinder, fichaPerfilFinder, contactosDeFichaFinder,
                reabrirObservacionItemValidator, observacionItemOutputPort, revisionItemOutputPort,
                eventPublisher, logger);
        orden.verify(contextoObservacionItemFinder).obtener(observacionItem);
        orden.verify(fichaPerfilFinder).obtener(fichaPerfil);
        orden.verify(contactosDeFichaFinder).obtener(fichaPerfil);
        orden.verify(reabrirObservacionItemValidator).validar(entrada, contexto, ficha);
        orden.verify(observacionItemOutputPort).actualizarEstado(observacionItem,
                EstadoObservacionRevision.CERRADO.getId(), EstadoObservacionRevision.EN_PROGRESO.getId());
        orden.verify(revisionItemOutputPort).actualizarEstado(revisionItem,
                EstadoRevision.CORRECCION_DISPONIBLE.getId(), EstadoRevision.EN_PROGRESO.getId());
        var captor = ArgumentCaptor.forClass(ObservacionItemReabiertaEvent.class);
        orden.verify(eventPublisher).publish(captor.capture());
        orden.verify(logger).info(eq(ObservacionItemKey.LOG_REABIERTA), eq(observacionItem));

        var evento = captor.getValue();
        assertThat(evento.getObservacionItemId()).isEqualTo(observacionItem);
        assertThat(evento.getRevisionItemId()).isEqualTo(revisionItem);
        assertThat(evento.getFichaPerfilId()).isEqualTo(ficha.getId());
        assertThat(evento.getTituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(evento.getEstudiantes()).containsExactlyElementsOf(estudiantes);
    }

    @Test
    void debeNoEscribirNiPublicar_cuandoElValidatorRechaza() {
        // Arrange
        var entrada = ReaperturaObservacionItemDomain.crear(observacionItem, asesorFicha);
        var contexto = contextoCerrado();
        var ficha = FichaPerfilDomain.crear("Sistema de gestión", asesorFicha);
        when(contextoObservacionItemFinder.obtener(observacionItem)).thenReturn(contexto);
        when(fichaPerfilFinder.obtener(fichaPerfil)).thenReturn(ficha);
        when(contactosDeFichaFinder.obtener(fichaPerfil)).thenReturn(List.of());
        doThrow(new ObservacionItemNoCerradaException(observacionItem, "EN_PROGRESO"))
                .when(reabrirObservacionItemValidator).validar(entrada, contexto, ficha);

        // Act & Assert
        assertThatThrownBy(() -> reabrirObservacionItemUseCase.ejecutar(entrada))
                .isInstanceOf(ObservacionItemNoCerradaException.class);

        verify(observacionItemOutputPort, never()).actualizarEstado(any(), anyString(), anyString());
        verify(revisionItemOutputPort, never()).actualizarEstado(any(), anyString(), anyString());
        verify(eventPublisher, never()).publish(any());
        verify(logger, never()).info(eq(ObservacionItemKey.LOG_REABIERTA), eq(observacionItem));
    }

    private ContextoObservacionItem contextoCerrado() {
        return new ContextoObservacionItem(
                revisionItem, EstadoRevision.CORRECCION_DISPONIBLE, fichaPerfil, asesorFicha,
                EstadoObservacionRevision.CERRADO);
    }
}
