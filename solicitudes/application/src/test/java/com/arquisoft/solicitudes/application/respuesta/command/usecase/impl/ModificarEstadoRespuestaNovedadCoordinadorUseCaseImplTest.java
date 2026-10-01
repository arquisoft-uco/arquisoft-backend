package com.arquisoft.solicitudes.application.respuesta.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.solicitudes.application.respuesta.command.finder.DatosRespuestaFinder;
import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.RespuestaOutputPort;
import com.arquisoft.solicitudes.application.respuesta.command.validator.ModificarEstadoRespuestaNovedadCoordinadorValidator;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosSolicitudFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosUsuarioFinder;
import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import com.arquisoft.solicitudes.domain.respuesta.ModificacionEstadoRespuestaNovedadCoordinadorDomain;
import com.arquisoft.solicitudes.domain.respuesta.event.SolicitudNovedadCoordinadorEstadoModificadoEvent;
import com.arquisoft.solicitudes.domain.respuesta.exception.EstadoRespuestaNoResolutivoException;
import com.arquisoft.solicitudes.domain.respuesta.exception.RespuestaNoEnRevisionException;
import com.arquisoft.solicitudes.domain.respuesta.exception.RespuestaNoEncontradaException;
import com.arquisoft.solicitudes.domain.respuesta.model.ResumenRespuesta;
import com.arquisoft.solicitudes.domain.solicitud.exception.DestinatarioNoEncontradoException;
import com.arquisoft.solicitudes.domain.solicitud.exception.RemitenteNoEncontradoException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEncontradaException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEsDestinatarioException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideException;
import com.arquisoft.solicitudes.domain.solicitud.model.ResumenSolicitud;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModificarEstadoRespuestaNovedadCoordinadorUseCaseImplTest {

    @Mock private DatosSolicitudFinder datosSolicitudFinder;
    @Mock private DatosRespuestaFinder datosRespuestaFinder;
    @Mock private DatosUsuarioFinder datosUsuarioFinder;
    @Mock private RespuestaOutputPort respuestaOutputPort;
    @Mock private ModificarEstadoRespuestaNovedadCoordinadorValidator validator;
    @Mock private EventPublisher eventPublisher;
    @Mock private AppLogger logger;

    private ModificarEstadoRespuestaNovedadCoordinadorUseCaseImpl useCase;

    private UUID solicitud;
    private UUID coordinadorUsuario;
    private UUID remitenteUsuario;
    private ModificacionEstadoRespuestaNovedadCoordinadorDomain entrada;
    private ResumenSolicitud resumenSolicitud;
    private ResumenRespuesta resumenRespuesta;
    private UsuarioDomain remitente;
    private UsuarioDomain coordinador;

    @BeforeEach
    void setUp() {
        useCase = new ModificarEstadoRespuestaNovedadCoordinadorUseCaseImpl(
                datosSolicitudFinder, datosRespuestaFinder, datosUsuarioFinder,
                respuestaOutputPort, validator, eventPublisher, logger);

        solicitud = UUID.randomUUID();
        coordinadorUsuario = UUID.randomUUID();
        remitenteUsuario = UUID.randomUUID();
        entrada = ModificacionEstadoRespuestaNovedadCoordinadorDomain.crear(
                solicitud, coordinadorUsuario, "APROBADA");
        resumenSolicitud = new ResumenSolicitud(
                solicitud, remitenteUsuario, coordinadorUsuario,
                TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId());
        resumenRespuesta = new ResumenRespuesta(solicitud, "EN_REVISION");
        remitente = UsuarioDomain.reconstruir(
                remitenteUsuario, "EST-1", "Ana Estudiante", "ana@uco.edu.co", Instant.now());
        coordinador = UsuarioDomain.reconstruir(
                coordinadorUsuario, "COO-1", "Pedro Coordinador", "pedro@uco.edu.co", Instant.now());
    }

    private void stubFlujoValido() {
        when(datosSolicitudFinder.obtener(solicitud)).thenReturn(resumenSolicitud);
        when(datosRespuestaFinder.obtener(solicitud)).thenReturn(resumenRespuesta);
        when(datosUsuarioFinder.obtener(remitenteUsuario)).thenReturn(remitente);
        when(datosUsuarioFinder.obtener(coordinadorUsuario)).thenReturn(coordinador);
    }

    @Test
    void debeActualizarElEstadoYPublicarElEventoConElPresupuestoDeIO_cuandoElFlujoEsValido() {
        // Arrange
        stubFlujoValido();

        // Act
        useCase.ejecutar(entrada);

        // Assert — flujo principal
        verify(respuestaOutputPort).actualizarEstadoPorSolicitud(solicitud, "APROBADA");

        var eventCaptor = ArgumentCaptor.forClass(SolicitudNovedadCoordinadorEstadoModificadoEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        var evento = eventCaptor.getValue();
        assertThat(evento.getSolicitudId()).isEqualTo(solicitud);
        assertThat(evento.getNuevoEstado()).isEqualTo("APROBADA");
        assertThat(evento.getNuevoEstadoNombre()).isEqualTo(EstadoRespuesta.APROBADA.getNombre());
        assertThat(evento.getRemitenteNombre()).isEqualTo("Ana Estudiante");
        assertThat(evento.getRemitenteEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(evento.getCoordinadorNombre()).isEqualTo("Pedro Coordinador");

        // Assert — presupuesto de I/O: un viaje por finder en el camino feliz
        verify(datosSolicitudFinder, times(1)).obtener(solicitud);
        verify(datosRespuestaFinder, times(1)).obtener(solicitud);
        verify(datosUsuarioFinder, times(1)).obtener(remitenteUsuario);
        verify(datosUsuarioFinder, times(1)).obtener(coordinadorUsuario);

        // Assert — logs
        verify(logger).info(eq(RespuestaKey.LOG_MODIFICANDO_ESTADO), eq(solicitud), eq(coordinadorUsuario));
        verify(logger).debug(eq(RespuestaKey.LOG_VERIFICACION_MODIFICACION_ESTADO), eq(true), eq(true));
        verify(logger).info(eq(RespuestaKey.LOG_ESTADO_MODIFICADO), eq(solicitud), eq("APROBADA"));
    }

    @Test
    void debeConsultarTodoAntesDeValidarYValidarAntesDeActualizarYPublicar_cuandoElFlujoEsValido() {
        // Arrange
        stubFlujoValido();

        // Act
        useCase.ejecutar(entrada);

        // Assert
        var inOrder = inOrder(datosSolicitudFinder, datosRespuestaFinder, datosUsuarioFinder,
                validator, respuestaOutputPort, eventPublisher);
        inOrder.verify(datosSolicitudFinder).obtener(solicitud);
        inOrder.verify(datosRespuestaFinder).obtener(solicitud);
        inOrder.verify(datosUsuarioFinder).obtener(remitenteUsuario);
        inOrder.verify(datosUsuarioFinder).obtener(coordinadorUsuario);
        inOrder.verify(validator).validar(entrada, resumenSolicitud, resumenRespuesta, remitente, coordinador);
        inOrder.verify(respuestaOutputPort).actualizarEstadoPorSolicitud(solicitud, "APROBADA");
        inOrder.verify(eventPublisher).publish(any());
    }

    @Test
    void debePasarLosResumenesVaciosAlValidator_cuandoLaSolicitudNoExiste() {
        // Arrange
        when(datosSolicitudFinder.obtener(solicitud)).thenReturn(ResumenSolicitud.VACIO);
        when(datosRespuestaFinder.obtener(solicitud)).thenReturn(ResumenRespuesta.VACIO);
        when(datosUsuarioFinder.obtener(ResumenSolicitud.VACIO.remitenteUsuario()))
                .thenReturn(UsuarioDomain.VACIO);
        doThrow(new SolicitudNoEncontradaException(solicitud)).when(validator).validar(
                entrada, ResumenSolicitud.VACIO, ResumenRespuesta.VACIO,
                UsuarioDomain.VACIO, UsuarioDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(SolicitudNoEncontradaException.class);

        verify(logger).debug(eq(RespuestaKey.LOG_VERIFICACION_MODIFICACION_ESTADO), eq(false), eq(false));
        verify(respuestaOutputPort, never()).actualizarEstadoPorSolicitud(any(), any());
        verify(eventPublisher, never()).publish(any());
    }

    static Stream<RuntimeException> excepcionesDelValidator() {
        return Stream.of(
                new RemitenteNoEncontradoException(UUID.randomUUID()),
                new DestinatarioNoEncontradoException(UUID.randomUUID()),
                new SolicitudTipoNoCoincideException(UUID.randomUUID()),
                new SolicitudNoEsDestinatarioException(UUID.randomUUID()),
                new RespuestaNoEncontradaException(UUID.randomUUID()),
                new RespuestaNoEnRevisionException(UUID.randomUUID()),
                new EstadoRespuestaNoResolutivoException(UUID.randomUUID(), "EN_REVISION"));
    }

    @ParameterizedTest
    @MethodSource("excepcionesDelValidator")
    void debeAbortarSinActualizarNiPublicar_cuandoElValidatorRechazaPorReglaDeNegocio(
            RuntimeException excepcion) {
        // Arrange
        stubFlujoValido();
        doThrow(excepcion).when(validator)
                .validar(entrada, resumenSolicitud, resumenRespuesta, remitente, coordinador);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada)).isSameAs(excepcion);

        verify(respuestaOutputPort, never()).actualizarEstadoPorSolicitud(any(), any());
        verify(eventPublisher, never()).publish(any());
    }
}
