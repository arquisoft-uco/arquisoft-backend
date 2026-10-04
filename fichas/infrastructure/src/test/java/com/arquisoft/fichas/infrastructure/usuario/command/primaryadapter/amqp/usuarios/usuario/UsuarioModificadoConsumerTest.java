package com.arquisoft.fichas.infrastructure.usuario.command.primaryadapter.amqp.usuarios.usuario;

import com.arquisoft.fichas.application.asesorficha.command.primaryport.interactor.ActualizarAsesorFichaInteractor;
import com.arquisoft.fichas.application.asesorficha.command.result.ActualizacionAsesorFichaResult;
import com.arquisoft.fichas.application.estudiante.command.primaryport.interactor.ActualizarEstudianteInteractor;
import com.arquisoft.fichas.application.estudiante.command.result.ActualizacionEstudianteResult;
import com.arquisoft.fichas.application.representantecomite.command.primaryport.interactor.ActualizarRepresentanteComiteInteractor;
import com.arquisoft.fichas.application.representantecomite.command.primaryport.model.ActualizarRepresentanteComiteCommand;
import com.arquisoft.fichas.application.representantecomite.command.result.ActualizacionRepresentanteComiteResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.RepresentanteComiteKey;
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

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioModificadoConsumerTest {

    private static final Instant OCURRIDO_EN_EVENTO = Instant.parse("2026-09-16T10:00:00Z");

    @Mock
    private ActualizarEstudianteInteractor actualizarEstudianteInteractor;
    @Mock
    private ActualizarAsesorFichaInteractor actualizarAsesorFichaInteractor;
    @Mock
    private ActualizarRepresentanteComiteInteractor actualizarRepresentanteComiteInteractor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private UsuarioModificadoConsumer adapter;

    @BeforeEach
    void setUp() {
        adapter = new UsuarioModificadoConsumer(
                actualizarEstudianteInteractor,
                actualizarAsesorFichaInteractor,
                actualizarRepresentanteComiteInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(actualizarEstudianteInteractor.ejecutar(any()))
                .thenReturn(new ActualizacionEstudianteResult.Actualizada(UUID.randomUUID()));
        lenient().when(actualizarAsesorFichaInteractor.ejecutar(any()))
                .thenReturn(new ActualizacionAsesorFichaResult.Actualizada(UUID.randomUUID()));
        lenient().when(actualizarRepresentanteComiteInteractor.ejecutar(any()))
                .thenReturn(new ActualizacionRepresentanteComiteResult.Actualizada(UUID.randomUUID()));
    }

    private Message mensajeCon(String idEvento, long deliveryTag, boolean reentregado) {
        var payloadJson = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "%s",
                    "usuario": "%s",
                    "identificador": "20161020999",
                    "nombre": "Ana Actualizada",
                    "email": "actualizada@uco.edu.co"
                }
                """.formatted(idEvento, OCURRIDO_EN_EVENTO, UUID.randomUUID());

        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        props.setHeader("X-Trace-Id", "trace-123");
        props.setHeader("X-User-Id", "user-456");

        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeInvocarLosTresInteractores_cuandoLlegaElEvento() throws Exception {
        // Arrange
        var representanteComite = UUID.randomUUID();
        when(actualizarRepresentanteComiteInteractor.ejecutar(any()))
                .thenReturn(new ActualizacionRepresentanteComiteResult.Actualizada(representanteComite));

        // Act
        adapter.onUsuarioModificado(mensajeCon(UUID.randomUUID().toString(), 1L, false), channel);

        // Assert
        verify(actualizarEstudianteInteractor).ejecutar(any());
        verify(actualizarAsesorFichaInteractor).ejecutar(any());
        var captor = ArgumentCaptor.forClass(ActualizarRepresentanteComiteCommand.class);
        verify(actualizarRepresentanteComiteInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().identificador()).isEqualTo("20161020999");
        assertThat(captor.getValue().nombre()).isEqualTo("Ana Actualizada");
        assertThat(captor.getValue().email()).isEqualTo("actualizada@uco.edu.co");
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(OCURRIDO_EN_EVENTO);
        verify(logger).info(RepresentanteComiteKey.LOG_ACTUALIZADO, representanteComite);
    }

    @Test
    void debeRegistrarYConfirmar_cuandoLaActualizacionDelRepresentanteEsDescartada() throws Exception {
        // Arrange
        var representanteComite = UUID.randomUUID();
        var ocurridoEnVigente = Instant.parse("2026-09-20T10:00:00Z");
        when(actualizarRepresentanteComiteInteractor.ejecutar(any())).thenReturn(
                new ActualizacionRepresentanteComiteResult.Descartada(representanteComite, ocurridoEnVigente));

        // Act
        adapter.onUsuarioModificado(mensajeCon(UUID.randomUUID().toString(), 7L, false), channel);

        // Assert
        verify(logger).info(eq(RepresentanteComiteKey.LOG_ACTUALIZACION_DESCARTADA), eq(representanteComite),
                eq(ocurridoEnVigente), eq(OCURRIDO_EN_EVENTO));
        verify(channel).basicAck(7L, false);
    }

    @Test
    void debeRegistrarEnDebugYConfirmar_cuandoElRepresentanteNoEstaReplicado() throws Exception {
        // Arrange
        var representanteComite = UUID.randomUUID();
        when(actualizarRepresentanteComiteInteractor.ejecutar(any())).thenReturn(
                new ActualizacionRepresentanteComiteResult.NoReplicado(representanteComite));

        // Act
        adapter.onUsuarioModificado(mensajeCon(UUID.randomUUID().toString(), 8L, false), channel);

        // Assert
        verify(logger).debug(RepresentanteComiteKey.LOG_ACTUALIZACION_NO_REPLICADO, representanteComite);
        verify(channel).basicAck(8L, false);
    }

    @Test
    void debeConfirmarElMensaje_cuandoAmbosResultadosSonActualizada() throws Exception {
        // Act
        adapter.onUsuarioModificado(mensajeCon(UUID.randomUUID().toString(), 1L, false), channel);

        // Assert
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeConfirmarElMensaje_cuandoElResultadoEsDescartada() throws Exception {
        // Arrange
        when(actualizarEstudianteInteractor.ejecutar(any())).thenReturn(
                new ActualizacionEstudianteResult.Descartada(UUID.randomUUID(), Instant.now()));

        // Act
        adapter.onUsuarioModificado(mensajeCon(UUID.randomUUID().toString(), 2L, false), channel);

        // Assert
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeConfirmarElMensaje_cuandoElResultadoEsNoReplicado() throws Exception {
        // Arrange
        when(actualizarAsesorFichaInteractor.ejecutar(any())).thenReturn(
                new ActualizacionAsesorFichaResult.NoReplicado(UUID.randomUUID()));

        // Act
        adapter.onUsuarioModificado(mensajeCon(UUID.randomUUID().toString(), 3L, false), channel);

        // Assert
        verify(channel).basicAck(3L, false);
    }

    @Test
    void debeEnviarNackConReintento_cuandoElFalloEsTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(actualizarEstudianteInteractor).ejecutar(any());

        // Act
        adapter.onUsuarioModificado(mensajeCon(UUID.randomUUID().toString(), 4L, false), channel);

        // Assert
        verify(channel).basicNack(4L, false, true);
    }

    @Test
    void debeEnviarNackSinReintento_cuandoElFalloEsTransitorioYaReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(actualizarEstudianteInteractor).ejecutar(any());

        // Act
        adapter.onUsuarioModificado(mensajeCon(UUID.randomUUID().toString(), 5L, true), channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElPayloadEsEnvenenado() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("id invalido")).when(actualizarEstudianteInteractor).ejecutar(any());

        // Act
        adapter.onUsuarioModificado(mensajeCon(UUID.randomUUID().toString(), 6L, false), channel);

        // Assert
        verify(channel).basicNack(6L, false, false);
    }
}
