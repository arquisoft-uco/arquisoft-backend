package com.arquisoft.fichas.infrastructure.usuario.command.adapter.in.amqp;

import com.arquisoft.fichas.application.usuario.command.model.RegistrarUsuarioCommand;
import com.arquisoft.fichas.application.usuario.command.port.in.RegistrarUsuarioInputPort;
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
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UsuarioCreadoInputAdapterTest {

    @Mock
    private RegistrarUsuarioInputPort registrarUsuarioInputPort;

    @Mock
    private Channel channel;

    private UsuarioCreadoInputAdapter adapter;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        adapter = new UsuarioCreadoInputAdapter(registrarUsuarioInputPort, objectMapper);
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
                    "eventId": "%s",
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
        verify(registrarUsuarioInputPort).ejecutar(commandCaptor.capture());

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
                    "eventId": "%s",
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
                .when(registrarUsuarioInputPort)
                .ejecutar(org.mockito.ArgumentMatchers.any());

        // Act
        adapter.onUsuarioCreado(message, channel);

        // Assert
        verify(channel).basicNack(2L, false, false);
    }
}
