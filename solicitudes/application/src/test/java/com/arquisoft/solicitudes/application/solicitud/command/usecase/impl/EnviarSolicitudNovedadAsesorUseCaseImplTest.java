package com.arquisoft.solicitudes.application.solicitud.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.solicitudes.application.destinatario.command.finder.DestinatarioDeUsuarioFinder;
import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.DestinatarioOutputPort;
import com.arquisoft.solicitudes.application.remitente.command.finder.RemitenteDeUsuarioFinder;
import com.arquisoft.solicitudes.application.remitente.command.secondaryport.RemitenteOutputPort;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosUsuarioFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.SolicitudDuplicadaFinder;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper.EnviarSolicitudNovedadAsesorMapper;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudNovedadAsesorCommand;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.SolicitudOutputPort;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.entity.SolicitudEntity;
import com.arquisoft.solicitudes.application.solicitud.command.validator.EnviarSolicitudNovedadAsesorValidator;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudNovedadAsesorDomain;
import com.arquisoft.solicitudes.domain.solicitud.event.SolicitudNovedadAsesorEnviadaEvent;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnviarSolicitudNovedadAsesorUseCaseImplTest {

    @Mock private SolicitudOutputPort solicitudOutputPort;
    @Mock private RemitenteOutputPort remitenteOutputPort;
    @Mock private DestinatarioOutputPort destinatarioOutputPort;
    @Mock private RemitenteDeUsuarioFinder remitenteDeUsuarioFinder;
    @Mock private DestinatarioDeUsuarioFinder destinatarioDeUsuarioFinder;
    @Mock private DatosUsuarioFinder datosUsuarioFinder;
    @Mock private SolicitudDuplicadaFinder solicitudDuplicadaFinder;
    @Mock private EnviarSolicitudNovedadAsesorValidator validator;
    @Mock private EventPublisher eventPublisher;
    @Mock private AppLogger logger;

    private EnviarSolicitudNovedadAsesorUseCaseImpl useCase;

    private EnvioSolicitudNovedadAsesorDomain envio;

    private static UsuarioDomain replica(UUID id) {
        return UsuarioDomain.reconstruir(id, "ID-" + id, "Nombre " + id, id + "@uco.edu.co");
    }

    @BeforeEach
    void setUp() {
        useCase = new EnviarSolicitudNovedadAsesorUseCaseImpl(
                solicitudOutputPort, remitenteOutputPort, destinatarioOutputPort,
                remitenteDeUsuarioFinder, destinatarioDeUsuarioFinder, datosUsuarioFinder,
                solicitudDuplicadaFinder, validator, eventPublisher, logger);

        var command = EnviarSolicitudNovedadAsesorCommand.crear(
                UUID.randomUUID().toString(), UUID.randomUUID().toString(), "novedad para el asesor");
        envio = EnviarSolicitudNovedadAsesorMapper.toDomain(command);
    }

    @Test
    void debeRegistrarYPublicarElEvento_cuandoElFlujoEsValido() {
        // Arrange
        UUID remitenteFila = UUID.randomUUID();
        UUID destinatarioFila = UUID.randomUUID();
        when(datosUsuarioFinder.obtener(any())).thenReturn(Optional.of(replica(UUID.randomUUID())));
        when(remitenteDeUsuarioFinder.obtener(envio.getRemitenteUsuario()))
                .thenReturn(Optional.of(remitenteFila));
        when(destinatarioDeUsuarioFinder.obtener(envio.getDestinatarioUsuario()))
                .thenReturn(Optional.of(destinatarioFila));
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
        assertThat(persistida.mensajeSolicitud()).isEqualTo("novedad para el asesor");
        assertThat(persistida.tipoSolicitud()).isEqualTo(TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId());

        ArgumentCaptor<SolicitudNovedadAsesorEnviadaEvent> eventoCaptor =
                ArgumentCaptor.forClass(SolicitudNovedadAsesorEnviadaEvent.class);
        verify(eventPublisher).publish(eventoCaptor.capture());
        SolicitudNovedadAsesorEnviadaEvent evento = eventoCaptor.getValue();
        assertThat(evento.getSolicitudId()).isEqualTo(resultado);
        assertThat(evento.getTemaEvento()).isEqualTo(SolicitudNovedadAsesorEnviadaEvent.EVENT_TOPIC);
        assertThat(evento.getRemitenteUsuarioId()).isEqualTo(envio.getRemitenteUsuario().toString());
        assertThat(evento.getDestinatarioUsuarioId()).isEqualTo(envio.getDestinatarioUsuario().toString());
        assertThat(evento.getMensajeSolicitud()).isEqualTo("novedad para el asesor");
        assertThat(evento.getTipoSolicitud()).isEqualTo(TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId());
    }

    @Test
    void debeReutilizarLaFilaExistente_cuandoElFinderDevuelveUnId() {
        // Arrange
        UUID remitenteFila = UUID.randomUUID();
        UUID destinatarioFila = UUID.randomUUID();
        when(datosUsuarioFinder.obtener(any())).thenReturn(Optional.of(replica(UUID.randomUUID())));
        when(remitenteDeUsuarioFinder.obtener(any())).thenReturn(Optional.of(remitenteFila));
        when(destinatarioDeUsuarioFinder.obtener(any())).thenReturn(Optional.of(destinatarioFila));
        when(solicitudDuplicadaFinder.obtener(any())).thenReturn(false);

        // Act
        useCase.ejecutar(envio);

        // Assert — no se persiste ninguna fila de rol; la solicitud referencia los ids existentes
        verify(remitenteOutputPort, never()).registrar(any());
        verify(destinatarioOutputPort, never()).registrar(any());

        ArgumentCaptor<SolicitudEntity> captor = ArgumentCaptor.forClass(SolicitudEntity.class);
        verify(solicitudOutputPort).registrar(captor.capture());
        assertThat(captor.getValue().remitente()).isEqualTo(remitenteFila);
        assertThat(captor.getValue().destinatario()).isEqualTo(destinatarioFila);
    }

    @Test
    void debePersistirElCandidato_cuandoNoExisteFilaDeRolPrevia() {
        // Arrange
        when(datosUsuarioFinder.obtener(any())).thenReturn(Optional.of(replica(UUID.randomUUID())));
        when(remitenteDeUsuarioFinder.obtener(any())).thenReturn(Optional.empty());
        when(destinatarioDeUsuarioFinder.obtener(any())).thenReturn(Optional.empty());
        when(solicitudDuplicadaFinder.obtener(any())).thenReturn(false);

        // Act
        useCase.ejecutar(envio);

        // Assert — se persiste el candidato del bundle y su id queda en la solicitud
        verify(remitenteOutputPort).registrar(org.mockito.ArgumentMatchers.argThat(e ->
                e.id().equals(envio.getRemitente().getId())
                        && e.usuario().equals(envio.getRemitenteUsuario())));
        verify(destinatarioOutputPort).registrar(org.mockito.ArgumentMatchers.argThat(e ->
                e.id().equals(envio.getDestinatario().getId())
                        && e.usuario().equals(envio.getDestinatarioUsuario())));

        ArgumentCaptor<SolicitudEntity> captor = ArgumentCaptor.forClass(SolicitudEntity.class);
        verify(solicitudOutputPort).registrar(captor.capture());
        assertThat(captor.getValue().remitente()).isEqualTo(envio.getRemitente().getId());
        assertThat(captor.getValue().destinatario()).isEqualTo(envio.getDestinatario().getId());
    }

    @Test
    void debeLanzarYNoTocarLaEscritura_cuandoElRemitenteNoExiste() {
        // Arrange
        when(datosUsuarioFinder.obtener(envio.getRemitenteUsuario())).thenReturn(Optional.empty());
        when(datosUsuarioFinder.obtener(envio.getDestinatarioUsuario()))
                .thenReturn(Optional.of(replica(envio.getDestinatarioUsuario())));
        doThrow(new RemitenteNoEncontradoException(envio.getRemitenteUsuario()))
                .when(validator).validarExistenciaUsuarios(any(), eq(false), eq(true));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(envio))
                .isInstanceOf(RemitenteNoEncontradoException.class);

        verify(solicitudOutputPort, never()).registrar(any());
        verify(eventPublisher, never()).publish(any());
        verifyNoInteractions(remitenteOutputPort, destinatarioOutputPort,
                remitenteDeUsuarioFinder, destinatarioDeUsuarioFinder, solicitudDuplicadaFinder);
    }

    @Test
    void debeLanzarDestinatarioNoEncontrado_cuandoElDestinatarioNoExiste() {
        // Arrange
        when(datosUsuarioFinder.obtener(envio.getRemitenteUsuario()))
                .thenReturn(Optional.of(replica(envio.getRemitenteUsuario())));
        when(datosUsuarioFinder.obtener(envio.getDestinatarioUsuario())).thenReturn(Optional.empty());
        doThrow(new DestinatarioNoEncontradoException(envio.getDestinatarioUsuario()))
                .when(validator).validarExistenciaUsuarios(any(), eq(true), eq(false));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(envio))
                .isInstanceOf(DestinatarioNoEncontradoException.class);

        verify(solicitudOutputPort, never()).registrar(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeLanzarSolicitudDuplicada_cuandoLaClaveYaExiste() {
        // Arrange
        when(datosUsuarioFinder.obtener(any())).thenReturn(Optional.of(replica(UUID.randomUUID())));
        when(remitenteDeUsuarioFinder.obtener(any())).thenReturn(Optional.of(UUID.randomUUID()));
        when(destinatarioDeUsuarioFinder.obtener(any())).thenReturn(Optional.of(UUID.randomUUID()));
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
        when(datosUsuarioFinder.obtener(any())).thenReturn(Optional.of(replica(UUID.randomUUID())));
        when(remitenteDeUsuarioFinder.obtener(any())).thenReturn(Optional.of(UUID.randomUUID()));
        when(destinatarioDeUsuarioFinder.obtener(any())).thenReturn(Optional.of(UUID.randomUUID()));
        when(solicitudDuplicadaFinder.obtener(any())).thenReturn(false);

        // Act
        useCase.ejecutar(envio);

        // Assert — existencia de usuario -> get-or-create -> unicidad -> persistir -> publicar
        InOrder inOrder = inOrder(datosUsuarioFinder, validator, remitenteDeUsuarioFinder,
                solicitudDuplicadaFinder, solicitudOutputPort, eventPublisher);
        inOrder.verify(validator).validarExistenciaUsuarios(any(), anyBoolean(), anyBoolean());
        inOrder.verify(remitenteDeUsuarioFinder).obtener(any());
        inOrder.verify(solicitudDuplicadaFinder).obtener(any());
        inOrder.verify(validator).validarUnicidad(any());
        inOrder.verify(solicitudOutputPort).registrar(any());
        inOrder.verify(eventPublisher).publish(any());
    }
}
