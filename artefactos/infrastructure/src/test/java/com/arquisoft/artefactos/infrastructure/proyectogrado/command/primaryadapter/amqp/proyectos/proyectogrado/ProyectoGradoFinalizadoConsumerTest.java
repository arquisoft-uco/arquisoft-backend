package com.arquisoft.artefactos.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.FinalizarProyectoGradoInteractor;
import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model.FinalizarProyectoGradoCommand;
import com.arquisoft.artefactos.application.proyectogrado.command.result.FinalizacionProyectoGradoResult;
import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.interactor.PurgarVersionesProyectoGradoInteractor;
import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model.PurgarVersionesProyectoGradoCommand;
import com.arquisoft.artefactos.application.versionartefacto.command.result.PurgaVersionesProyectoGradoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProyectoGradoFinalizadoConsumerTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-11-08T10:00:00Z");

    @Mock
    private FinalizarProyectoGradoInteractor finalizarProyectoGradoInteractor;
    @Mock
    private PurgarVersionesProyectoGradoInteractor purgarVersionesProyectoGradoInteractor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private ProyectoGradoFinalizadoConsumer consumer;

    private final UUID proyecto = UtilUUID.generarNuevoUUID();

    @BeforeEach
    void setUp() {
        consumer = new ProyectoGradoFinalizadoConsumer(
                finalizarProyectoGradoInteractor,
                purgarVersionesProyectoGradoInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
    }

    private Message mensaje(String proyectoGradoId, long deliveryTag, boolean reentregado) {
        var json = """
                {
                    "idEvento": "evt-finalizado-1",
                    "ocurridoEn": "%s",
                    "proyectoGradoId": "%s",
                    "tituloProyecto": "Sistema de gestion",
                    "estudiantes": []
                }
                """.formatted(OCURRIDO_EN, proyectoGradoId);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @Test
    void debeFinalizarYPurgarEnOrden_cuandoLlegaElEvento() throws Exception {
        // Arrange
        when(finalizarProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new FinalizacionProyectoGradoResult.Finalizado(proyecto));
        when(purgarVersionesProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new PurgaVersionesProyectoGradoResult(proyecto, 4));

        // Act
        consumer.onProyectoGradoFinalizado(mensaje(proyecto.toString(), 1L, false), channel);

        // Assert
        var orden = inOrder(finalizarProyectoGradoInteractor, purgarVersionesProyectoGradoInteractor, channel);
        orden.verify(finalizarProyectoGradoInteractor)
                .ejecutar(new FinalizarProyectoGradoCommand(proyecto, OCURRIDO_EN));
        orden.verify(purgarVersionesProyectoGradoInteractor)
                .ejecutar(new PurgarVersionesProyectoGradoCommand(proyecto));
        orden.verify(channel).basicAck(1L, false);
        verify(logger).info(ProyectoGradoKey.LOG_FINALIZADO, proyecto);
        verify(logger).info(VersionArtefactoKey.LOG_PURGADAS, proyecto, 4);
    }

    @Test
    void debePurgar_cuandoYaEstabaFinalizado() throws Exception {
        // Arrange
        when(finalizarProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new FinalizacionProyectoGradoResult.YaFinalizado(proyecto));
        when(purgarVersionesProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new PurgaVersionesProyectoGradoResult(proyecto, 0));

        // Act
        consumer.onProyectoGradoFinalizado(mensaje(proyecto.toString(), 2L, true), channel);

        // Assert
        verify(logger).info(ProyectoGradoKey.LOG_YA_FINALIZADO, proyecto);
        verify(purgarVersionesProyectoGradoInteractor).ejecutar(new PurgarVersionesProyectoGradoCommand(proyecto));
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeNoPurgar_cuandoFinalizarFalla() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(finalizarProyectoGradoInteractor).ejecutar(any());

        // Act
        consumer.onProyectoGradoFinalizado(mensaje(proyecto.toString(), 3L, false), channel);

        // Assert
        verify(purgarVersionesProyectoGradoInteractor, never()).ejecutar(any());
        verify(channel).basicNack(3L, false, true);
    }

    @Test
    void debeEnviarADlq_cuandoLaPurgaFallaYaReentregado() throws Exception {
        // Arrange
        when(finalizarProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new FinalizacionProyectoGradoResult.YaFinalizado(proyecto));
        doThrow(new QueryTimeoutException("timeout")).when(purgarVersionesProyectoGradoInteractor).ejecutar(any());

        // Act
        consumer.onProyectoGradoFinalizado(mensaje(proyecto.toString(), 4L, true), channel);

        // Assert
        verify(channel).basicNack(4L, false, false);
    }

    @Test
    void debeApartarADlq_cuandoElPayloadEsInvalido() throws Exception {
        // Act
        consumer.onProyectoGradoFinalizado(mensaje("no-uuid", 5L, false), channel);

        // Assert
        verify(finalizarProyectoGradoInteractor, never()).ejecutar(any());
        verify(channel).basicNack(5L, false, false);
    }
}
