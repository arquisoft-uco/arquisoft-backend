package com.arquisoft.fichas.application.observacionitem.command.usecase.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.ContactosDeFichaFinder;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.application.observacionitem.command.finder.ContextoObservacionItemFinder;
import com.arquisoft.fichas.application.observacionitem.command.finder.ObservacionesSinCerrarEnRevisionFinder;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.ObservacionItemOutputPort;
import com.arquisoft.fichas.application.observacionitem.command.validator.CerrarObservacionItemValidator;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import com.arquisoft.fichas.domain.estadoobservacionrevision.EstadoObservacionRevision;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ContactoEstudiante;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.observacionitem.CierreObservacionItemDomain;
import com.arquisoft.fichas.domain.observacionitem.exception.ObservacionItemNoEnProgresoException;
import com.arquisoft.fichas.domain.observacionitem.model.ContextoObservacionItem;
import com.arquisoft.fichas.domain.revisionitem.event.RevisionItemCerradaEvent;
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
class CerrarObservacionItemUseCaseImplTest {

    @Mock
    private ContextoObservacionItemFinder contextoObservacionItemFinder;

    @Mock
    private ObservacionesSinCerrarEnRevisionFinder observacionesSinCerrarEnRevisionFinder;

    @Mock
    private FichaPerfilFinder fichaPerfilFinder;

    @Mock
    private ContactosDeFichaFinder contactosDeFichaFinder;

    @Mock
    private CerrarObservacionItemValidator cerrarObservacionItemValidator;

    @Mock
    private ObservacionItemOutputPort observacionItemOutputPort;

    @Mock
    private RevisionItemOutputPort revisionItemOutputPort;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private CerrarObservacionItemUseCaseImpl cerrarObservacionItemUseCase;

    private final UUID observacionItem = UtilUUID.generarNuevoUUID();
    private final UUID asesorFicha = UtilUUID.generarNuevoUUID();
    private final UUID revisionItem = UtilUUID.generarNuevoUUID();
    private final UUID fichaPerfil = UtilUUID.generarNuevoUUID();

    @Test
    void debeCerrarSoloLaObservacion_cuandoQuedanOtrasSinCerrar() {
        // Arrange
        var entrada = CierreObservacionItemDomain.crear(observacionItem, asesorFicha);
        var contexto = contextoEnProgreso();
        var ficha = FichaPerfilDomain.crear("Sistema de gestión", asesorFicha);
        var estudiantes = List.of(new ContactoEstudiante("Ana Gomez", "ana.gomez@uco.edu.co"));
        stubConsultas(contexto, 3L, ficha, estudiantes);

        // Act
        cerrarObservacionItemUseCase.ejecutar(entrada);

        // Assert: una llamada por finder, finders antes del validator, y solo se cierra la observacion
        verify(contextoObservacionItemFinder, times(1)).obtener(observacionItem);
        verify(observacionesSinCerrarEnRevisionFinder, times(1)).obtener(observacionItem);
        verify(fichaPerfilFinder, times(1)).obtener(fichaPerfil);
        verify(contactosDeFichaFinder, times(1)).obtener(fichaPerfil);
        verify(revisionItemOutputPort, never()).actualizarEstado(any(), anyString(), anyString());
        verify(eventPublisher, never()).publish(any());
        verify(logger).info(eq(ObservacionItemKey.LOG_CERRADA), eq(observacionItem));

        var orden = inOrder(contextoObservacionItemFinder, observacionesSinCerrarEnRevisionFinder,
                fichaPerfilFinder, contactosDeFichaFinder, cerrarObservacionItemValidator, observacionItemOutputPort);
        orden.verify(contextoObservacionItemFinder).obtener(observacionItem);
        orden.verify(observacionesSinCerrarEnRevisionFinder).obtener(observacionItem);
        orden.verify(fichaPerfilFinder).obtener(fichaPerfil);
        orden.verify(contactosDeFichaFinder).obtener(fichaPerfil);
        orden.verify(cerrarObservacionItemValidator).validar(entrada, contexto, ficha);
        orden.verify(observacionItemOutputPort).actualizarEstado(observacionItem,
                EstadoObservacionRevision.EN_PROGRESO.getId(), EstadoObservacionRevision.CERRADO.getId());
    }

    @Test
    void debeCerrarObservacionYRevisionYPublicar_cuandoEsLaUltima() {
        // Arrange
        var entrada = CierreObservacionItemDomain.crear(observacionItem, asesorFicha);
        var contexto = contextoEnProgreso();
        var ficha = FichaPerfilDomain.crear("Sistema de gestión", asesorFicha);
        var estudiantes = List.of(
                new ContactoEstudiante("Ana Gomez", "ana.gomez@uco.edu.co"),
                new ContactoEstudiante("Luis Perez", "luis.perez@uco.edu.co"));
        stubConsultas(contexto, 0L, ficha, estudiantes);

        // Act
        cerrarObservacionItemUseCase.ejecutar(entrada);

        // Assert
        var orden = inOrder(cerrarObservacionItemValidator, observacionItemOutputPort, revisionItemOutputPort,
                eventPublisher, logger);
        orden.verify(cerrarObservacionItemValidator).validar(entrada, contexto, ficha);
        orden.verify(observacionItemOutputPort).actualizarEstado(observacionItem,
                EstadoObservacionRevision.EN_PROGRESO.getId(), EstadoObservacionRevision.CERRADO.getId());
        orden.verify(revisionItemOutputPort).actualizarEstado(revisionItem,
                EstadoRevision.CORRECCION_DISPONIBLE.getId(), EstadoRevision.CERRADA.getId());
        var captor = ArgumentCaptor.forClass(RevisionItemCerradaEvent.class);
        orden.verify(eventPublisher).publish(captor.capture());
        orden.verify(logger).info(eq(ObservacionItemKey.LOG_CERRADA), eq(observacionItem));

        var evento = captor.getValue();
        assertThat(evento.getRevisionItemId()).isEqualTo(revisionItem);
        assertThat(evento.getFichaPerfilId()).isEqualTo(ficha.getId());
        assertThat(evento.getTituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(evento.getEstudiantes()).containsExactlyElementsOf(estudiantes);
    }

    @Test
    void debeNoEscribirNiPublicar_cuandoElValidatorRechaza() {
        // Arrange
        var entrada = CierreObservacionItemDomain.crear(observacionItem, asesorFicha);
        var contexto = contextoEnProgreso();
        var ficha = FichaPerfilDomain.crear("Sistema de gestión", asesorFicha);
        stubConsultas(contexto, 0L, ficha, List.of());
        doThrow(new ObservacionItemNoEnProgresoException(observacionItem, "PENDIENTE"))
                .when(cerrarObservacionItemValidator).validar(entrada, contexto, ficha);

        // Act & Assert
        assertThatThrownBy(() -> cerrarObservacionItemUseCase.ejecutar(entrada))
                .isInstanceOf(ObservacionItemNoEnProgresoException.class);

        verify(observacionItemOutputPort, never()).actualizarEstado(any(), anyString(), anyString());
        verify(revisionItemOutputPort, never()).actualizarEstado(any(), anyString(), anyString());
        verify(eventPublisher, never()).publish(any());
        verify(logger, never()).info(eq(ObservacionItemKey.LOG_CERRADA), eq(observacionItem));
    }

    private ContextoObservacionItem contextoEnProgreso() {
        return new ContextoObservacionItem(
                revisionItem, EstadoRevision.CORRECCION_DISPONIBLE, fichaPerfil, asesorFicha,
                EstadoObservacionRevision.EN_PROGRESO);
    }

    private void stubConsultas(ContextoObservacionItem contexto, long otrasSinCerrar, FichaPerfilDomain ficha,
                               List<ContactoEstudiante> estudiantes) {
        when(contextoObservacionItemFinder.obtener(observacionItem)).thenReturn(contexto);
        when(observacionesSinCerrarEnRevisionFinder.obtener(observacionItem)).thenReturn(otrasSinCerrar);
        when(fichaPerfilFinder.obtener(fichaPerfil)).thenReturn(ficha);
        when(contactosDeFichaFinder.obtener(fichaPerfil)).thenReturn(estudiantes);
    }
}
