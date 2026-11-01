package com.arquisoft.artefactos.application.observacion.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorObservacionFinder;
import com.arquisoft.artefactos.application.observacion.command.finder.ObservacionPorIdFinder;
import com.arquisoft.artefactos.application.observacion.command.secondaryport.ObservacionOutputPort;
import com.arquisoft.artefactos.application.observacion.command.validator.EliminarObservacionValidator;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionAsesorPorObservacionFinder;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.observacion.EliminacionObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.ObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.exception.ObservacionNoEncontradaException;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EliminarObservacionKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EliminarObservacionUseCaseImplTest {

    @Mock
    private RevisionAsesorPorObservacionFinder revisionAsesorPorObservacionFinder;
    @Mock
    private ObservacionPorIdFinder observacionPorIdFinder;
    @Mock
    private ArtefactoPorObservacionFinder artefactoPorObservacionFinder;
    @Mock
    private EliminarObservacionValidator validator;
    @Mock
    private ObservacionOutputPort observacionOutputPort;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private EliminarObservacionUseCaseImpl useCase;

    @Test
    void debeEliminar_cuandoValidacionPasa() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var eliminacion = EliminacionObservacionDomain.crear(observacion, asesor);
        var revision = RevisionAsesorDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                EstadoRevisionAsesor.PENDIENTE, UtilFecha.generarInstanteActual());
        var artefacto = ArtefactoDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), asesor, null);
        when(revisionAsesorPorObservacionFinder.obtener(observacion)).thenReturn(revision);
        var observacionDomain = ObservacionDomain.reconstruir(
                observacion, revision.getId(), "Texto", EstadoObservacion.NUEVA, UtilUUID.obtenerUUIDPorDefecto());
        when(observacionPorIdFinder.obtener(observacion)).thenReturn(observacionDomain);
        when(artefactoPorObservacionFinder.obtener(observacion)).thenReturn(artefacto);

        // Act
        useCase.ejecutar(eliminacion);

        // Assert
        var orden = inOrder(revisionAsesorPorObservacionFinder, observacionPorIdFinder, artefactoPorObservacionFinder,
                validator, observacionOutputPort, logger);
        orden.verify(logger).info(EliminarObservacionKey.LOG_ELIMINANDO, observacion, asesor);
        orden.verify(revisionAsesorPorObservacionFinder).obtener(observacion);
        orden.verify(observacionPorIdFinder).obtener(observacion);
        orden.verify(artefactoPorObservacionFinder).obtener(observacion);
        orden.verify(validator).validar(eliminacion, observacionDomain, revision, artefacto);
        orden.verify(observacionOutputPort).eliminar(observacion);
        orden.verify(logger).info(EliminarObservacionKey.LOG_ELIMINADA, observacion);
        verify(revisionAsesorPorObservacionFinder, times(1)).obtener(any());
        verify(artefactoPorObservacionFinder, times(1)).obtener(any());
    }

    @Test
    void noDebeEliminar_cuandoValidatorRechaza() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var eliminacion = EliminacionObservacionDomain.crear(observacion, UtilUUID.generarNuevoUUID());
        when(revisionAsesorPorObservacionFinder.obtener(observacion)).thenReturn(RevisionAsesorDomain.VACIO);
        when(observacionPorIdFinder.obtener(observacion)).thenReturn(ObservacionDomain.VACIO);
        when(artefactoPorObservacionFinder.obtener(observacion)).thenReturn(ArtefactoDomain.VACIO);
        doThrow(new ObservacionNoEncontradaException(observacion)).when(validator)
                .validar(any(), any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(eliminacion))
                .isInstanceOf(ObservacionNoEncontradaException.class);
        verifyNoInteractions(observacionOutputPort);
        verify(logger, never()).info(eq(EliminarObservacionKey.LOG_ELIMINADA), any());
    }

    @Test
    void debeDevolverLaOriginalASolucionada_cuandoEliminaUnaContinuacion() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var origen = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var eliminacion = EliminacionObservacionDomain.crear(observacion, asesor);
        var revision = RevisionAsesorDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                EstadoRevisionAsesor.PENDIENTE, UtilFecha.generarInstanteActual());
        var continuacion = ObservacionDomain.reconstruir(
                observacion, revision.getId(), "Texto", EstadoObservacion.NUEVA, origen);
        when(revisionAsesorPorObservacionFinder.obtener(observacion)).thenReturn(revision);
        when(observacionPorIdFinder.obtener(observacion)).thenReturn(continuacion);
        when(artefactoPorObservacionFinder.obtener(observacion)).thenReturn(ArtefactoDomain.VACIO);

        // Act
        useCase.ejecutar(eliminacion);

        // Assert
        var orden = inOrder(observacionOutputPort);
        orden.verify(observacionOutputPort).eliminar(observacion);
        orden.verify(observacionOutputPort).devolverEstado(origen, "REABIERTA", "SOLUCIONADA");
    }

    @Test
    void noDebeTocarOtraObservacion_cuandoNoEsContinuacion() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var eliminacion = EliminacionObservacionDomain.crear(observacion, UtilUUID.generarNuevoUUID());
        var revision = RevisionAsesorDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                EstadoRevisionAsesor.PENDIENTE, UtilFecha.generarInstanteActual());
        when(revisionAsesorPorObservacionFinder.obtener(observacion)).thenReturn(revision);
        when(observacionPorIdFinder.obtener(observacion)).thenReturn(ObservacionDomain.reconstruir(
                observacion, revision.getId(), "Texto", EstadoObservacion.NUEVA, UtilUUID.obtenerUUIDPorDefecto()));
        when(artefactoPorObservacionFinder.obtener(observacion)).thenReturn(ArtefactoDomain.VACIO);

        // Act
        useCase.ejecutar(eliminacion);

        // Assert
        verify(observacionOutputPort).eliminar(observacion);
        verify(observacionOutputPort, never()).devolverEstado(any(), any(), any());
    }
}
