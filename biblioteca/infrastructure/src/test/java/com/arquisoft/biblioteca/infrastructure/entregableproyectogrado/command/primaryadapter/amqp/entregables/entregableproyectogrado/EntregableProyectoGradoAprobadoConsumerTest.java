package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import com.arquisoft.biblioteca.application.entregableproyectogrado.command.primaryport.interactor.AgregarEntregableProyectoGradoInteractor;
import com.arquisoft.biblioteca.application.entregableproyectogrado.command.primaryport.model.AgregarEntregableProyectoGradoCommand;
import com.arquisoft.biblioteca.application.entregableproyectogrado.command.result.AgregacionEntregableProyectoGradoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.EntregableProyectoGradoKey;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntregableProyectoGradoAprobadoConsumerTest {

    private static final String ID_EVENTO = "evt-aprobado-1";
    private static final Instant OCURRIDO_EN = Instant.parse("2026-11-19T10:00:00Z");

    @Mock
    private AgregarEntregableProyectoGradoInteractor interactor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private EntregableProyectoGradoAprobadoConsumer consumer;

    private final UUID entregable = UtilUUID.generarNuevoUUID();
    private final UUID proyecto = UtilUUID.generarNuevoUUID();

    @BeforeEach
    void setUp() {
        consumer = new EntregableProyectoGradoAprobadoConsumer(
                interactor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        lenient().when(interactor.ejecutar(any()))
                .thenReturn(new AgregacionEntregableProyectoGradoResult.Agregado(entregable, proyecto));
    }

    private Message mensaje(String entregableEnPayload, long deliveryTag, boolean reentregado) {
        var json = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "%s",
                    "entregableProyectoGrado": "%s",
                    "proyectoGrado": "%s",
                    "version": 2,
                    "estadoEntregable": "APROBADO",
                    "estadoEntregableNombre": "Aprobado",
                    "bucket": "entregables",
                    "claveObjeto": "paquetes/a.zip",
                    "nombreArchivo": "a.zip"
                }
                """.formatted(ID_EVENTO, OCURRIDO_EN, entregableEnPayload, proyecto);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @Test
    void debeInvocarInteractorConLaUbicacionYConfirmar_cuandoLlegaElEvento() throws Exception {
        // Act
        consumer.onEntregableAprobado(mensaje(entregable.toString(), 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(AgregarEntregableProyectoGradoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new AgregarEntregableProyectoGradoCommand(
                entregable, proyecto, 2, "APROBADO", "paquetes/a.zip", "a.zip"));
        verify(logger).info(EntregableProyectoGradoKey.LOG_APROBADO_RECIBIDO,
                ID_EVENTO, entregable.toString(), proyecto.toString());
        verify(logger).info(EntregableProyectoGradoKey.LOG_AGREGADO, entregable, proyecto);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeRegistrarElDescarteYConfirmar_cuandoElInteractorDevuelveDescartado() throws Exception {
        // Arrange
        when(interactor.ejecutar(any()))
                .thenReturn(new AgregacionEntregableProyectoGradoResult.Descartado(entregable));

        // Act
        consumer.onEntregableAprobado(mensaje(entregable.toString(), 2L, false), channel);

        // Assert
        verify(logger).info(EntregableProyectoGradoKey.LOG_DESCARTADO, entregable);
        verify(logger, never()).info(eq(EntregableProyectoGradoKey.LOG_AGREGADO), any(), any());
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeEnviarNackSinReencolarNiInvocarElInteractor_cuandoElPayloadEsMalformado() throws Exception {
        // Act
        consumer.onEntregableAprobado(mensaje("no-es-un-uuid", 3L, false), channel);

        // Assert
        verify(interactor, never()).ejecutar(any());
        verify(channel).basicNack(3L, false, false);
        verify(channel, never()).basicAck(3L, false);
    }

    @Test
    void debeEnviarNackReencolando_cuandoElFalloEsTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onEntregableAprobado(mensaje(entregable.toString(), 4L, false), channel);

        // Assert
        verify(channel).basicNack(4L, false, true);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElFalloEsTransitorioYaReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onEntregableAprobado(mensaje(entregable.toString(), 5L, true), channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
        verify(channel, never()).basicAck(5L, false);
    }
}
