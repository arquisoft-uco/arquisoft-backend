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
import com.arquisoft.solicitudes.domain.respuesta.model.ResumenRespuesta;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEncontradaException;
import com.arquisoft.solicitudes.domain.solicitud.model.ResumenSolicitud;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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
    }

    private void stubSolicitudYRespuestaValidas() {
        when(datosSolicitudFinder.obtener(solicitud)).thenReturn(Optional.of(new ResumenSolicitud(
                solicitud, remitenteUsuario, coordinadorUsuario,
                TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId())));
        when(datosRespuestaFinder.obtener(solicitud))
                .thenReturn(Optional.of(new ResumenRespuesta(solicitud, "EN_REVISION")));
    }

    private void stubFlujoValido() {
        stubSolicitudYRespuestaValidas();
        when(datosUsuarioFinder.obtener(remitenteUsuario)).thenReturn(Optional.of(
                UsuarioDomain.reconstruir(
                        remitenteUsuario, "EST-1", "Ana Estudiante", "ana@uco.edu.co", Instant.now())));
        when(datosUsuarioFinder.obtener(coordinadorUsuario)).thenReturn(Optional.of(
                UsuarioDomain.reconstruir(
                        coordinadorUsuario, "COO-1", "Pedro Coordinador", "pedro@uco.edu.co", Instant.now())));
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
    void debeAbortarSinActualizarNiPublicar_cuandoElValidatorLanza() {
        // Arrange
        stubSolicitudYRespuestaValidas();
        doThrow(new RespuestaNoEnRevisionException(solicitud))
                .when(validator).validar(any(), anyBoolean(), any(), any(), anyBoolean(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(RespuestaNoEnRevisionException.class);

        verify(respuestaOutputPort, never()).actualizarEstadoPorSolicitud(any(), any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debePasarLosValoresPorDefectoAlValidator_cuandoLaSolicitudNoExiste() {
        // Arrange
        when(datosSolicitudFinder.obtener(solicitud)).thenReturn(Optional.empty());
        when(datosRespuestaFinder.obtener(solicitud)).thenReturn(Optional.empty());
        doThrow(new SolicitudNoEncontradaException(solicitud))
                .when(validator).validar(any(), anyBoolean(), any(), any(), anyBoolean(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(SolicitudNoEncontradaException.class);

        verify(validator).validar(eq(entrada), eq(false), any(), any(), eq(false), any());
        verify(respuestaOutputPort, never()).actualizarEstadoPorSolicitud(any(), any());
    }

    @Test
    void debeLanzarEstadoRespuestaNoResolutivo_cuandoElValidatorRechazaElNuevoEstado() {
        // Arrange
        stubSolicitudYRespuestaValidas();
        doThrow(new EstadoRespuestaNoResolutivoException(solicitud, "APROBADA"))
                .when(validator).validar(any(), anyBoolean(), any(), any(), anyBoolean(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(EstadoRespuestaNoResolutivoException.class);

        verify(respuestaOutputPort, never()).actualizarEstadoPorSolicitud(any(), any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeConsultarValidarActualizarYPublicarEnOrden_cuandoElFlujoEsValido() {
        // Arrange
        stubFlujoValido();

        // Act
        useCase.ejecutar(entrada);

        // Assert
        InOrder inOrder = inOrder(datosSolicitudFinder, datosRespuestaFinder,
                validator, respuestaOutputPort, eventPublisher);
        inOrder.verify(datosSolicitudFinder).obtener(solicitud);
        inOrder.verify(datosRespuestaFinder).obtener(solicitud);
        inOrder.verify(validator).validar(any(), anyBoolean(), any(), any(), anyBoolean(), any());
        inOrder.verify(respuestaOutputPort).actualizarEstadoPorSolicitud(solicitud, "APROBADA");
        inOrder.verify(eventPublisher).publish(any());
    }
}
