package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.artefactos.versionartefacto;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.application.notificacion.command.result.EnvioNotificacionResult;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.tracing.application.traza.primaryport.impl.GestorTrazaImpl;
import com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc.MdcContextoDiagnosticoOutputAdapter;
import com.arquisoft.shared.util.UtilUUID;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VersionArtefactoGeneradaConsumerTest {

    @Mock
    private EnviarNotificacionInteractor enviarNotificacionInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private VersionArtefactoGeneradaConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new VersionArtefactoGeneradaConsumer(
                enviarNotificacionInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        lenient().when(enviarNotificacionInteractor.ejecutar(any()))
                .thenReturn(new EnvioNotificacionResult.Enviada("evt", "ana.perez@uco.edu.co"));
    }

    private static Message mensaje(String cuerpo, long deliveryTag) {
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        return MessageBuilder.withBody(cuerpo.getBytes()).andProperties(props).build();
    }

    private static String payloadValido(String idEvento) {
        return """
                {
                    "idEvento": "%s",
                    "versionArtefacto": "%s",
                    "artefacto": "%s",
                    "numeroVersion": 3,
                    "nombreDocumento": "Formato A",
                    "asesor": { "nombre": "Ana Perez", "email": "ana.perez@uco.edu.co" }
                }
                """.formatted(idEvento, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
    }

    @Test
    void debeEnviarNotificacionAlAsesor() throws Exception {
        // Arrange
        var idEvento = UtilUUID.generarNuevoUUID().toString();

        // Act
        consumer.onVersionArtefactoGenerada(mensaje(payloadValido(idEvento), 1L), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor).ejecutar(captor.capture());
        var command = captor.getValue();
        assertThat(command.idEvento()).isEqualTo(idEvento);
        assertThat(command.tipo()).isEqualTo(TipoNotificacion.VERSION_ARTEFACTO_GENERADA);
        assertThat(command.destinatarioNombre()).isEqualTo("Ana Perez");
        assertThat(command.destinatarioEmail()).isEqualTo("ana.perez@uco.edu.co");
        assertThat(command.asunto()).contains("3").contains("Formato A");
        assertThat(command.cuerpo()).contains("Ana Perez").contains("3").contains("Formato A");
        assertThat(command.pie()).isNotEmpty();
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeNackear_cuandoPayloadInvalido() throws Exception {
        // Act
        consumer.onVersionArtefactoGenerada(mensaje("{ no es json", 2L), channel);

        // Assert
        verify(enviarNotificacionInteractor, never()).ejecutar(any());
        verify(channel).basicNack(2L, false, false);
    }
}
