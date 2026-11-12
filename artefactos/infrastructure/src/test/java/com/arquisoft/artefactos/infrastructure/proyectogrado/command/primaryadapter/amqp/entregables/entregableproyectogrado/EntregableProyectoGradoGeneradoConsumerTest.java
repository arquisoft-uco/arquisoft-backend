package com.arquisoft.artefactos.infrastructure.proyectogrado.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.CongelarProyectoGradoInteractor;
import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model.CongelarProyectoGradoCommand;
import com.arquisoft.artefactos.application.proyectogrado.command.result.CongelacionProyectoGradoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.impl.GestorTrazaImpl;
import com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc.MdcContextoDiagnosticoOutputAdapter;
import com.arquisoft.shared.util.UtilUUID;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.dao.QueryTimeoutException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntregableProyectoGradoGeneradoConsumerTest {

    @Mock
    private CongelarProyectoGradoInteractor congelarProyectoGradoInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private EntregableProyectoGradoGeneradoConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new EntregableProyectoGradoGeneradoConsumer(
                congelarProyectoGradoInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
    }

    private Message mensajeCon(String proyecto, long deliveryTag, boolean redelivered) {
        var payloadJson = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "2026-11-08T10:00:00Z",
                    "entregableProyectoGrado": "%s",
                    "proyectoGrado": "%s",
                    "version": 2
                }
                """.formatted(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), proyecto);

        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(redelivered);

        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeCongelarYConfirmar_cuandoEventoValido() throws Exception {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        when(congelarProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new CongelacionProyectoGradoResult.Congelado(proyecto));

        // Act
        consumer.onEntregableProyectoGradoGenerado(mensajeCon(proyecto.toString(), 1L, false), channel);

        // Assert
        verify(congelarProyectoGradoInteractor).ejecutar(
                new CongelarProyectoGradoCommand(proyecto, Instant.parse("2026-11-08T10:00:00Z")));
        verify(logger).info(ProyectoGradoKey.LOG_CONGELADO, proyecto);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeRegistrarYaCongelado_cuandoElProyectoYaEstabaCongelado() throws Exception {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        when(congelarProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new CongelacionProyectoGradoResult.YaCongelado(proyecto));

        // Act
        consumer.onEntregableProyectoGradoGenerado(mensajeCon(proyecto.toString(), 2L, false), channel);

        // Assert
        verify(logger).info(ProyectoGradoKey.LOG_YA_CONGELADO, proyecto);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeApartarADlq_cuandoPayloadInvalido() throws Exception {
        // Act
        consumer.onEntregableProyectoGradoGenerado(mensajeCon("no-uuid", 3L, false), channel);

        // Assert
        verify(channel).basicNack(3L, false, false);
    }

    @Test
    void debeReencolar_cuandoFallaTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(congelarProyectoGradoInteractor).ejecutar(any());

        // Act
        consumer.onEntregableProyectoGradoGenerado(
                mensajeCon(UtilUUID.generarNuevoUUID().toString(), 4L, false), channel);

        // Assert
        verify(channel).basicNack(4L, false, true);
    }
}
