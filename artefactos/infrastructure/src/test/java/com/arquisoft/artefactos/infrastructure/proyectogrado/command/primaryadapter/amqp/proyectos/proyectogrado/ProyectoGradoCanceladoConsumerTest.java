package com.arquisoft.artefactos.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.interactor.PurgarPaquetesProyectoGradoInteractor;
import com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.model.PurgarPaquetesProyectoGradoCommand;
import com.arquisoft.artefactos.application.paqueteentregable.command.result.PurgaPaquetesProyectoGradoResult;
import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.CancelarProyectoGradoInteractor;
import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model.CancelarProyectoGradoCommand;
import com.arquisoft.artefactos.application.proyectogrado.command.result.CancelacionProyectoGradoResult;
import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.interactor.PurgarVersionesProyectoGradoInteractor;
import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model.PurgarVersionesProyectoGradoCommand;
import com.arquisoft.artefactos.application.versionartefacto.command.result.PurgaVersionesProyectoGradoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.PaqueteEntregableKey;
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
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProyectoGradoCanceladoConsumerTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-11-08T10:00:00Z");

    @Mock
    private CancelarProyectoGradoInteractor cancelarProyectoGradoInteractor;
    @Mock
    private PurgarVersionesProyectoGradoInteractor purgarVersionesProyectoGradoInteractor;
    @Mock
    private PurgarPaquetesProyectoGradoInteractor purgarPaquetesProyectoGradoInteractor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private ProyectoGradoCanceladoConsumer consumer;

    private final UUID proyecto = UtilUUID.generarNuevoUUID();

    @BeforeEach
    void setUp() {
        consumer = new ProyectoGradoCanceladoConsumer(
                cancelarProyectoGradoInteractor,
                purgarVersionesProyectoGradoInteractor,
                purgarPaquetesProyectoGradoInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
    }

    private Message mensaje(long deliveryTag) {
        var json = """
                {
                    "idEvento": "evt-cancelado-1",
                    "ocurridoEn": "%s",
                    "proyectoGradoId": "%s",
                    "tituloProyecto": "Sistema de gestion",
                    "motivo": "Falta de avance",
                    "estudiantes": [],
                    "asesores": []
                }
                """.formatted(OCURRIDO_EN, proyecto);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @Test
    void debeMarcarYPurgar_cuandoLlegaElEvento() throws Exception {
        // Arrange
        when(cancelarProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new CancelacionProyectoGradoResult.Cancelado(proyecto));
        when(purgarVersionesProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new PurgaVersionesProyectoGradoResult(proyecto, 4));
        when(purgarPaquetesProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new PurgaPaquetesProyectoGradoResult(proyecto, 3));

        // Act
        consumer.onProyectoGradoCancelado(mensaje(1L), channel);

        // Assert
        var orden = inOrder(cancelarProyectoGradoInteractor, purgarVersionesProyectoGradoInteractor,
                purgarPaquetesProyectoGradoInteractor, channel);
        orden.verify(cancelarProyectoGradoInteractor)
                .ejecutar(new CancelarProyectoGradoCommand(proyecto, OCURRIDO_EN));
        orden.verify(purgarVersionesProyectoGradoInteractor)
                .ejecutar(new PurgarVersionesProyectoGradoCommand(proyecto));
        orden.verify(purgarPaquetesProyectoGradoInteractor)
                .ejecutar(new PurgarPaquetesProyectoGradoCommand(proyecto));
        orden.verify(channel).basicAck(1L, false);
        verify(logger).info(ProyectoGradoKey.LOG_CANCELADO, proyecto);
        verify(logger).info(VersionArtefactoKey.LOG_PURGADAS, proyecto, 4);
        verify(logger).info(PaqueteEntregableKey.LOG_PROYECTO_PURGADOS, proyecto, 3);
    }

    @Test
    void debePurgarIgual_cuandoYaEstabaCancelado() throws Exception {
        // Arrange
        when(cancelarProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new CancelacionProyectoGradoResult.YaCancelado(proyecto));
        when(purgarVersionesProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new PurgaVersionesProyectoGradoResult(proyecto, 0));
        when(purgarPaquetesProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new PurgaPaquetesProyectoGradoResult(proyecto, 0));

        // Act
        consumer.onProyectoGradoCancelado(mensaje(2L), channel);

        // Assert
        verify(logger).info(ProyectoGradoKey.LOG_YA_CANCELADO, proyecto);
        verify(purgarVersionesProyectoGradoInteractor).ejecutar(new PurgarVersionesProyectoGradoCommand(proyecto));
        verify(purgarPaquetesProyectoGradoInteractor).ejecutar(new PurgarPaquetesProyectoGradoCommand(proyecto));
        verify(channel).basicAck(2L, false);
    }
}
