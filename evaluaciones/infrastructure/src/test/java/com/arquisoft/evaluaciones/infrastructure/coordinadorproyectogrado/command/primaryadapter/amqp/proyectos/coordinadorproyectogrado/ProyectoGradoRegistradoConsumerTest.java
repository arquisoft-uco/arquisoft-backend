package com.arquisoft.evaluaciones.infrastructure.coordinadorproyectogrado.command.primaryadapter.amqp.proyectos.coordinadorproyectogrado;

import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.primaryport.interactor.AgregarCoordinadorProyectoGradoInteractor;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.primaryport.model.AgregarCoordinadorProyectoGradoCommand;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.result.AgregacionCoordinadorProyectoGradoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.CoordinadorProyectoGradoKey;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProyectoGradoRegistradoConsumerTest {

    private static final String ID_EVENTO = "evt-registrado-1";
    private static final Instant OCURRIDO_EN = Instant.parse("2026-11-19T10:00:00Z");

    @Mock
    private AgregarCoordinadorProyectoGradoInteractor interactor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private ProyectoGradoRegistradoConsumer consumer;

    private final UUID proyectoGrado = UtilUUID.generarNuevoUUID();
    private final UUID coordinador = UtilUUID.generarNuevoUUID();

    @BeforeEach
    void setUp() {
        consumer = new ProyectoGradoRegistradoConsumer(interactor, new ObjectMapper(), logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        lenient().when(interactor.ejecutar(any()))
                .thenReturn(new AgregacionCoordinadorProyectoGradoResult.Agregada(proyectoGrado));
    }

    private Message mensaje(String proyecto, long deliveryTag, boolean reentregado) {
        var json = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "%s",
                    "proyectoGradoId": "%s",
                    "fichaPerfilId": "%s",
                    "tituloProyecto": "Sistema",
                    "coordinador": {"id": "%s", "nombre": "Coord Uno", "email": "coord@uco.edu.co"}
                }
                """.formatted(ID_EVENTO, OCURRIDO_EN, proyecto, UtilUUID.generarNuevoUUID(), coordinador);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @Test
    void debeInvocarElInteractorYConfirmar_cuandoLlegaElEvento() throws Exception {
        // Act
        consumer.onProyectoGradoRegistrado(mensaje(proyectoGrado.toString(), 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(AgregarCoordinadorProyectoGradoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new AgregarCoordinadorProyectoGradoCommand(
                proyectoGrado, coordinador, "Coord Uno", "coord@uco.edu.co", OCURRIDO_EN));
        verify(logger).info(eq(CoordinadorProyectoGradoKey.LOG_AGREGADO), eq(proyectoGrado));
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeLoguearDuplicadoYConfirmar_cuandoElProyectoYaTeniaCoordinador() throws Exception {
        // Arrange
        lenient().when(interactor.ejecutar(any()))
                .thenReturn(new AgregacionCoordinadorProyectoGradoResult.Duplicada(proyectoGrado));

        // Act
        consumer.onProyectoGradoRegistrado(mensaje(proyectoGrado.toString(), 2L, false), channel);

        // Assert
        verify(logger).info(eq(CoordinadorProyectoGradoKey.LOG_DUPLICADO), eq(proyectoGrado));
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
    void debeReencolarSoloEnLaPrimeraEntrega_cuandoElFalloEsTransitorio() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onProyectoGradoRegistrado(mensaje(proyectoGrado.toString(), 4L, false), channel);
        consumer.onProyectoGradoRegistrado(mensaje(proyectoGrado.toString(), 5L, true), channel);

        // Assert
        verify(channel).basicNack(4L, false, true);
        verify(channel).basicNack(5L, false, false);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElFalloNoEsTransitorio() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("envenenado")).when(interactor).ejecutar(any());

        // Act
        consumer.onProyectoGradoRegistrado(mensaje(proyectoGrado.toString(), 6L, false), channel);

        // Assert
        verify(channel).basicNack(6L, false, false);
    }
}
