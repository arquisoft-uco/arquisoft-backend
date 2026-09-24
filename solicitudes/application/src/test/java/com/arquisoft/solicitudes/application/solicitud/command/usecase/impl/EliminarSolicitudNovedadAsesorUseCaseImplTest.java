package com.arquisoft.solicitudes.application.solicitud.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosSolicitudFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.SolicitudTieneRespuestasFinder;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.SolicitudOutputPort;
import com.arquisoft.solicitudes.application.solicitud.command.validator.EliminarSolicitudNovedadAsesorValidator;
import com.arquisoft.solicitudes.domain.solicitud.EliminacionSolicitudNovedadAsesorDomain;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EliminarSolicitudNovedadAsesorUseCaseImplTest {

    @Mock private SolicitudOutputPort solicitudOutputPort;
    @Mock private DatosSolicitudFinder datosSolicitudFinder;
    @Mock private SolicitudTieneRespuestasFinder solicitudTieneRespuestasFinder;
    @Mock private EliminarSolicitudNovedadAsesorValidator validator;
    @Mock private AppLogger logger;

    private EliminarSolicitudNovedadAsesorUseCaseImpl useCase;

    private UUID solicitud;
    private UUID remitenteUsuario;
    private EliminacionSolicitudNovedadAsesorDomain entrada;

    @BeforeEach
    void setUp() {
        useCase = new EliminarSolicitudNovedadAsesorUseCaseImpl(
                solicitudOutputPort, datosSolicitudFinder, solicitudTieneRespuestasFinder,
                validator, logger);

        solicitud = UUID.randomUUID();
        remitenteUsuario = UUID.randomUUID();
        entrada = EliminacionSolicitudNovedadAsesorDomain.crear(solicitud, remitenteUsuario);
    }

    private void stubSolicitudPropiaSinRespuestas() {
        when(datosSolicitudFinder.obtener(solicitud)).thenReturn(new ResumenSolicitud(
                solicitud, remitenteUsuario, UUID.randomUUID(),
                TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId()));
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
        verify(datosSolicitudFinder, times(1)).obtener(solicitud);
        verify(solicitudTieneRespuestasFinder, times(1)).obtener(solicitud);
        verify(logger).info(eq(SolicitudKey.LOG_ELIMINANDO_ASESOR), eq(solicitud), eq(remitenteUsuario));
        verify(logger).info(eq(SolicitudKey.LOG_ELIMINADA_ASESOR), eq(solicitud));
    }

    @Test
    void debeAbortarSinEliminar_cuandoElValidatorLanza() {
        // Arrange
        stubSolicitudPropiaSinRespuestas();
        doThrow(new SolicitudConRespuestasException(solicitud))
                .when(validator).validar(any(), anyBoolean(), any(), any(), any(), anyBoolean());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(SolicitudConRespuestasException.class);

        verify(solicitudOutputPort, never()).eliminar(any());
        verify(logger, never()).info(eq(SolicitudKey.LOG_ELIMINADA_ASESOR), any());
    }

    @Test
    void debePasarLosDatosProyectadosAlValidator_cuandoLaSolicitudExiste() {
        // Arrange — el remitente de la solicitud difiere del actor del JWT
        var remitenteDeLaFila = UUID.randomUUID();
        when(datosSolicitudFinder.obtener(solicitud)).thenReturn(new ResumenSolicitud(
                solicitud, remitenteDeLaFila, UUID.randomUUID(),
                TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId()));
        when(solicitudTieneRespuestasFinder.obtener(solicitud)).thenReturn(false);

        // Act
        useCase.ejecutar(entrada);

        // Assert
        verify(validator).validar(
                eq(solicitud), eq(true), eq(remitenteDeLaFila),
                eq(TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId()), eq(remitenteUsuario), eq(false));
    }

    @Test
    void debePasarLosValoresPorDefectoAlValidator_cuandoLaSolicitudNoExiste() {
        // Arrange
        when(datosSolicitudFinder.obtener(solicitud)).thenReturn(ResumenSolicitud.VACIO);
        when(solicitudTieneRespuestasFinder.obtener(solicitud)).thenReturn(false);

        // Act
        useCase.ejecutar(entrada);

        // Assert
        verify(validator).validar(
                eq(solicitud), eq(false), eq(UtilUUID.obtenerUUIDPorDefecto()),
                eq(UtilTexto.VACIO), eq(remitenteUsuario), eq(false));
    }

    @Test
    void debeConsultarValidarYEliminarEnOrden_cuandoElFlujoEsValido() {
        // Arrange
        stubSolicitudPropiaSinRespuestas();

        // Act
        useCase.ejecutar(entrada);

        // Assert
        var orden = inOrder(datosSolicitudFinder, solicitudTieneRespuestasFinder,
                validator, solicitudOutputPort);
        orden.verify(datosSolicitudFinder).obtener(solicitud);
        orden.verify(solicitudTieneRespuestasFinder).obtener(solicitud);
        orden.verify(validator).validar(any(), anyBoolean(), any(), any(), any(), anyBoolean());
        orden.verify(solicitudOutputPort).eliminar(solicitud);
    }
}
