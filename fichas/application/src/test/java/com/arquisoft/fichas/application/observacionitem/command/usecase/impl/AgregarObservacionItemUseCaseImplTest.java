package com.arquisoft.fichas.application.observacionitem.command.usecase.impl;

import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.finder.FichaPerfilDelItemFinder;
import com.arquisoft.fichas.application.observacionitem.command.finder.ObservacionesIgualesEnRevisionFinder;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.ObservacionItemOutputPort;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.entity.ObservacionItemEntity;
import com.arquisoft.fichas.application.observacionitem.command.validator.AgregarObservacionItemValidator;
import com.arquisoft.fichas.application.revisionitem.command.finder.RevisionItemFinder;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPerteneceAsesorException;
import com.arquisoft.fichas.domain.observacionitem.AgregacionObservacionItemDomain;
import com.arquisoft.fichas.domain.observacionitem.ObservacionItemDomain;
import com.arquisoft.fichas.domain.observacionitem.event.ObservacionItemAgregadaEvent;
import com.arquisoft.fichas.domain.observacionitem.exception.ObservacionItemDuplicadaException;
import com.arquisoft.fichas.domain.revisionitem.RevisionItemDomain;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemCerradaException;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemNoEncontradoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarObservacionItemUseCaseImplTest {

    @Mock
    private RevisionItemFinder revisionItemFinder;

    @Mock
    private FichaPerfilDelItemFinder fichaPerfilDelItemFinder;

    @Mock
    private FichaPerfilFinder fichaPerfilFinder;

    @Mock
    private ObservacionesIgualesEnRevisionFinder observacionesIgualesEnRevisionFinder;

    @Mock
    private AgregarObservacionItemValidator agregarObservacionItemValidator;

    @Mock
    private ObservacionItemOutputPort observacionItemOutputPort;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AgregarObservacionItemUseCaseImpl agregarObservacionItemUseCase;

    private final UUID revisionItemId = UUID.randomUUID();
    private final UUID itemId = UUID.randomUUID();
    private final UUID fichaPerfilId = UUID.randomUUID();
    private final UUID asesorFicha = UUID.randomUUID();
    private final UUID otroAsesor = UUID.randomUUID();

    @Test
    void debeAgregarObservacion_cuandoDatosValidos() {
        // Arrange
        var entrada = entradaValida();
        var revisionItem = RevisionItemDomain.reconstruir(revisionItemId, itemId, EstadoRevision.NUEVA, Instant.now());
        stubFinders(entrada, revisionItem, fichaPerfilId, asesorFicha, 0L);

        // Act
        var resultado = agregarObservacionItemUseCase.ejecutar(entrada);

        // Assert — resultado y presupuesto de I/O (una sola llamada por finder)
        assertThat(resultado).isEqualTo(entrada.getObservacionItem().getId());
        verify(revisionItemFinder, times(1)).obtener(revisionItemId);
        verify(fichaPerfilDelItemFinder, times(1)).obtener(itemId);
        verify(fichaPerfilFinder, times(1)).obtener(fichaPerfilId);
        verify(observacionesIgualesEnRevisionFinder, times(1)).obtener(entrada);
        verify(agregarObservacionItemValidator)
                .validar(entrada, true, "NUEVA", fichaPerfilId, asesorFicha, 0L);

        var entityCaptor = ArgumentCaptor.forClass(ObservacionItemEntity.class);
        verify(observacionItemOutputPort).registrarObservacion(entityCaptor.capture());
        assertThat(entityCaptor.getValue().id()).isEqualTo(entrada.getObservacionItem().getId());
        assertThat(entityCaptor.getValue().revisionItem()).isEqualTo(revisionItemId);
        assertThat(entityCaptor.getValue().observacion()).isEqualTo("Observación válida");
        assertThat(entityCaptor.getValue().estadoObservacionRevision()).isEqualTo("PENDIENTE");

        var eventCaptor = ArgumentCaptor.forClass(ObservacionItemAgregadaEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getObservacionItemId()).isEqualTo(entrada.getObservacionItem().getId());
        assertThat(eventCaptor.getValue().getRevisionItemId()).isEqualTo(revisionItemId);
        assertThat(eventCaptor.getValue().getObservacion()).isEqualTo("Observación válida");
        assertThat(eventCaptor.getValue().getEstadoObservacionRevisionId()).isEqualTo("PENDIENTE");
        assertThat(eventCaptor.getValue().getEstadoObservacionRevisionNombre()).isEqualTo("Pendiente");

        // Assert — orden: finders -> validator -> persistencia -> evento
        InOrder inOrderVerifier = inOrder(revisionItemFinder, fichaPerfilDelItemFinder, fichaPerfilFinder,
                observacionesIgualesEnRevisionFinder, agregarObservacionItemValidator,
                observacionItemOutputPort, eventPublisher);
        inOrderVerifier.verify(revisionItemFinder).obtener(revisionItemId);
        inOrderVerifier.verify(fichaPerfilDelItemFinder).obtener(itemId);
        inOrderVerifier.verify(fichaPerfilFinder).obtener(fichaPerfilId);
        inOrderVerifier.verify(observacionesIgualesEnRevisionFinder).obtener(entrada);
        inOrderVerifier.verify(agregarObservacionItemValidator)
                .validar(entrada, true, "NUEVA", fichaPerfilId, asesorFicha, 0L);
        inOrderVerifier.verify(observacionItemOutputPort).registrarObservacion(any());
        inOrderVerifier.verify(eventPublisher).publish(any());
    }

    @Test
    void debeLanzarRevisionItemNoEncontrada_cuandoRevisionNoExiste() {
        // Arrange — el Finder vuelve VACIO: estadoRevisionId e item degradan a sus centinelas,
        // sin lanzar NoSuchElementException
        var entrada = entradaValida();
        stubFinders(entrada, RevisionItemDomain.VACIO, UtilUUID.obtenerUUIDPorDefecto(),
                UtilUUID.obtenerUUIDPorDefecto(), 0L);
        doThrow(new RevisionItemNoEncontradoException(revisionItemId))
                .when(agregarObservacionItemValidator)
                .validar(entrada, false, EstadoRevision.VACIO.getId(),
                        UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(), 0L);

        // Act & Assert
        assertThatThrownBy(() -> agregarObservacionItemUseCase.ejecutar(entrada))
                .isInstanceOf(RevisionItemNoEncontradoException.class);

        verify(fichaPerfilDelItemFinder).obtener(UtilUUID.obtenerUUIDPorDefecto());
        verify(observacionItemOutputPort, never()).registrarObservacion(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeLanzarRevisionItemCerrada_cuandoRevisionEstaCerrada() {
        // Arrange
        var entrada = entradaValida();
        var revisionItem = RevisionItemDomain.reconstruir(revisionItemId, itemId, EstadoRevision.CERRADA, Instant.now());
        stubFinders(entrada, revisionItem, fichaPerfilId, asesorFicha, 0L);
        doThrow(new RevisionItemCerradaException(revisionItemId))
                .when(agregarObservacionItemValidator)
                .validar(entrada, true, "CERRADA", fichaPerfilId, asesorFicha, 0L);

        // Act & Assert
        assertThatThrownBy(() -> agregarObservacionItemUseCase.ejecutar(entrada))
                .isInstanceOf(RevisionItemCerradaException.class);

        verify(observacionItemOutputPort, never()).registrarObservacion(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeLanzarFichaNoPerteneceAsesor_cuandoAsesorNoEsPropietario() {
        // Arrange
        var entrada = entradaValida();
        var revisionItem = RevisionItemDomain.reconstruir(revisionItemId, itemId, EstadoRevision.NUEVA, Instant.now());
        stubFinders(entrada, revisionItem, fichaPerfilId, otroAsesor, 0L);
        doThrow(new FichaNoPerteneceAsesorException(fichaPerfilId, asesorFicha))
                .when(agregarObservacionItemValidator)
                .validar(entrada, true, "NUEVA", fichaPerfilId, otroAsesor, 0L);

        // Act & Assert
        assertThatThrownBy(() -> agregarObservacionItemUseCase.ejecutar(entrada))
                .isInstanceOf(FichaNoPerteneceAsesorException.class);

        verify(observacionItemOutputPort, never()).registrarObservacion(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeLanzarObservacionItemDuplicada_cuandoTextoYaExiste() {
        // Arrange
        var entrada = entradaValida();
        var revisionItem = RevisionItemDomain.reconstruir(revisionItemId, itemId, EstadoRevision.NUEVA, Instant.now());
        stubFinders(entrada, revisionItem, fichaPerfilId, asesorFicha, 1L);
        doThrow(new ObservacionItemDuplicadaException(revisionItemId, entrada.getObservacion()))
                .when(agregarObservacionItemValidator)
                .validar(entrada, true, "NUEVA", fichaPerfilId, asesorFicha, 1L);

        // Act & Assert
        assertThatThrownBy(() -> agregarObservacionItemUseCase.ejecutar(entrada))
                .isInstanceOf(ObservacionItemDuplicadaException.class);

        verify(observacionItemOutputPort, never()).registrarObservacion(any());
        verify(eventPublisher, never()).publish(any());
    }

    private void stubFinders(AgregacionObservacionItemDomain entrada, RevisionItemDomain revisionItem,
                              UUID fichaPerfilResuelta, UUID asesorDeLaFicha, long observacionesIguales) {
        when(revisionItemFinder.obtener(entrada.getRevisionItem())).thenReturn(revisionItem);
        when(fichaPerfilDelItemFinder.obtener(revisionItem.getItem())).thenReturn(fichaPerfilResuelta);
        var ficha = UtilUUID.obtenerUUIDPorDefecto().equals(fichaPerfilResuelta)
                ? FichaPerfilDomain.VACIO
                : FichaPerfilDomain.reconstruir(fichaPerfilResuelta, "Sistema de gestión", asesorDeLaFicha);
        when(fichaPerfilFinder.obtener(fichaPerfilResuelta)).thenReturn(ficha);
        when(observacionesIgualesEnRevisionFinder.obtener(entrada)).thenReturn(observacionesIguales);
    }

    private AgregacionObservacionItemDomain entradaValida() {
        var observacionItem = ObservacionItemDomain.crear(revisionItemId, "Observación válida");
        return AgregacionObservacionItemDomain.crear(observacionItem, asesorFicha);
    }
}
