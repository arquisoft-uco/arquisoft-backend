package com.arquisoft.entregables.infrastructure.artefacto.command.primaryadapter.amqp.artefactos.revisionasesor;

import com.arquisoft.entregables.application.artefacto.command.primaryport.interactor.RegistrarVersionCerradaArtefactoInteractor;
import com.arquisoft.entregables.application.artefacto.command.primaryport.model.RegistrarVersionCerradaArtefactoCommand;
import com.arquisoft.entregables.application.artefacto.command.result.RegistroVersionCerradaArtefactoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ArtefactoKey;
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
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RevisionAsesorCerradaConsumerTest {

    @Mock
    private RegistrarVersionCerradaArtefactoInteractor interactor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private RevisionAsesorCerradaConsumer consumer;

    private final UUID artefacto = UtilUUID.generarNuevoUUID();
    private final UUID proyectoGrado = UtilUUID.generarNuevoUUID();

    @BeforeEach
    void setUp() {
        consumer = new RevisionAsesorCerradaConsumer(
                interactor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
    }

    private Message mensaje(String numeroVersion, long deliveryTag) {
        var json = """
                {
                    "idEvento": "evt-1",
                    "ocurridoEn": "2026-11-10T10:00:00Z",
                    "documento": "Anteproyecto",
                    "numeroVersion": %s,
                    "proyectoGrado": "%s",
                    "artefacto": "%s",
                    "versionArtefacto": "%s",
                    "uriArchivo": "pendientes/a.pdf",
                    "fechaVersion": "2026-11-09T08:00:00Z",
                    "versionRepositorioArtefacto": "%s",
                    "repositorio": "%s",
                    "nombreRepositorio": "Repositorio",
                    "asesor": {"nombre": "Mario", "email": "mario@uco.edu.co"},
                    "estudiantes": []
                }
                """.formatted(numeroVersion, proyectoGrado, artefacto, UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @Test
    void debeRegistrarConLosDatosDelEventoYConfirmar_cuandoLaVersionEsNueva() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(new RegistroVersionCerradaArtefactoResult.Registrada(artefacto));

        // Act
        consumer.onRevisionAsesorCerrada(mensaje("3", 1L), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(RegistrarVersionCerradaArtefactoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().artefacto()).isEqualTo(artefacto);
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(captor.getValue().documento()).isEqualTo("Anteproyecto");
        assertThat(captor.getValue().numeroVersion()).isEqualTo(3);
        assertThat(captor.getValue().uriArchivo()).isEqualTo("pendientes/a.pdf");
        assertThat(captor.getValue().fechaVersion()).isEqualTo(Instant.parse("2026-11-09T08:00:00Z"));
        assertThat(captor.getValue().nombreRepositorio()).isEqualTo("Repositorio");
        verify(logger).info(ArtefactoKey.LOG_REGISTRADA, artefacto);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeConfirmarSinMasEfectos_cuandoElResultadoEsObsoleta() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(new RegistroVersionCerradaArtefactoResult.Obsoleta(artefacto));

        // Act
        consumer.onRevisionAsesorCerrada(mensaje("1", 2L), channel);

        // Assert
        verify(logger).info(ArtefactoKey.LOG_OBSOLETA, artefacto);
        verify(logger, never()).info(ArtefactoKey.LOG_REGISTRADA, artefacto);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElPayloadEsInvalido() throws Exception {
        // Act
        consumer.onRevisionAsesorCerrada(mensaje("null", 3L), channel);

        // Assert
        verify(interactor, never()).ejecutar(any());
        verify(channel).basicNack(3L, false, false);
    }
}
