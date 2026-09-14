package com.arquisoft.solicitudes.infrastructure.usuario.command.primaryadapter.amqp.usuarios.coordinador;

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

@ExtendWith(MockitoExtension.class)
class CoordinadorAgregadoConsumerTest {

    @Mock
    private RegistrarUsuarioInteractor registrarUsuarioInteractor;

    @Mock
    private Channel channel;

    private CoordinadorAgregadoConsumer consumer;

    @BeforeEach
    void setUp() {
        var gestorTraza = new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false);
        consumer = new CoordinadorAgregadoConsumer(
                registrarUsuarioInteractor, new ObjectMapper(), mock(AppLogger.class), gestorTraza);
    }

    private static Message mensaje(String json) {
        MessageProperties props = new MessageProperties();
        props.setDeliveryTag(1L);
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @Test
    void debeRegistrarLaReplica_cuandoLlegaElEvento() throws Exception {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        String json = String.format("""
                {
                  "idEvento": "%s",
                  "ocurridoEn": "2026-09-12T10:00:00Z",
                  "usuario": "%s",
                  "identificador": "COORD-001",
                  "nombre": "Ana Coordinadora",
                  "email": "ana@uco.edu.co"
                }
                """, UUID.randomUUID(), usuarioId);

        // Act
        consumer.onCoordinadorAgregado(mensaje(json), channel);

        // Assert
        ArgumentCaptor<RegistrarUsuarioCommand> captor = ArgumentCaptor.forClass(RegistrarUsuarioCommand.class);
        verify(registrarUsuarioInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().usuarioId()).isEqualTo(usuarioId);
        assertThat(captor.getValue().identificador()).isEqualTo("COORD-001");
        assertThat(captor.getValue().nombre()).isEqualTo("Ana Coordinadora");
        assertThat(captor.getValue().email()).isEqualTo("ana@uco.edu.co");
        verify(channel).basicAck(1L, false);
    }
}
