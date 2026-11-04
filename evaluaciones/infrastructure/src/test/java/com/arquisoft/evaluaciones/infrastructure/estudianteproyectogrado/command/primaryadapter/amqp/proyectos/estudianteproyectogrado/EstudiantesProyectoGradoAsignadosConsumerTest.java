package com.arquisoft.evaluaciones.infrastructure.estudianteproyectogrado.command.primaryadapter.amqp.proyectos.estudianteproyectogrado;

import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.primaryport.interactor.AgregarEstudiantesProyectoGradoInteractor;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.primaryport.model.AgregarEstudiantesProyectoGradoCommand;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.result.AgregacionEstudiantesProyectoGradoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EstudianteProyectoGradoKey;
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
    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-20T10:00:00Z");

    @Mock
    private AgregarEstudiantesProyectoGradoInteractor interactor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private EstudiantesProyectoGradoAsignadosConsumer consumer;

    private final UUID proyectoGrado = UtilUUID.generarNuevoUUID();
    private final UUID primero = UtilUUID.generarNuevoUUID();
    private final UUID segundo = UtilUUID.generarNuevoUUID();

    @BeforeEach
    void setUp() {
        consumer = new EstudiantesProyectoGradoAsignadosConsumer(
                interactor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        lenient().when(interactor.ejecutar(any()))
                .thenReturn(new AgregacionEstudiantesProyectoGradoResult(proyectoGrado, 2, 0, 0, 0));
    }

    private Message mensaje(String proyectoPayload, long deliveryTag, boolean reentregado) {
        var json = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "%s",
                    "proyectoGradoId": "%s",
                    "tituloProyecto": "Sistema",
                    "estudiantes": [
                        {"id": "%s", "nombre": "Ana", "email": "ana@uco.edu.co"},
                        {"id": "%s", "nombre": "Luis", "email": "luis@uco.edu.co"}
                    ]
                }
                """.formatted(ID_EVENTO, OCURRIDO_EN, proyectoPayload, primero, segundo);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @Test
    void debeInvocarInteractor_cuandoLlegaElEvento() throws Exception {
        // Act
        consumer.onEstudiantesAsignados(mensaje(proyectoGrado.toString(), 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(AgregarEstudiantesProyectoGradoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new AgregarEstudiantesProyectoGradoCommand(
                proyectoGrado, List.of(primero, segundo), OCURRIDO_EN));
        verify(logger).info(EstudianteProyectoGradoKey.LOG_ASIGNADOS_RECIBIDO, ID_EVENTO,
                proyectoGrado.toString(), 2);
        verify(logger).info(eq(EstudianteProyectoGradoKey.LOG_AGREGADOS), eq(proyectoGrado), eq(2), eq(0), eq(0),
                eq(0));
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeEnviarNackSinReencolarNiInvocarElInteractor_cuandoElPayloadEsMalformado() throws Exception {
        // Act
        consumer.onEstudiantesAsignados(mensaje("no-es-un-uuid", 2L, false), channel);

        // Assert
        verify(interactor, never()).ejecutar(any());
        verify(channel).basicNack(2L, false, false);
        verify(channel, never()).basicAck(2L, false);
    }

    @Test
    void debeEnviarNackReencolando_cuandoElFalloEsTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onEstudiantesAsignados(mensaje(proyectoGrado.toString(), 3L, false), channel);

        // Assert
        verify(channel).basicNack(3L, false, true);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElFalloEsTransitorioYaReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onEstudiantesAsignados(mensaje(proyectoGrado.toString(), 4L, true), channel);

        // Assert
        verify(channel).basicNack(4L, false, false);
        verify(channel, never()).basicAck(4L, false);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElFalloNoEsTransitorio() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("envenenado")).when(interactor).ejecutar(any());

        // Act
        consumer.onEstudiantesAsignados(mensaje(proyectoGrado.toString(), 5L, false), channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
    }
}
