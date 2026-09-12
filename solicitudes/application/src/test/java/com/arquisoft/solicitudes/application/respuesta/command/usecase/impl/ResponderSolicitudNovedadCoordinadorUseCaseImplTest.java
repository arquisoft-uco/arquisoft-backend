package com.arquisoft.solicitudes.application.respuesta.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.RespuestaOutputPort;
import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.entity.RespuestaEntity;
import com.arquisoft.solicitudes.application.respuesta.command.validator.ResponderSolicitudNovedadCoordinadorValidator;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosSolicitudFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosUsuarioFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.SolicitudTieneRespuestasFinder;
import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaNovedadCoordinadorDomain;
import com.arquisoft.solicitudes.domain.respuesta.event.SolicitudNovedadCoordinadorRespondidaEvent;
import com.arquisoft.solicitudes.domain.respuesta.exception.SolicitudYaRespondidaException;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResponderSolicitudNovedadCoordinadorUseCaseImplTest {

    @Mock private DatosSolicitudFinder datosSolicitudFinder;
    @Mock private DatosUsuarioFinder datosUsuarioFinder;
    @Mock private SolicitudTieneRespuestasFinder solicitudTieneRespuestasFinder;
    @Mock private ResponderSolicitudNovedadCoordinadorValidator validator;
    @Mock private RespuestaOutputPort respuestaOutputPort;
    @Mock private EventPublisher eventPublisher;
    @Mock private AppLogger logger;

    private ResponderSolicitudNovedadCoordinadorUseCaseImpl useCase;

    private UUID solicitud;
    private UUID coordinadorUsuario;
    private UUID remitenteUsuario;
    private RespuestaNovedadCoordinadorDomain entrada;

    @BeforeEach
    void setUp() {
        useCase = new ResponderSolicitudNovedadCoordinadorUseCaseImpl(
                datosSolicitudFinder, datosUsuarioFinder, solicitudTieneRespuestasFinder,
                validator, respuestaOutputPort, eventPublisher, logger);

        solicitud = UUID.randomUUID();
        coordinadorUsuario = UUID.randomUUID();
        remitenteUsuario = UUID.randomUUID();
        entrada = RespuestaNovedadCoordinadorDomain.crear(solicitud, "No puedo asistir", coordinadorUsuario);
    }

    private void stubFlujoValido() {
        when(datosSolicitudFinder.obtener(solicitud)).thenReturn(Optional.of(new ResumenSolicitud(
                solicitud, remitenteUsuario, coordinadorUsuario,
                TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId())));
        when(solicitudTieneRespuestasFinder.obtener(solicitud)).thenReturn(false);
        when(datosUsuarioFinder.obtener(remitenteUsuario)).thenReturn(Optional.of(
                UsuarioDomain.reconstruir(remitenteUsuario, "EST-1", "Ana Estudiante", "ana@uco.edu.co")));
        when(datosUsuarioFinder.obtener(coordinadorUsuario)).thenReturn(Optional.of(
                UsuarioDomain.reconstruir(coordinadorUsuario, "COO-1", "Pedro Coordinador", "pedro@uco.edu.co")));
    }

    @Test
    void debeRegistrarLaRespuestaYPublicarElEvento_cuandoElFlujoEsValido() {
        // Arrange
        stubFlujoValido();

        // Act
        UUID id = useCase.ejecutar(entrada);

        // Assert
        ArgumentCaptor<RespuestaEntity> entityCaptor = ArgumentCaptor.forClass(RespuestaEntity.class);
        verify(respuestaOutputPort).registrar(entityCaptor.capture());
        RespuestaEntity persistida = entityCaptor.getValue();
        assertThat(persistida.solicitud()).isEqualTo(solicitud);
        assertThat(persistida.estadoRespuesta()).isEqualTo(EstadoRespuesta.EN_REVISION.getId());
        assertThat(persistida.contenido()).isEqualTo("No puedo asistir");
        assertThat(id).isEqualTo(persistida.id());

        ArgumentCaptor<SolicitudNovedadCoordinadorRespondidaEvent> eventCaptor =
                ArgumentCaptor.forClass(SolicitudNovedadCoordinadorRespondidaEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        SolicitudNovedadCoordinadorRespondidaEvent evento = eventCaptor.getValue();
        assertThat(evento.getSolicitudId()).isEqualTo(solicitud);
        assertThat(evento.getRemitenteNombre()).isEqualTo("Ana Estudiante");
        assertThat(evento.getRemitenteEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(evento.getCoordinadorNombre()).isEqualTo("Pedro Coordinador");
        assertThat(evento.getEstadoRespuesta()).isEqualTo("EN_REVISION");

        verify(logger).info(eq(RespuestaKey.LOG_RESPONDIENDO), eq(solicitud), eq(coordinadorUsuario));
        verify(logger).debug(eq(RespuestaKey.LOG_VERIFICACION_RESPUESTA), eq(true), eq(false));
        verify(logger).info(eq(RespuestaKey.LOG_RESPONDIDA), any());
    }

    @Test
    void debeAbortarSinRegistrarNiPublicar_cuandoElValidatorLanza() {
        // Arrange
        when(datosSolicitudFinder.obtener(solicitud)).thenReturn(Optional.of(new ResumenSolicitud(
                solicitud, remitenteUsuario, coordinadorUsuario,
                TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId())));
        when(solicitudTieneRespuestasFinder.obtener(solicitud)).thenReturn(false);
        doThrow(new SolicitudYaRespondidaException(solicitud))
                .when(validator).validar(any(), anyBoolean(), any(), any(), any(), anyBoolean());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(SolicitudYaRespondidaException.class);

        verify(respuestaOutputPort, never()).registrar(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debePasarLosValoresPorDefectoAlValidator_cuandoLaSolicitudNoExiste() {
        // Arrange
        when(datosSolicitudFinder.obtener(solicitud)).thenReturn(Optional.empty());
        when(solicitudTieneRespuestasFinder.obtener(solicitud)).thenReturn(false);
        doThrow(new SolicitudYaRespondidaException(solicitud))
                .when(validator).validar(any(), anyBoolean(), any(), any(), any(), anyBoolean());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(SolicitudYaRespondidaException.class);

        verify(validator).validar(eq(solicitud), eq(false), any(), any(), eq(coordinadorUsuario), eq(false));
        verify(respuestaOutputPort, never()).registrar(any());
    }

    @Test
    void debeConsultarValidarRegistrarYPublicarEnOrden_cuandoElFlujoEsValido() {
        // Arrange
        stubFlujoValido();

        // Act
        useCase.ejecutar(entrada);

        // Assert
        InOrder inOrder = inOrder(datosSolicitudFinder, solicitudTieneRespuestasFinder,
                validator, respuestaOutputPort, eventPublisher);
        inOrder.verify(datosSolicitudFinder).obtener(solicitud);
        inOrder.verify(solicitudTieneRespuestasFinder).obtener(solicitud);
        inOrder.verify(validator).validar(any(), anyBoolean(), any(), any(), any(), anyBoolean());
        inOrder.verify(respuestaOutputPort).registrar(any());
        inOrder.verify(eventPublisher).publish(any());
    }
}
