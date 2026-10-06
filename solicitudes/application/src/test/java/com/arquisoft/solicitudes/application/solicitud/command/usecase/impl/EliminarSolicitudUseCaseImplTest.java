package com.arquisoft.solicitudes.application.solicitud.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosSolicitudFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.SolicitudTieneRespuestasFinder;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.SolicitudOutputPort;
import com.arquisoft.solicitudes.application.solicitud.command.validator.EliminarSolicitudValidator;
import com.arquisoft.solicitudes.domain.solicitud.EliminacionSolicitudDomain;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudConRespuestasException;
import com.arquisoft.solicitudes.domain.solicitud.model.ResumenSolicitud;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EliminarSolicitudUseCaseImplTest {

    @Mock private SolicitudOutputPort solicitudOutputPort;
    @Mock private DatosSolicitudFinder datosSolicitudFinder;
    @Mock private SolicitudTieneRespuestasFinder solicitudTieneRespuestasFinder;
    @Mock private EliminarSolicitudValidator validator;
    @Mock private AppLogger logger;

    private EliminarSolicitudUseCaseImpl useCase;

    private UUID solicitud;
    private UUID remitenteUsuario;
    private EliminacionSolicitudDomain entrada;

    @BeforeEach
    void setUp() {
        useCase = new EliminarSolicitudUseCaseImpl(
                solicitudOutputPort, datosSolicitudFinder, solicitudTieneRespuestasFinder, validator, logger);

        solicitud = UUID.randomUUID();
        remitenteUsuario = UUID.randomUUID();
        entrada = EliminacionSolicitudDomain.crear(
                solicitud, remitenteUsuario, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);
    }

    private ResumenSolicitud resumenPropio() {
        return new ResumenSolicitud(solicitud, remitenteUsuario, UUID.randomUUID(),
                TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId());
    }

    private void stubSolicitudPropiaSinRespuestas() {
        when(datosSolicitudFinder.obtener(solicitud)).thenReturn(resumenPropio());
        when(solicitudTieneRespuestasFinder.obtener(solicitud)).thenReturn(false);
    }

    @Test
    void debeEliminar_cuandoElFlujoEsValido() {
        // Arrange
        stubSolicitudPropiaSinRespuestas();

        // Act
        useCase.ejecutar(entrada);

        // Assert
        verify(solicitudOutputPort).eliminar(solicitud);
    }

    @Test
    void debeLoguearConElTipoDeLaSolicitud_cuandoElFlujoEsValido() {
        // Arrange
        stubSolicitudPropiaSinRespuestas();
        var tipo = TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId();

        // Act
        useCase.ejecutar(entrada);

        // Assert
        verify(logger).info(eq(SolicitudKey.LOG_ELIMINANDO), eq(tipo), eq(solicitud), eq(remitenteUsuario));
        verify(logger).debug(eq(SolicitudKey.LOG_VERIFICACION_ELIMINACION), eq(true), eq(false));
        verify(logger).info(eq(SolicitudKey.LOG_ELIMINADA), eq(tipo), eq(solicitud));
    }

    @Test
    void debeEliminarYLoguearElTipoDelAsesor_cuandoElObjetoDeAccionEsDeNovedadParaElAsesor() {
        // Arrange
        var entradaAsesor = EliminacionSolicitudDomain.crear(
                solicitud, remitenteUsuario, TipoSolicitud.NOVEDAD_PARA_EL_ASESOR);
        when(datosSolicitudFinder.obtener(solicitud)).thenReturn(new ResumenSolicitud(
                solicitud, remitenteUsuario, UUID.randomUUID(), TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId()));
        when(solicitudTieneRespuestasFinder.obtener(solicitud)).thenReturn(false);

        // Act
        useCase.ejecutar(entradaAsesor);

        // Assert
        verify(solicitudOutputPort).eliminar(solicitud);
        verify(logger).info(eq(SolicitudKey.LOG_ELIMINANDO), eq(TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId()),
                eq(solicitud), eq(remitenteUsuario));
    }

    @Test
    void debeAbortarSinEliminar_cuandoElValidatorLanza() {
        // Arrange
        stubSolicitudPropiaSinRespuestas();
        doThrow(new SolicitudConRespuestasException(solicitud))
                .when(validator).validar(any(), any(), anyBoolean());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(SolicitudConRespuestasException.class);

        verify(solicitudOutputPort, never()).eliminar(any());
        verify(logger, never()).info(eq(SolicitudKey.LOG_ELIMINADA), any(), any());
    }

    @Test
    void debePasarElResumenYElObjetoDeAccionAlValidator_cuandoLaSolicitudExiste() {
        // Arrange — el remitente de la solicitud difiere del actor del JWT
        var resumen = new ResumenSolicitud(solicitud, UUID.randomUUID(), UUID.randomUUID(),
                TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId());
        when(datosSolicitudFinder.obtener(solicitud)).thenReturn(resumen);
        when(solicitudTieneRespuestasFinder.obtener(solicitud)).thenReturn(false);

        // Act
        useCase.ejecutar(entrada);

        // Assert
        verify(validator).validar(eq(entrada), eq(resumen), eq(false));
    }

    @Test
    void debePasarElResumenVacioAlValidator_cuandoLaSolicitudNoExiste() {
        // Arrange
        when(datosSolicitudFinder.obtener(solicitud)).thenReturn(ResumenSolicitud.VACIO);
        when(solicitudTieneRespuestasFinder.obtener(solicitud)).thenReturn(false);

        // Act
        useCase.ejecutar(entrada);

        // Assert
        verify(validator).validar(eq(entrada), eq(ResumenSolicitud.VACIO), eq(false));
        verify(logger).debug(eq(SolicitudKey.LOG_VERIFICACION_ELIMINACION), eq(false), eq(false));
    }

    @Test
    void debeConsultarValidarYEliminarEnOrden_cuandoElFlujoEsValido() {
        // Arrange
        stubSolicitudPropiaSinRespuestas();

        // Act
        useCase.ejecutar(entrada);

        // Assert
        var inOrder = inOrder(datosSolicitudFinder, solicitudTieneRespuestasFinder,
                validator, solicitudOutputPort);
        inOrder.verify(datosSolicitudFinder).obtener(solicitud);
        inOrder.verify(solicitudTieneRespuestasFinder).obtener(solicitud);
        inOrder.verify(validator).validar(any(), any(), anyBoolean());
        inOrder.verify(solicitudOutputPort).eliminar(solicitud);
    }
}
