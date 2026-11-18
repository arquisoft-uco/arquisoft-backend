package com.arquisoft.biblioteca.infrastructure.estudianteproyectogrado.command.primaryadapter.amqp.proyectos.estudianteproyectogrado;

import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.primaryport.interactor.AgregarEstudiantesProyectoGradoInteractor;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.primaryport.model.AgregarEstudiantesProyectoGradoCommand;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.result.AgregacionEstudiantesProyectoGradoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.EstudianteProyectoGradoKey;
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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EstudiantesProyectoGradoAsignadosConsumerTest {

    private static final String ID_EVENTO = "evt-asignados-1";
    private static final Instant OCURRIDO_EN = Instant.parse("2026-11-15T10:00:00Z");

    @Mock
    private AgregarEstudiantesProyectoGradoInteractor interactor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private EstudiantesProyectoGradoAsignadosConsumer consumer;

    private UUID proyecto;
    private UUID estudiante;

    @BeforeEach
    void setUp() {
        consumer = new EstudiantesProyectoGradoAsignadosConsumer(interactor, new ObjectMapper(), logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        proyecto = UtilUUID.generarNuevoUUID();
        estudiante = UtilUUID.generarNuevoUUID();
        lenient().when(interactor.ejecutar(any()))
                .thenReturn(new AgregacionEstudiantesProyectoGradoResult(proyecto, 1, 0, 0, 0));
    }

    private Message mensaje(String proyectoPayload, long deliveryTag, boolean reentregado) {
        var payloadJson = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "%s",
                    "proyectoGradoId": "%s",
                    "tituloProyecto": "Sistema de riego",
                    "estudiantes": [{"id": "%s", "nombre": "Ana", "email": "ana@uco.edu.co"}]
                }
                """.formatted(ID_EVENTO, OCURRIDO_EN, proyectoPayload, estudiante);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeInvocarElInteractorConLoQueTraeElPayloadYConfirmar_cuandoElEventoEsValido() throws Exception {
        // Act
        consumer.onEstudiantesAsignados(mensaje(proyecto.toString(), 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(AgregarEstudiantesProyectoGradoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(AgregarEstudiantesProyectoGradoCommand.crear(
                proyecto.toString(), "Sistema de riego", List.of(estudiante.toString()), OCURRIDO_EN));
        verify(logger).info(eq(EstudianteProyectoGradoKey.LOG_ASIGNADOS_RECIBIDO), eq(ID_EVENTO),
                eq(proyecto.toString()), eq(1));
        verify(logger).info(EstudianteProyectoGradoKey.LOG_AGREGADOS, proyecto, 1, 0, 0, 0);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeEnviarNackSinReencolarNiInvocarElInteractor_cuandoElPayloadEsEnvenenado() throws Exception {
        // Act
        consumer.onEstudiantesAsignados(mensaje("no-es-un-uuid", 2L, false), channel);

        // Assert
        verify(interactor, never()).ejecutar(any());
        verify(channel).basicNack(2L, false, false);
    }

    @Test
    void debeEnviarNackReencolandoSoloEnLaPrimeraEntrega_cuandoElFalloEsTransitorio() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onEstudiantesAsignados(mensaje(proyecto.toString(), 3L, false), channel);
        consumer.onEstudiantesAsignados(mensaje(proyecto.toString(), 4L, true), channel);

        // Assert
        verify(channel).basicNack(3L, false, true);
        verify(channel).basicNack(4L, false, false);
    }
}
