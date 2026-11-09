package com.arquisoft.entregables.infrastructure.paqueteentregable.command.primaryadapter.amqp.artefactos.paqueteentregable;

import com.arquisoft.entregables.application.paqueteentregable.command.primaryport.interactor.AgregarPaqueteEntregableInteractor;
import com.arquisoft.entregables.application.paqueteentregable.command.primaryport.model.AgregarPaqueteEntregableCommand;
import com.arquisoft.entregables.application.paqueteentregable.command.result.AgregacionPaqueteEntregableResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.PaqueteEntregableKey;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaqueteEntregableGeneradoConsumerTest {

    @Mock
    private AgregarPaqueteEntregableInteractor agregarPaqueteEntregableInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private PaqueteEntregableGeneradoConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new PaqueteEntregableGeneradoConsumer(
                agregarPaqueteEntregableInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(agregarPaqueteEntregableInteractor.ejecutar(any()))
                .thenReturn(new AgregacionPaqueteEntregableResult.Agregado(UtilUUID.generarNuevoUUID()));
    }

    private Message mensajeCon(UUID entregable, long deliveryTag, boolean redelivered) {
        var payloadJson = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "2026-11-08T10:00:00Z",
                    "paqueteEntregable": "%s",
                    "entregableProyectoGrado": "%s",
                    "bucket": "bucket",
                    "claveObjeto": "paquetes/x.zip",
                    "nombreArchivo": "p.zip",
                    "tamanoBytes": 2048,
                    "generadoEn": "2026-11-08T09:59:00Z",
                    "artefactos": [{
                        "artefacto": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
                        "numeroVersion": 3,
                        "versionRepositorioArtefacto": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb",
                        "nombreDocumento": "Informe",
                        "repositorio": "cccccccc-cccc-cccc-cccc-cccccccccccc",
                        "nombreRepositorio": "Repo",
                        "rutaEnPaquete": "01-Informe/v3-informe.pdf"
                    }]
                }
                """.formatted(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), entregable);

        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(redelivered);

        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debePasarLosArtefactosAlCommandYConfirmar_cuandoEventoValido() throws Exception {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();

        // Act
        consumer.onPaqueteEntregableGenerado(mensajeCon(entregable, 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(AgregarPaqueteEntregableCommand.class);
        verify(agregarPaqueteEntregableInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().entregableProyectoGrado()).isEqualTo(entregable);
        assertThat(captor.getValue().bucket()).isEqualTo("bucket");
        assertThat(captor.getValue().claveObjeto()).isEqualTo("paquetes/x.zip");
        assertThat(captor.getValue().nombreArchivo()).isEqualTo("p.zip");
        assertThat(captor.getValue().tamanoBytes()).isEqualTo(2048L);
        assertThat(captor.getValue().generadoEn()).isEqualTo(Instant.parse("2026-11-08T09:59:00Z"));
        assertThat(captor.getValue().artefactos()).singleElement().satisfies(item -> {
            assertThat(item.artefacto()).hasToString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
            assertThat(item.versionRepositorioArtefacto()).hasToString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
            assertThat(item.repositorio()).hasToString("cccccccc-cccc-cccc-cccc-cccccccccccc");
            assertThat(item.nombreDocumento()).isEqualTo("Informe");
            assertThat(item.nombreRepositorio()).isEqualTo("Repo");
            assertThat(item.rutaEnPaquete()).isEqualTo("01-Informe/v3-informe.pdf");
        });
        verify(logger).info(eq(PaqueteEntregableKey.LOG_AGREGADO), any());
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeConfirmarYRegistrarDuplicado_cuandoElPaqueteYaExiste() throws Exception {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        when(agregarPaqueteEntregableInteractor.ejecutar(any()))
                .thenReturn(new AgregacionPaqueteEntregableResult.Duplicado(entregable));

        // Act
        consumer.onPaqueteEntregableGenerado(mensajeCon(entregable, 2L, false), channel);

        // Assert
        verify(logger).info(PaqueteEntregableKey.LOG_DUPLICADO, entregable);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeReencolar_cuandoFallaTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(agregarPaqueteEntregableInteractor).ejecutar(any());

        // Act
        consumer.onPaqueteEntregableGenerado(mensajeCon(UtilUUID.generarNuevoUUID(), 3L, false), channel);

        // Assert
        verify(channel).basicNack(3L, false, true);
    }

    @Test
    void debeEnviarADlq_cuandoFallaTransitorioYaReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(agregarPaqueteEntregableInteractor).ejecutar(any());

        // Act
        consumer.onPaqueteEntregableGenerado(mensajeCon(UtilUUID.generarNuevoUUID(), 4L, true), channel);

        // Assert
        verify(channel).basicNack(4L, false, false);
    }

    @Test
    void debeEnviarADlq_cuandoElPayloadEsEnvenenado() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("invalido")).when(agregarPaqueteEntregableInteractor).ejecutar(any());

        // Act
        consumer.onPaqueteEntregableGenerado(mensajeCon(UtilUUID.generarNuevoUUID(), 5L, false), channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
    }
}
