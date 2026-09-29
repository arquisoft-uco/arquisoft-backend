package com.arquisoft.solicitudes.application.solicitud.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.solicitudes.application.destinatario.command.usecase.RegistrarDestinatarioUseCase;
import com.arquisoft.solicitudes.application.remitente.command.usecase.RegistrarRemitenteUseCase;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosUsuarioFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DestinatarioAsignadoFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.SolicitudDuplicadaFinder;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper.EnviarSolicitudCambioAsesorMapper;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudCambioAsesorCommand;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.SolicitudOutputPort;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.entity.SolicitudEntity;
import com.arquisoft.solicitudes.application.solicitud.command.validator.EnviarSolicitudCambioAsesorValidator;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudCambioAsesorDomain;
import com.arquisoft.solicitudes.domain.solicitud.event.SolicitudCambioAsesorEnviadaEvent;
import com.arquisoft.solicitudes.domain.solicitud.exception.DestinatarioNoAsignadoException;
import com.arquisoft.solicitudes.domain.solicitud.exception.DestinatarioNoEncontradoException;
import com.arquisoft.solicitudes.domain.solicitud.exception.RemitenteNoEncontradoException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudDuplicadaException;
import com.arquisoft.solicitudes.domain.solicitud.model.DisponibilidadSolicitud;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnviarSolicitudCambioAsesorUseCaseImplTest {

    @Mock private SolicitudOutputPort solicitudOutputPort;
    @Mock private RegistrarRemitenteUseCase registrarRemitenteUseCase;
    @Mock private RegistrarDestinatarioUseCase registrarDestinatarioUseCase;
    @Mock private DatosUsuarioFinder datosUsuarioFinder;
    @Mock private DestinatarioAsignadoFinder destinatarioAsignadoFinder;
    @Mock private SolicitudDuplicadaFinder solicitudDuplicadaFinder;
    @Mock private EnviarSolicitudCambioAsesorValidator validator;
    @Mock private EventPublisher eventPublisher;
    @Mock private AppLogger logger;

    private EnviarSolicitudCambioAsesorUseCaseImpl useCase;

    private EnvioSolicitudCambioAsesorDomain envio;

    private static UsuarioDomain replica(UUID id) {
        return UsuarioDomain.reconstruir(id, "ID-" + id, "Nombre " + id, id + "@uco.edu.co", Instant.now());
    }

    @BeforeEach
    void setUp() {
        useCase = new EnviarSolicitudCambioAsesorUseCaseImpl(
                solicitudOutputPort, registrarRemitenteUseCase, registrarDestinatarioUseCase, datosUsuarioFinder,
                destinatarioAsignadoFinder, solicitudDuplicadaFinder, validator, eventPublisher, logger);

        var command = EnviarSolicitudCambioAsesorCommand.crear(
                UUID.randomUUID(), UUID.randomUUID().toString(), "cambio de asesor");
        envio = EnviarSolicitudCambioAsesorMapper.toDomain(command);
    }

    @Test
    void debeRegistrarYPublicarElEvento_cuandoElFlujoEsValido() {
        // Arrange
        UUID remitenteFila = UUID.randomUUID();
        UUID destinatarioFila = UUID.randomUUID();
        var remitenteReplica = UsuarioDomain.reconstruir(
                envio.getRemitenteUsuario(), "EST-1", "Ana Estudiante", "ana@uco.edu.co", Instant.now());
        var destinatarioReplica = UsuarioDomain.reconstruir(
                envio.getDestinatarioUsuario(), "COO-1", "Pedro Coordinador", "pedro@uco.edu.co", Instant.now());
        when(datosUsuarioFinder.obtener(envio.getRemitenteUsuario()))
                .thenReturn(remitenteReplica);
        when(datosUsuarioFinder.obtener(envio.getDestinatarioUsuario()))
                .thenReturn(destinatarioReplica);
        when(destinatarioAsignadoFinder.obtener(any())).thenReturn(true);
        when(registrarRemitenteUseCase.ejecutar(envio.getRemitente())).thenReturn(remitenteFila);
        when(registrarDestinatarioUseCase.ejecutar(envio.getDestinatario())).thenReturn(destinatarioFila);
        when(solicitudDuplicadaFinder.obtener(any())).thenReturn(false);

        // Act
        UUID resultado = useCase.ejecutar(envio);

        // Assert
        assertThat(resultado).isNotNull();

        ArgumentCaptor<SolicitudEntity> entityCaptor = ArgumentCaptor.forClass(SolicitudEntity.class);
        verify(solicitudOutputPort).registrar(entityCaptor.capture());
        SolicitudEntity persistida = entityCaptor.getValue();
        assertThat(persistida.id()).isEqualTo(resultado);
        assertThat(persistida.remitente()).isEqualTo(remitenteFila);
        assertThat(persistida.destinatario()).isEqualTo(destinatarioFila);
        assertThat(persistida.mensajeSolicitud()).isEqualTo("cambio de asesor");
        assertThat(persistida.tipoSolicitud()).isEqualTo(TipoSolicitud.CAMBIO_DE_ASESOR.getId());

        ArgumentCaptor<SolicitudCambioAsesorEnviadaEvent> eventoCaptor =
                ArgumentCaptor.forClass(SolicitudCambioAsesorEnviadaEvent.class);
        verify(eventPublisher).publish(eventoCaptor.capture());
        SolicitudCambioAsesorEnviadaEvent evento = eventoCaptor.getValue();
        assertThat(evento.getSolicitudId()).isEqualTo(resultado);
        assertThat(evento.getTemaEvento()).isEqualTo(SolicitudCambioAsesorEnviadaEvent.EVENT_TOPIC);
        assertThat(evento.getRemitenteNombre()).isEqualTo("Ana Estudiante");
        assertThat(evento.getDestinatarioNombre()).isEqualTo("Pedro Coordinador");
        assertThat(evento.getDestinatarioEmail()).isEqualTo("pedro@uco.edu.co");
        assertThat(evento.getMensajeSolicitud()).isEqualTo("cambio de asesor");
    }

    @Test
    void debeUsarLosIdsQueDevuelvenLosUseCasesDeRegistro_alPersistirLaSolicitud() {
        // Arrange
        UUID remitenteId = UUID.randomUUID();
        UUID destinatarioId = UUID.randomUUID();
        when(datosUsuarioFinder.obtener(any())).thenReturn(replica(UUID.randomUUID()));
        when(destinatarioAsignadoFinder.obtener(any())).thenReturn(true);
        when(registrarRemitenteUseCase.ejecutar(envio.getRemitente())).thenReturn(remitenteId);
        when(registrarDestinatarioUseCase.ejecutar(envio.getDestinatario())).thenReturn(destinatarioId);
        when(solicitudDuplicadaFinder.obtener(any())).thenReturn(false);

        // Act
        useCase.ejecutar(envio);

        // Assert — el orquestador delega la resolucion del id, no decide si viene de una fila existente o nueva
        ArgumentCaptor<SolicitudEntity> captor = ArgumentCaptor.forClass(SolicitudEntity.class);
        verify(solicitudOutputPort).registrar(captor.capture());
        assertThat(captor.getValue().remitente()).isEqualTo(remitenteId);
        assertThat(captor.getValue().destinatario()).isEqualTo(destinatarioId);
    }

    @Test
    void debeLanzarYNoTocarLaEscritura_cuandoElRemitenteNoExiste() {
        // Arrange
        when(datosUsuarioFinder.obtener(envio.getRemitenteUsuario())).thenReturn(UsuarioDomain.VACIO);
        when(datosUsuarioFinder.obtener(envio.getDestinatarioUsuario()))
                .thenReturn(replica(envio.getDestinatarioUsuario()));
        doThrow(new RemitenteNoEncontradoException(envio.getRemitenteUsuario()))
                .when(validator).validarExistenciaUsuarios(any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(envio))
                .isInstanceOf(RemitenteNoEncontradoException.class);

        verify(solicitudOutputPort, never()).registrar(any());
        verify(eventPublisher, never()).publish(any());
        verifyNoInteractions(registrarRemitenteUseCase, registrarDestinatarioUseCase,
                destinatarioAsignadoFinder, solicitudDuplicadaFinder);
    }

    @Test
    void debeLanzarDestinatarioNoAsignado_cuandoElDestinatarioNoEsElResponsableDelEstudiante() {
        // Arrange
        when(datosUsuarioFinder.obtener(any())).thenReturn(replica(UUID.randomUUID()));
        when(destinatarioAsignadoFinder.obtener(any())).thenReturn(false);
        doThrow(new DestinatarioNoAsignadoException(
                envio.getDestinatarioUsuario(), envio.getRemitenteUsuario()))
                .when(validator).validarAsignacionDestinatario(any(), eq(false));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(envio))
                .isInstanceOf(DestinatarioNoAsignadoException.class);

        verify(solicitudOutputPort, never()).registrar(any());
        verify(eventPublisher, never()).publish(any());
        verifyNoInteractions(registrarRemitenteUseCase, registrarDestinatarioUseCase, solicitudDuplicadaFinder);
    }

    @Test
    void debeLanzarDestinatarioNoEncontrado_cuandoElDestinatarioNoExiste() {
        // Arrange
        when(datosUsuarioFinder.obtener(envio.getRemitenteUsuario()))
                .thenReturn(replica(envio.getRemitenteUsuario()));
        when(datosUsuarioFinder.obtener(envio.getDestinatarioUsuario())).thenReturn(UsuarioDomain.VACIO);
        doThrow(new DestinatarioNoEncontradoException(envio.getDestinatarioUsuario()))
                .when(validator).validarExistenciaUsuarios(any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(envio))
                .isInstanceOf(DestinatarioNoEncontradoException.class);

        verify(solicitudOutputPort, never()).registrar(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeLanzarSolicitudDuplicada_cuandoLaClaveYaExiste() {
        // Arrange
        when(datosUsuarioFinder.obtener(any())).thenReturn(replica(UUID.randomUUID()));
        when(destinatarioAsignadoFinder.obtener(any())).thenReturn(true);
        when(registrarRemitenteUseCase.ejecutar(any())).thenReturn(UUID.randomUUID());
        when(registrarDestinatarioUseCase.ejecutar(any())).thenReturn(UUID.randomUUID());
        when(solicitudDuplicadaFinder.obtener(any())).thenReturn(true);
        doThrow(new SolicitudDuplicadaException())
                .when(validator).validarUnicidad(any(DisponibilidadSolicitud.class));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(envio))
                .isInstanceOf(SolicitudDuplicadaException.class);

        verify(solicitudOutputPort, never()).registrar(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeConsultarValidarYPersistirEnOrden_cuandoElFlujoEsValido() {
        // Arrange
        when(datosUsuarioFinder.obtener(any())).thenReturn(replica(UUID.randomUUID()));
        when(destinatarioAsignadoFinder.obtener(any())).thenReturn(true);
        when(registrarRemitenteUseCase.ejecutar(any())).thenReturn(UUID.randomUUID());
        when(registrarDestinatarioUseCase.ejecutar(any())).thenReturn(UUID.randomUUID());
        when(solicitudDuplicadaFinder.obtener(any())).thenReturn(false);

        // Act
        useCase.ejecutar(envio);

        // Assert — existencia usuario -> asignacion -> get-or-create -> unicidad -> persistir -> publicar
        InOrder inOrder = inOrder(datosUsuarioFinder, validator, destinatarioAsignadoFinder,
                registrarRemitenteUseCase, solicitudDuplicadaFinder, solicitudOutputPort, eventPublisher);
        inOrder.verify(validator).validarExistenciaUsuarios(any(), any(), any());
        inOrder.verify(destinatarioAsignadoFinder).obtener(any());
        inOrder.verify(validator).validarAsignacionDestinatario(any(), anyBoolean());
        inOrder.verify(registrarRemitenteUseCase).ejecutar(any());
        inOrder.verify(solicitudDuplicadaFinder).obtener(any());
        inOrder.verify(validator).validarUnicidad(any());
        inOrder.verify(solicitudOutputPort).registrar(any());
        inOrder.verify(eventPublisher).publish(any());
    }
}
