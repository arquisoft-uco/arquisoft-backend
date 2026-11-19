package com.arquisoft.mapas_ruta.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.mapas_ruta.application.proyectogrado.command.primaryport.interactor.AgregarProyectoGradoInteractor;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.primaryport.model.AgregarProyectoGradoCommand;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.result.AgregacionProyectoGradoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.ProyectoGradoMapasRutaKey;
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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProyectoGradoRegistradoConsumerTest {

    private static final String ID_EVENTO = "evt-reg-1";
    private static final Instant OCURRIDO_EN = Instant.parse("2026-11-01T10:00:00Z");

    @Mock
    private AgregarProyectoGradoInteractor interactor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private ProyectoGradoRegistradoConsumer consumer;

    private UUID proyecto;
    private UUID ficha;
    private UUID coordinador;

    @BeforeEach
    void setUp() {
        consumer = new ProyectoGradoRegistradoConsumer(interactor, new ObjectMapper(), logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        proyecto = UtilUUID.generarNuevoUUID();
        ficha = UtilUUID.generarNuevoUUID();
        coordinador = UtilUUID.generarNuevoUUID();
        lenient().when(interactor.ejecutar(any())).thenReturn(new AgregacionProyectoGradoResult.Agregado(proyecto));
    }

    private Message mensaje(String proyectoPayload, long deliveryTag, boolean reentregado) {
        var json = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "%s",
                    "proyectoGradoId": "%s",
                    "fichaPerfilId": "%s",
                    "tituloProyecto": "Titulo",
                    "coordinador": {"id": "%s", "nombre": "Ana", "email": "ana@uco.edu.co"}
                }
                """.formatted(ID_EVENTO, OCURRIDO_EN, proyectoPayload, ficha, coordinador);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @Test
    void debeInvocarInteractorYLoguearAgregado_cuandoLlegaEvento() throws Exception {
        // Act
        consumer.onProyectoGradoRegistrado(mensaje(proyecto.toString(), 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(AgregarProyectoGradoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(
                new AgregarProyectoGradoCommand(proyecto, ficha, "Titulo", coordinador, OCURRIDO_EN));
        verify(logger).info(ProyectoGradoMapasRutaKey.LOG_PROYECTO_RECIBIDO, ID_EVENTO, proyecto.toString(), "Titulo");
        verify(logger).info(ProyectoGradoMapasRutaKey.LOG_AGREGADO, proyecto);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeLoguearDuplicadoYConfirmar_cuandoElResultadoEsDuplicado() throws Exception {
        // Arrange
        when(interactor.ejecutar(any()))
                .thenReturn(new AgregacionProyectoGradoResult.Duplicado(proyecto));

        // Act
        consumer.onProyectoGradoRegistrado(mensaje(proyecto.toString(), 2L, false), channel);

        // Assert
        verify(logger).info(ProyectoGradoMapasRutaKey.LOG_DUPLICADO, proyecto);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeEnviarADlq_cuandoPayloadInvalido() throws Exception {
        // Act
        consumer.onProyectoGradoRegistrado(mensaje("no-es-un-uuid", 3L, false), channel);

        // Assert
        verify(interactor, never()).ejecutar(any());
        verify(channel).basicNack(3L, false, false);
    }

    @Test
    void debeReencolar_cuandoFalloTransitorioEnPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onProyectoGradoRegistrado(mensaje(proyecto.toString(), 4L, false), channel);

        // Assert
        verify(channel).basicNack(4L, false, true);
    }

    @Test
    void debeEnviarADlq_cuandoFalloTransitorioYaReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onProyectoGradoRegistrado(mensaje(proyecto.toString(), 5L, true), channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
    }
}
