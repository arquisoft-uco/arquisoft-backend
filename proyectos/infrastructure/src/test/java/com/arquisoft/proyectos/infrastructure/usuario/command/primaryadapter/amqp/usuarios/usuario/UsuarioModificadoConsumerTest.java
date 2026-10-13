package com.arquisoft.proyectos.infrastructure.usuario.command.primaryadapter.amqp.usuarios.usuario;

import com.arquisoft.proyectos.application.asesor.command.primaryport.interactor.ActualizarAsesorInteractor;
import com.arquisoft.proyectos.application.asesor.command.result.ActualizacionAsesorResult;
import com.arquisoft.proyectos.application.coordinador.command.primaryport.interactor.ActualizarCoordinadorInteractor;
import com.arquisoft.proyectos.application.coordinador.command.result.ActualizacionCoordinadorResult;
import com.arquisoft.proyectos.application.estudiante.command.primaryport.interactor.ActualizarEstudianteInteractor;
import com.arquisoft.proyectos.application.estudiante.command.result.ActualizacionEstudianteResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.tracing.application.traza.primaryport.impl.GestorTrazaImpl;
import com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc.MdcContextoDiagnosticoOutputAdapter;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.dao.QueryTimeoutException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioModificadoConsumerTest {

    @Mock
    private ActualizarEstudianteInteractor actualizarEstudianteInteractor;
    @Mock
    private ActualizarAsesorInteractor actualizarAsesorInteractor;
    @Mock
    private ActualizarCoordinadorInteractor actualizarCoordinadorInteractor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private UsuarioModificadoConsumer adapter;

    @BeforeEach
    void setUp() {
        adapter = new UsuarioModificadoConsumer(
                actualizarEstudianteInteractor,
                actualizarAsesorInteractor,
                actualizarCoordinadorInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(actualizarEstudianteInteractor.ejecutar(any()))
                .thenReturn(new ActualizacionEstudianteResult.Actualizada(UUID.randomUUID()));
        lenient().when(actualizarAsesorInteractor.ejecutar(any()))
                .thenReturn(new ActualizacionAsesorResult.Actualizada(UUID.randomUUID()));
        lenient().when(actualizarCoordinadorInteractor.ejecutar(any()))
                .thenReturn(new ActualizacionCoordinadorResult.Actualizada(UUID.randomUUID()));
    }

    private Message mensajeCon(String idEvento, long deliveryTag, boolean reentregado) {
        var payloadJson = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "2026-09-16T10:00:00Z",
                    "usuario": "%s",
                    "identificador": "20161020999",
                    "nombre": "Ana Actualizada",
                    "email": "actualizada@uco.edu.co"
                }
                """.formatted(idEvento, UUID.randomUUID());

        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        props.setHeader("X-Trace-Id", "trace-123");
        props.setHeader("X-User-Id", "user-456");

        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeInvocarLosTresInteractores_cuandoLlegaElEvento() throws Exception {
        // Act
        adapter.onUsuarioModificado(mensajeCon(UUID.randomUUID().toString(), 1L, false), channel);

        // Assert
        verify(actualizarEstudianteInteractor).ejecutar(any());
        verify(actualizarAsesorInteractor).ejecutar(any());
        verify(actualizarCoordinadorInteractor).ejecutar(any());
    }

    @Test
    void debeConfirmarElMensaje_cuandoLosTresResultadosSonActualizada() throws Exception {
        // Act
        adapter.onUsuarioModificado(mensajeCon(UUID.randomUUID().toString(), 1L, false), channel);

        // Assert
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeConfirmarElMensaje_cuandoElResultadoEsDescartada() throws Exception {
        // Arrange
        when(actualizarEstudianteInteractor.ejecutar(any())).thenReturn(
                new ActualizacionEstudianteResult.Descartada(UUID.randomUUID(), Instant.now()));

        // Act
        adapter.onUsuarioModificado(mensajeCon(UUID.randomUUID().toString(), 2L, false), channel);

        // Assert
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeConfirmarElMensaje_cuandoElResultadoEsNoReplicado() throws Exception {
        // Arrange
        when(actualizarCoordinadorInteractor.ejecutar(any())).thenReturn(
                new ActualizacionCoordinadorResult.NoReplicado(UUID.randomUUID()));

        // Act
        adapter.onUsuarioModificado(mensajeCon(UUID.randomUUID().toString(), 3L, false), channel);

        // Assert
        verify(channel).basicAck(3L, false);
    }

    @Test
    void debeEnviarNackConReintento_cuandoElFalloEsTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(actualizarEstudianteInteractor).ejecutar(any());

        // Act
        adapter.onUsuarioModificado(mensajeCon(UUID.randomUUID().toString(), 4L, false), channel);

        // Assert
        verify(channel).basicNack(4L, false, true);
    }

    @Test
    void debeEnviarNackSinReintento_cuandoElFalloEsTransitorioYaReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(actualizarEstudianteInteractor).ejecutar(any());

        // Act
        adapter.onUsuarioModificado(mensajeCon(UUID.randomUUID().toString(), 5L, true), channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElPayloadEsEnvenenado() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("id invalido")).when(actualizarEstudianteInteractor).ejecutar(any());

        // Act
        adapter.onUsuarioModificado(mensajeCon(UUID.randomUUID().toString(), 6L, false), channel);

        // Assert
        verify(channel).basicNack(6L, false, false);
    }
}
