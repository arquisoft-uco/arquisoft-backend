package com.arquisoft.proyectos.infrastructure.proyectogrado.command.primaryadapter.amqp.fichas.fichaperfil;

import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.interactor.RegistrarProyectoGradoInteractor;
import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model.RegistrarProyectoGradoCommand;
import com.arquisoft.proyectos.application.proyectogrado.command.result.RegistroProyectoGradoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.impl.GestorTrazaImpl;
import com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc.MdcContextoDiagnosticoOutputAdapter;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FichaPerfilAprobadaConsumerTest {

    @Mock
    private RegistrarProyectoGradoInteractor interactor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private FichaPerfilAprobadaConsumer adapter;

    private final UUID fichaPerfil = UUID.randomUUID();
    private final UUID coordinador = UUID.randomUUID();
    private final UUID estudiante1 = UUID.randomUUID();
    private final UUID estudiante2 = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        adapter = new FichaPerfilAprobadaConsumer(
                interactor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(interactor.ejecutar(any()))
                .thenReturn(new RegistroProyectoGradoResult.Registrado(UUID.randomUUID(), fichaPerfil));
    }

    private Message mensaje(long deliveryTag, boolean reentregado) {
        var payloadJson = """
                {
                    "idEvento": "evt-1",
                    "ocurridoEn": "2026-10-03T10:00:00Z",
                    "fichaPerfilId": "%s",
                    "tituloProyecto": "Sistema de gestión",
                    "estadoFicha": "APROBADA",
                    "coordinadorId": "%s",
                    "asesor": {"nombre": "Carlos Ruiz", "email": "carlos.ruiz@soyuco.edu.co"},
                    "estudiantes": [
                        {"estudiante": "%s", "contacto": {"nombre": "Ana", "email": "ana@soyuco.edu.co"}},
                        {"estudiante": "%s", "contacto": {"nombre": "Luis", "email": "luis@soyuco.edu.co"}}
                    ]
                }
                """.formatted(fichaPerfil, coordinador, estudiante1, estudiante2);

        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        props.setHeader("X-Trace-Id", "trace-123");

        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeRegistrarConLosDatosDelPayloadYConfirmar_cuandoElResultadoEsRegistrado() throws Exception {
        // Act
        adapter.onFichaPerfilAprobada(mensaje(1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(RegistrarProyectoGradoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(captor.getValue().tituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(captor.getValue().coordinador()).isEqualTo(coordinador);
        assertThat(captor.getValue().estudiantes()).containsExactly(estudiante1, estudiante2);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeRegistrarDuplicadoYConfirmar_cuandoLaFichaYaTeniaProyecto() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(new RegistroProyectoGradoResult.Duplicado(fichaPerfil));

        // Act
        adapter.onFichaPerfilAprobada(mensaje(2L, false), channel);

        // Assert
        verify(logger).info(ProyectoGradoKey.LOG_DUPLICADO, fichaPerfil);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeReencolarUnaVez_cuandoFallaLaBaseEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        adapter.onFichaPerfilAprobada(mensaje(3L, false), channel);

        // Assert
        verify(channel).basicNack(3L, false, true);
    }

    @Test
    void debeEnviarALaDlq_cuandoFallaLaBaseEnUnaReentrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        adapter.onFichaPerfilAprobada(mensaje(4L, true), channel);

        // Assert
        verify(channel).basicNack(4L, false, false);
    }

    @Test
    void debeEnviarALaDlqSinReintentar_cuandoElMensajeEsEnvenenado() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("payload invalido")).when(interactor).ejecutar(any());

        // Act
        adapter.onFichaPerfilAprobada(mensaje(5L, false), channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
    }
}
