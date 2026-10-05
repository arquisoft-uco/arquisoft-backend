package com.arquisoft.solicitudes.application.solicitud.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.solicitudes.application.destinatario.command.finder.DestinatarioDeUsuarioFinder;
import com.arquisoft.solicitudes.application.destinatario.command.usecase.RegistrarDestinatarioUseCase;
import com.arquisoft.solicitudes.application.remitente.command.finder.RemitenteDeUsuarioFinder;
import com.arquisoft.solicitudes.application.remitente.command.usecase.RegistrarRemitenteUseCase;
import com.arquisoft.solicitudes.application.solicitud.command.finder.SolicitudDuplicadaFinder;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper.EnviarSolicitudCambioAsesorMapper;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper.EnviarSolicitudNovedadAsesorMapper;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudCambioAsesorCommand;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudNovedadAsesorCommand;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.SolicitudOutputPort;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.entity.SolicitudEntity;
import com.arquisoft.solicitudes.application.solicitud.command.validator.EnviarSolicitudValidator;
import com.arquisoft.solicitudes.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.solicitudes.domain.destinatario.exception.DestinatarioNoEncontradoException;
import com.arquisoft.solicitudes.domain.remitente.exception.RemitenteNoEncontradoException;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudDomain;
import com.arquisoft.solicitudes.domain.solicitud.event.SolicitudEnviadaEvent;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudDuplicadaException;
import com.arquisoft.solicitudes.domain.solicitud.model.ClaveSolicitud;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnviarSolicitudUseCaseImplTest {

    @Mock private SolicitudOutputPort solicitudOutputPort;
    @Mock private RegistrarRemitenteUseCase registrarRemitenteUseCase;
    @Mock private RegistrarDestinatarioUseCase registrarDestinatarioUseCase;
    @Mock private RemitenteDeUsuarioFinder remitenteDeUsuarioFinder;
    @Mock private DestinatarioDeUsuarioFinder destinatarioDeUsuarioFinder;
    @Mock private UsuarioPorIdFinder usuarioPorIdFinder;
    @Mock private SolicitudDuplicadaFinder solicitudDuplicadaFinder;
    @Mock private EnviarSolicitudValidator validator;
    @Mock private EventPublisher eventPublisher;
    @Mock private AppLogger logger;

    private EnviarSolicitudUseCaseImpl useCase;

    private EnvioSolicitudDomain envio;

    private static UsuarioDomain replica(UUID id, String nombre, String email) {
        return UsuarioDomain.reconstruir(id, "ID-" + id, nombre, email, Instant.now());
    }

    @BeforeEach
    void setUp() {
        useCase = new EnviarSolicitudUseCaseImpl(
                solicitudOutputPort, registrarRemitenteUseCase, registrarDestinatarioUseCase,
                remitenteDeUsuarioFinder, destinatarioDeUsuarioFinder, usuarioPorIdFinder,
                solicitudDuplicadaFinder, validator, eventPublisher, logger);

        var command = EnviarSolicitudNovedadAsesorCommand.crear(
                UUID.randomUUID(), UUID.randomUUID().toString(), "novedad para el asesor");
        envio = EnviarSolicitudNovedadAsesorMapper.toDomain(command);
    }

    private void stubFlujoValido(UUID remitenteFila, UUID destinatarioFila) {
        when(remitenteDeUsuarioFinder.obtener(envio.getRemitenteUsuario())).thenReturn(remitenteFila);
        when(destinatarioDeUsuarioFinder.obtener(envio.getDestinatarioUsuario())).thenReturn(destinatarioFila);
        when(solicitudDuplicadaFinder.obtener(any())).thenReturn(false);
        when(usuarioPorIdFinder.obtener(envio.getRemitenteUsuario()))
                .thenReturn(replica(envio.getRemitenteUsuario(), "Ana Estudiante", "ana@uco.edu.co"));
        when(usuarioPorIdFinder.obtener(envio.getDestinatarioUsuario()))
                .thenReturn(replica(envio.getDestinatarioUsuario(), "Pedro Asesor", "pedro@uco.edu.co"));
    }

    @Test
    void debeRegistrarYPublicarElEvento_cuandoElFlujoEsValido() {
        // Arrange
        var remitenteFila = UUID.randomUUID();
        var destinatarioFila = UUID.randomUUID();
        stubFlujoValido(remitenteFila, destinatarioFila);

        // Act
        var resultado = useCase.ejecutar(envio);

        // Assert
        assertThat(resultado).isEqualTo(envio.getSolicitud().getId());

        var entityCaptor = ArgumentCaptor.forClass(SolicitudEntity.class);
        verify(solicitudOutputPort).registrar(entityCaptor.capture());
        var persistida = entityCaptor.getValue();
        assertThat(persistida.id()).isEqualTo(resultado);
        assertThat(persistida.remitente()).isEqualTo(remitenteFila);
        assertThat(persistida.destinatario()).isEqualTo(destinatarioFila);
        assertThat(persistida.mensajeSolicitud()).isEqualTo("novedad para el asesor");
        assertThat(persistida.tipoSolicitud()).isEqualTo(TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId());

        var eventoCaptor = ArgumentCaptor.forClass(SolicitudEnviadaEvent.class);
        verify(eventPublisher).publish(eventoCaptor.capture());
        var evento = eventoCaptor.getValue();
        assertThat(evento.getSolicitudId()).isEqualTo(resultado);
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Solicitudes.NOVEDAD_ASESOR_ENVIADA);
        assertThat(evento.getRemitenteNombre()).isEqualTo("Ana Estudiante");
        assertThat(evento.getDestinatarioNombre()).isEqualTo("Pedro Asesor");
        assertThat(evento.getDestinatarioEmail()).isEqualTo("pedro@uco.edu.co");
        assertThat(evento.getMensajeSolicitud()).isEqualTo("novedad para el asesor");
    }

    @Test
    void debePublicarElEventoDelTipoDeLaSolicitud_cuandoElTipoEsCambioDeAsesor() {
        // Arrange
        var command = EnviarSolicitudCambioAsesorCommand.crear(
                UUID.randomUUID(), UUID.randomUUID().toString(), "cambio de asesor");
        envio = EnviarSolicitudCambioAsesorMapper.toDomain(command);
        stubFlujoValido(UUID.randomUUID(), UUID.randomUUID());

        // Act
        useCase.ejecutar(envio);

        // Assert
        var entityCaptor = ArgumentCaptor.forClass(SolicitudEntity.class);
        verify(solicitudOutputPort).registrar(entityCaptor.capture());
        assertThat(entityCaptor.getValue().tipoSolicitud()).isEqualTo(TipoSolicitud.CAMBIO_DE_ASESOR.getId());
        var eventoCaptor = ArgumentCaptor.forClass(SolicitudEnviadaEvent.class);
        verify(eventPublisher).publish(eventoCaptor.capture());
        assertThat(eventoCaptor.getValue().getTemaEvento())
                .isEqualTo(EventTopics.Solicitudes.CAMBIO_ASESOR_ENVIADA);
    }

    @Test
    void debeUsarLosIdsDeFilaQueResuelvenLosFinders_alPersistirYAlConsultarLaUnicidad() {
        // Arrange
        var remitenteFila = UUID.randomUUID();
        var destinatarioFila = UUID.randomUUID();
        stubFlujoValido(remitenteFila, destinatarioFila);

        // Act
        useCase.ejecutar(envio);

        // Assert
        var claveCaptor = ArgumentCaptor.forClass(ClaveSolicitud.class);
        verify(solicitudDuplicadaFinder).obtener(claveCaptor.capture());
        assertThat(claveCaptor.getValue().remitente()).isEqualTo(remitenteFila);
        assertThat(claveCaptor.getValue().destinatario()).isEqualTo(destinatarioFila);

    }

    @Test
    void debeLanzarYNoSeguir_cuandoElRemitenteNoExiste() {
        // Arrange
        doThrow(new RemitenteNoEncontradoException(envio.getRemitenteUsuario()))
                .when(registrarRemitenteUseCase).ejecutar(envio.getRemitente());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(envio))
                .isInstanceOf(RemitenteNoEncontradoException.class);

        verifyNoInteractions(registrarDestinatarioUseCase, remitenteDeUsuarioFinder, destinatarioDeUsuarioFinder,
                solicitudDuplicadaFinder, solicitudOutputPort, eventPublisher);
    }

    @Test
    void debeLanzarYNoPersistir_cuandoElDestinatarioNoExiste() {
        // Arrange
        doThrow(new DestinatarioNoEncontradoException(envio.getDestinatarioUsuario()))
                .when(registrarDestinatarioUseCase).ejecutar(envio.getDestinatario());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(envio))
                .isInstanceOf(DestinatarioNoEncontradoException.class);

        verify(registrarRemitenteUseCase).ejecutar(envio.getRemitente());
        verifyNoInteractions(solicitudDuplicadaFinder, solicitudOutputPort, eventPublisher);
    }

    @Test
    void debeLanzarSolicitudDuplicada_cuandoLaClaveYaExiste() {
        // Arrange
        when(remitenteDeUsuarioFinder.obtener(any())).thenReturn(UUID.randomUUID());
        when(destinatarioDeUsuarioFinder.obtener(any())).thenReturn(UUID.randomUUID());
        when(solicitudDuplicadaFinder.obtener(any())).thenReturn(true);
        doThrow(new SolicitudDuplicadaException()).when(validator).validar(any(DisponibilidadSolicitud.class));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(envio))
                .isInstanceOf(SolicitudDuplicadaException.class);

        verify(solicitudOutputPort, never()).registrar(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeRegistrarConsultarValidarYPersistirEnOrden_cuandoElFlujoEsValido() {
        // Arrange
        stubFlujoValido(UUID.randomUUID(), UUID.randomUUID());

        // Act
        useCase.ejecutar(envio);

        // Assert — registrar remitente (valida existencia) -> registrar destinatario -> finders -> validar -> persistir -> publicar
        InOrder inOrder = inOrder(registrarRemitenteUseCase, registrarDestinatarioUseCase,
                remitenteDeUsuarioFinder, solicitudDuplicadaFinder,
                validator, solicitudOutputPort, eventPublisher);
        inOrder.verify(registrarRemitenteUseCase).ejecutar(any());
        inOrder.verify(registrarDestinatarioUseCase).ejecutar(any());
        inOrder.verify(remitenteDeUsuarioFinder).obtener(any());
        inOrder.verify(solicitudDuplicadaFinder).obtener(any());
        inOrder.verify(validator).validar(any());
        inOrder.verify(solicitudOutputPort).registrar(any());
        inOrder.verify(eventPublisher).publish(any());
    }
}
