package com.arquisoft.proyectos.infrastructure.proyectogrado.command.primaryadapter.amqp.mapasruta.maparuta;

import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.interactor.ReanudarProyectoGradoInteractor;
import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model.ReanudarProyectoGradoCommand;
import com.arquisoft.proyectos.application.proyectogrado.command.result.ReanudacionProyectoGradoResult;
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
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapaRutaFechaFinCambiadaConsumerTest {

    private static final String ID_EVENTO = "evt-fecha-1";
    private static final String ZONA = "Pacific/Kiritimati";
    private static final LocalDate FECHA_FIN = LocalDate.of(2026, 12, 20);

    @Mock
    private ReanudarProyectoGradoInteractor interactor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private MapaRutaFechaFinCambiadaConsumer consumer;

    private UUID proyecto;
    private UUID mapa;

    @BeforeEach
    void setUp() {
        consumer = new MapaRutaFechaFinCambiadaConsumer(interactor, new ObjectMapper(), logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false), ZONA);
        proyecto = UtilUUID.generarNuevoUUID();
        mapa = UtilUUID.generarNuevoUUID();
        lenient().when(interactor.ejecutar(any())).thenReturn(new ReanudacionProyectoGradoResult.Reanudado(proyecto));
    }

    private Message mensaje(String proyectoPayload, long deliveryTag, boolean reentregado) {
        var json = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "2026-11-19T10:00:00Z",
                    "mapaRuta": "%s",
                    "proyectoGrado": "%s",
                    "fechaFin": "%s"
                }
                """.formatted(ID_EVENTO, mapa, proyectoPayload, FECHA_FIN);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @Test
    void debeInvocarInteractorConHoyDeLaZonaYLoguearReanudado_cuandoLlegaEvento() throws Exception {
        // Arrange
        var zona = ZoneId.of(ZONA);
        var antes = LocalDate.now(zona);

        // Act
        consumer.onMapaRutaFechaFinCambiada(mensaje(proyecto.toString(), 1L, false), channel);

        // Assert
        var despues = LocalDate.now(zona);
        var captor = ArgumentCaptor.forClass(ReanudarProyectoGradoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyecto);
        assertThat(captor.getValue().fechaFin()).isEqualTo(FECHA_FIN);
        assertThat(captor.getValue().hoy()).isBetween(antes, despues);
        verify(logger).info(ProyectoGradoKey.LOG_FECHA_FIN_CAMBIADA_RECIBIDA,
                ID_EVENTO, mapa.toString(), proyecto.toString(), FECHA_FIN);
        verify(logger).info(ProyectoGradoKey.LOG_REANUDADO, proyecto);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeLoguearOmitidoYConfirmar_cuandoElResultadoEsOmitido() throws Exception {
        // Arrange
        when(interactor.ejecutar(any()))
                .thenReturn(new ReanudacionProyectoGradoResult.Omitido(proyecto, "EN_PROCESO", true));

        // Act
        consumer.onMapaRutaFechaFinCambiada(mensaje(proyecto.toString(), 2L, false), channel);

        // Assert
        verify(logger).info(ProyectoGradoKey.LOG_REANUDACION_OMITIDA, proyecto, "EN_PROCESO", true);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeEnviarADlq_cuandoPayloadInvalido() throws Exception {
        // Act
        consumer.onMapaRutaFechaFinCambiada(mensaje("no-es-un-uuid", 3L, false), channel);

        // Assert
        verify(interactor, never()).ejecutar(any());
        verify(channel).basicNack(3L, false, false);
    }

    @Test
    void debeReencolar_cuandoFalloTransitorioEnPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onMapaRutaFechaFinCambiada(mensaje(proyecto.toString(), 4L, false), channel);

        // Assert
        verify(channel).basicNack(4L, false, true);
    }

    @Test
    void debeEnviarADlq_cuandoFalloTransitorioYaReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onMapaRutaFechaFinCambiada(mensaje(proyecto.toString(), 5L, true), channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
    }
}
