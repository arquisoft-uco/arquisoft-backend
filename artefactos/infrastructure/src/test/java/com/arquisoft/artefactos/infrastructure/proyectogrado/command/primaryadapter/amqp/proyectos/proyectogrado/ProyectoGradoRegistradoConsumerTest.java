package com.arquisoft.artefactos.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.AgregarProyectoGradoInteractor;
import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model.AgregarProyectoGradoCommand;
import com.arquisoft.artefactos.application.proyectogrado.command.result.AgregacionProyectoGradoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;
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
import org.springframework.dao.QueryTimeoutException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProyectoGradoRegistradoConsumerTest {

    private static final String ID_EVENTO = "evt-proyecto-1";
    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-21T10:00:00Z");

    @Mock
    private AgregarProyectoGradoInteractor interactor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private ProyectoGradoRegistradoConsumer consumer;

    private final UUID proyectoGrado = UtilUUID.generarNuevoUUID();

    @BeforeEach
    void setUp() {
        consumer = new ProyectoGradoRegistradoConsumer(
                interactor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
    }

    private Message mensaje(String proyectoPayload, long deliveryTag, boolean reentregado) {
        var json = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "%s",
                    "proyectoGradoId": "%s",
                    "tituloProyecto": "Sistema de gestion"
                }
                """.formatted(ID_EVENTO, OCURRIDO_EN, proyectoPayload);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @Test
    void debeInvocarInteractorYConfirmar_cuandoLlegaElEvento() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(new AgregacionProyectoGradoResult.Agregado(proyectoGrado));

        // Act
        consumer.onProyectoGradoRegistrado(mensaje(proyectoGrado.toString(), 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(AgregarProyectoGradoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new AgregarProyectoGradoCommand(proyectoGrado, "Sistema de gestion"));
        verify(logger).info(ProyectoGradoKey.LOG_AGREGADO, proyectoGrado);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeConfirmarSinMasEfectos_cuandoElResultadoEsDuplicado() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(new AgregacionProyectoGradoResult.Duplicado(proyectoGrado));

        // Act
        consumer.onProyectoGradoRegistrado(mensaje(proyectoGrado.toString(), 2L, false), channel);

        // Assert
        verify(logger).info(ProyectoGradoKey.LOG_DUPLICADO, proyectoGrado);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeEnviarNackSinReencolarNiInvocarElInteractor_cuandoElPayloadEsMalformado() throws Exception {
        // Act
        consumer.onProyectoGradoRegistrado(mensaje("no-es-un-uuid", 3L, false), channel);

        // Assert
        verify(interactor, never()).ejecutar(any());
        verify(channel).basicNack(3L, false, false);
    }

    @Test
    void debeEnviarNackReencolando_cuandoElFalloEsTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onProyectoGradoRegistrado(mensaje(proyectoGrado.toString(), 4L, false), channel);

        // Assert
        verify(channel).basicNack(4L, false, true);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElFalloEsTransitorioYaReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onProyectoGradoRegistrado(mensaje(proyectoGrado.toString(), 5L, true), channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
        verify(channel, never()).basicAck(5L, false);
    }
}
