package com.arquisoft.proyectos.infrastructure.proyectogrado.command.primaryadapter.amqp.mapasruta.maparuta;

import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.interactor.AtrasarProyectoGradoInteractor;
import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model.AtrasarProyectoGradoCommand;
import com.arquisoft.proyectos.application.proyectogrado.command.result.AtrasoProyectoGradoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;
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

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapaRutaPlazoVencidoConsumerTest {

    private static final String ID_EVENTO = "evt-plazo-1";

    @Mock
    private AtrasarProyectoGradoInteractor interactor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private MapaRutaPlazoVencidoConsumer consumer;

    private UUID proyecto;
    private UUID mapa;

    @BeforeEach
    void setUp() {
        consumer = new MapaRutaPlazoVencidoConsumer(interactor, new ObjectMapper(), logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        proyecto = UtilUUID.generarNuevoUUID();
        mapa = UtilUUID.generarNuevoUUID();
        lenient().when(interactor.ejecutar(any())).thenReturn(new AtrasoProyectoGradoResult.Atrasado(proyecto));
    }

    private Message mensaje(String proyectoPayload, long deliveryTag, boolean reentregado) {
        var json = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "2026-11-19T05:05:00Z",
                    "mapaRuta": "%s",
                    "proyectoGrado": "%s",
                    "fechaFin": "2026-11-18"
                }
                """.formatted(ID_EVENTO, mapa, proyectoPayload);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @Test
    void debeInvocarInteractorYLoguearAtrasado_cuandoLlegaEvento() throws Exception {
        // Act
        consumer.onMapaRutaPlazoVencido(mensaje(proyecto.toString(), 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(AtrasarProyectoGradoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new AtrasarProyectoGradoCommand(proyecto));
        verify(logger).info(ProyectoGradoKey.LOG_PLAZO_VENCIDO_RECIBIDO,
                ID_EVENTO, mapa.toString(), proyecto.toString(), LocalDate.of(2026, 11, 18));
        verify(logger).info(ProyectoGradoKey.LOG_ATRASADO, proyecto);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeLoguearOmitidoYConfirmar_cuandoElResultadoEsOmitido() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(new AtrasoProyectoGradoResult.Omitido(proyecto, "ATRASADO"));

        // Act
        consumer.onMapaRutaPlazoVencido(mensaje(proyecto.toString(), 2L, false), channel);

        // Assert
        verify(logger).info(ProyectoGradoKey.LOG_ATRASO_OMITIDO, proyecto, "ATRASADO");
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeEnviarADlq_cuandoPayloadInvalido() throws Exception {
        // Act
        consumer.onMapaRutaPlazoVencido(mensaje("no-es-un-uuid", 3L, false), channel);

        // Assert
        verify(interactor, never()).ejecutar(any());
        verify(channel).basicNack(3L, false, false);
    }

    @Test
    void debeReencolar_cuandoFalloTransitorioEnPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onMapaRutaPlazoVencido(mensaje(proyecto.toString(), 4L, false), channel);

        // Assert
        verify(channel).basicNack(4L, false, true);
    }

    @Test
    void debeEnviarADlq_cuandoFalloTransitorioYaReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onMapaRutaPlazoVencido(mensaje(proyecto.toString(), 5L, true), channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
    }
}
