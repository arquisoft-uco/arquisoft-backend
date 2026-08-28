package com.arquisoft.solicitudes.infrastructure.usuario.command.primaryadapter.amqp;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.tracing.application.traza.primaryport.impl.GestorTrazaImpl;
import com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc.MdcContextoDiagnosticoOutputAdapter;
import com.arquisoft.solicitudes.application.usuario.command.primaryport.interactor.RegistrarUsuarioInteractor;
import com.arquisoft.solicitudes.application.usuario.command.primaryport.model.RegistrarUsuarioCommand;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class UsuarioCreadoConsumerTest {

    @Mock
    private RegistrarUsuarioInteractor registrarUsuarioInteractor;

    @Mock
    private Channel channel;

    private UsuarioCreadoConsumer consumer;

    @BeforeEach
    void setUp() {
        var gestorTraza = new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false);
        consumer = new UsuarioCreadoConsumer(
                registrarUsuarioInteractor, new ObjectMapper(), mock(AppLogger.class), gestorTraza);
    }

    private static Message mensaje(String json) {
        MessageProperties props = new MessageProperties();
        props.setDeliveryTag(1L);
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @Test
    void debeRegistrarLaReplica_cuandoElPayloadTraeIdentificadorYNombre() throws Exception {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        String json = String.format("""
                {
                  "idEvento": "%s",
                  "usuarioId": "%s",
                  "identificador": "EST-001",
                  "nombre": "Ana Estudiante",
                  "email": "ana@uco.edu.co"
                }
                """, UUID.randomUUID(), usuarioId);

        // Act
        consumer.onUsuarioCreado(mensaje(json), channel);

        // Assert
        ArgumentCaptor<RegistrarUsuarioCommand> captor = ArgumentCaptor.forClass(RegistrarUsuarioCommand.class);
        verify(registrarUsuarioInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().usuarioId()).isEqualTo(usuarioId);
        assertThat(captor.getValue().identificador()).isEqualTo("EST-001");
        assertThat(captor.getValue().nombre()).isEqualTo("Ana Estudiante");
        assertThat(captor.getValue().email()).isEqualTo("ana@uco.edu.co");
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeIgnorarElMensaje_cuandoFaltanIdentificadorYNombre() throws Exception {
        // Arrange — contrato viejo de 'usuarios' (P1): solo usuarioId/email/rol
        String json = String.format("""
                { "idEvento": "%s", "usuarioId": "%s", "email": "x@uco.edu.co", "rol": "ESTUDIANTE" }
                """, UUID.randomUUID(), UUID.randomUUID());

        // Act
        consumer.onUsuarioCreado(mensaje(json), channel);

        // Assert — no-op idempotente: ACK sin persistir, sin DLQ
        verifyNoInteractions(registrarUsuarioInteractor);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeIgnorarElMensaje_cuandoElNombreLlegaEnBlanco() throws Exception {
        // Arrange
        String json = String.format("""
                {
                  "idEvento": "%s",
                  "usuarioId": "%s",
                  "identificador": "EST-002",
                  "nombre": "   ",
                  "email": "y@uco.edu.co"
                }
                """, UUID.randomUUID(), UUID.randomUUID());

        // Act
        consumer.onUsuarioCreado(mensaje(json), channel);

        // Assert
        verifyNoInteractions(registrarUsuarioInteractor);
        verify(channel).basicAck(1L, false);
    }
}
