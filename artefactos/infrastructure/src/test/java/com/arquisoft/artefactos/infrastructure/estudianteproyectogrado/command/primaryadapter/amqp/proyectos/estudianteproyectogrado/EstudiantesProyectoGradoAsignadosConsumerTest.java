package com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.primaryadapter.amqp.proyectos.estudianteproyectogrado;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.primaryport.interactor.AgregarEstudiantesProyectoGradoInteractor;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.primaryport.model.AgregarEstudiantesProyectoGradoCommand;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.result.AgregacionEstudiantesProyectoGradoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EstudianteProyectoGradoKey;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudiantesProyectoGradoAsignadosConsumerTest {

    private static final String ID_EVENTO = "evt-asignados-1";
    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-21T10:00:00Z");

    @Mock
    private AgregarEstudiantesProyectoGradoInteractor interactor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private EstudiantesProyectoGradoAsignadosConsumer consumer;

    private final UUID proyectoGrado = UtilUUID.generarNuevoUUID();

    @BeforeEach
    void setUp() {
        consumer = new EstudiantesProyectoGradoAsignadosConsumer(
                interactor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
    }

    private Message mensaje(String estudiantesJson, long deliveryTag, boolean reentregado) {
        var json = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "%s",
                    "proyectoGradoId": "%s",
                    "tituloProyecto": "Sistema",
                    "estudiantes": %s
                }
                """.formatted(ID_EVENTO, OCURRIDO_EN, proyectoGrado, estudiantesJson);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    private String estudiantesJson(UUID... ids) {
        var elementos = java.util.Arrays.stream(ids)
                .map(id -> "{\"id\":\"%s\",\"nombre\":\"Ignorado\"}".formatted(id))
                .toList();
        return "[" + String.join(",", elementos) + "]";
    }

    @Test
    void debeArmarElComandoConTodosLosEstudiantesYConfirmar_cuandoLlegaElEvento() throws Exception {
        // Arrange
        var primero = UtilUUID.generarNuevoUUID();
        var segundo = UtilUUID.generarNuevoUUID();
        var tercero = UtilUUID.generarNuevoUUID();
        var resultado = new AgregacionEstudiantesProyectoGradoResult(proyectoGrado, 3, 0, 0, 0);
        when(interactor.ejecutar(any())).thenReturn(resultado);

        // Act
        consumer.onEstudiantesProyectoGradoAsignados(
                mensaje(estudiantesJson(primero, segundo, tercero), 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(AgregarEstudiantesProyectoGradoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new AgregarEstudiantesProyectoGradoCommand(
                proyectoGrado, java.util.List.of(primero, segundo, tercero), OCURRIDO_EN));
        verify(logger).info(EstudianteProyectoGradoKey.LOG_AGREGADOS, proyectoGrado, 3, 0, 0, 0);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeEnviarNackSinReencolarNiInvocarElInteractor_cuandoLaListaEstaVaciaOUnIdEsInvalido() throws Exception {
        // Act
        consumer.onEstudiantesProyectoGradoAsignados(mensaje("[]", 2L, false), channel);
        consumer.onEstudiantesProyectoGradoAsignados(mensaje("[{\"id\":\"no-es-un-uuid\"}]", 3L, false), channel);

        // Assert
        verify(interactor, never()).ejecutar(any());
        verify(channel).basicNack(2L, false, false);
        verify(channel).basicNack(3L, false, false);
    }

    @Test
    void debeEnviarNackReencolando_cuandoElFalloEsTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onEstudiantesProyectoGradoAsignados(
                mensaje(estudiantesJson(UtilUUID.generarNuevoUUID()), 4L, false), channel);

        // Assert
        verify(channel).basicNack(4L, false, true);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElFalloEsTransitorioYaReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onEstudiantesProyectoGradoAsignados(
                mensaje(estudiantesJson(UtilUUID.generarNuevoUUID()), 5L, true), channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
        verify(channel, never()).basicAck(5L, false);
    }
}
