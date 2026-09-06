package com.arquisoft.fichas.infrastructure.usuario.command.primaryadapter.amqp;

import com.arquisoft.fichas.application.usuario.command.primaryport.model.RegistrarUsuarioCommand;
import com.arquisoft.fichas.application.usuario.command.usecase.RegistrarUsuarioUseCase;
import com.arquisoft.shared.tracing.application.traza.primaryport.impl.GestorTrazaImpl;
import com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc.MdcContextoDiagnosticoOutputAdapter;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.dao.QueryTimeoutException;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UsuarioCreadoConsumerTest {

    @Mock
    private RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @Mock
    private Channel channel;

    private UsuarioCreadoConsumer adapter;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        var gestorTraza = new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false);
        adapter = new UsuarioCreadoConsumer(registrarUsuarioUseCase, objectMapper,
                org.mockito.Mockito.mock(com.arquisoft.shared.logger.AppLogger.class),
                gestorTraza);
    }

    @Test
    void debeRegistrarUsuario_cuandoEventoValido() throws Exception {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        String email = "juan.perez@example.com";
        String rol = "ESTUDIANTE";

        String payloadJson = String.format(
                """
                {
                    "idEvento": "%s",
                    "usuarioId": "%s",
                    "email": "%s",
                    "rol": "%s"
                }
                """,
                UUID.randomUUID().toString(),
                usuarioId.toString(),
                email,
                rol
        );

        MessageProperties props = new MessageProperties();
        props.setDeliveryTag(1L);
        props.setHeader("X-Trace-Id", "trace-123");
        props.setHeader("X-User-Id", "user-456");

        Message message = MessageBuilder
                .withBody(payloadJson.getBytes())
                .andProperties(props)
                .build();

        // Act
        adapter.onUsuarioCreado(message, channel);

        // Assert
        ArgumentCaptor<RegistrarUsuarioCommand> commandCaptor = ArgumentCaptor.forClass(RegistrarUsuarioCommand.class);
        verify(registrarUsuarioUseCase).ejecutar(commandCaptor.capture());

        RegistrarUsuarioCommand command = commandCaptor.getValue();
        assertThat(command.usuarioId()).isEqualTo(usuarioId);
        assertThat(command.email()).isEqualTo(email);
        assertThat(command.rol()).isEqualTo(rol);

        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeEnviarNack_cuandoRegistrarUsuarioFalla() throws Exception {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        String payloadJson = String.format(
                """
                {
                    "idEvento": "%s",
                    "usuarioId": "%s",
                    "email": "duplicado@example.com",
                    "rol": "ESTUDIANTE"
                }
                """,
                UUID.randomUUID().toString(),
                usuarioId.toString()
        );

        MessageProperties props = new MessageProperties();
        props.setDeliveryTag(2L);

        Message message = MessageBuilder
                .withBody(payloadJson.getBytes())
                .andProperties(props)
                .build();

        doThrow(new RuntimeException("Email duplicado"))
                .when(registrarUsuarioUseCase)
                .ejecutar(org.mockito.ArgumentMatchers.any());

        // Act
        adapter.onUsuarioCreado(message, channel);

        // Assert
        verify(channel).basicNack(2L, false, false);
    }

    @Test
    void debeReencolar_cuandoFalloTransitorioEnPrimeraEntrega() throws Exception {
        // Arrange
        Message message = mensajeValido(3L, false);

        doThrow(new QueryTimeoutException("Base de datos no disponible"))
                .when(registrarUsuarioUseCase)
                .ejecutar(org.mockito.ArgumentMatchers.any());

        // Act
        adapter.onUsuarioCreado(message, channel);

        // Assert
        verify(channel).basicNack(3L, false, true);
    }

    @Test
    void debeEnviarADlq_cuandoFalloTransitorioPersisteTrasElReintento() throws Exception {
        // Arrange
        Message message = mensajeValido(4L, true);

        doThrow(new QueryTimeoutException("Base de datos no disponible"))
                .when(registrarUsuarioUseCase)
                .ejecutar(org.mockito.ArgumentMatchers.any());

        // Act
        adapter.onUsuarioCreado(message, channel);

        // Assert
        verify(channel).basicNack(4L, false, false);
    }

    @Test
    void debeEnviarADlqSinReencolar_cuandoFalloEnvenenadoEnPrimeraEntrega() throws Exception {
        // Arrange
        Message message = mensajeValido(5L, false);

        doThrow(new IllegalArgumentException("Rol desconocido"))
                .when(registrarUsuarioUseCase)
                .ejecutar(org.mockito.ArgumentMatchers.any());

        // Act
        adapter.onUsuarioCreado(message, channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
    }

    private Message mensajeValido(long deliveryTag, boolean reentregado) {
        String payloadJson = String.format(
                """
                {
                    "idEvento": "%s",
                    "usuarioId": "%s",
                    "email": "juan.perez@example.com",
                    "rol": "ESTUDIANTE"
                }
                """,
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        MessageProperties props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);

        return MessageBuilder
                .withBody(payloadJson.getBytes())
                .andProperties(props)
                .build();
    }
}
