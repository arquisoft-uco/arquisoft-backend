package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.solicitudes.respuesta;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.application.notificacion.command.result.EnvioNotificacionResult;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.notificaciones.PlantillaKey;
import com.arquisoft.shared.message.prueba.CatalogoMensajesPrueba;
import com.arquisoft.shared.tracing.application.traza.primaryport.impl.GestorTrazaImpl;
import com.arquisoft.shared.util.UtilUUID;
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
import tools.jackson.databind.ObjectMapper;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SolicitudAmpliacionPlazoRespondidaConsumerTest {

    @Mock
    private EnviarNotificacionInteractor enviarNotificacionInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private SolicitudAmpliacionPlazoRespondidaConsumer adapter;

    @BeforeEach
    void setUp() {
        adapter = new SolicitudAmpliacionPlazoRespondidaConsumer(
                enviarNotificacionInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(enviarNotificacionInteractor.ejecutar(any()))
                .thenReturn(new EnvioNotificacionResult.Enviada("evt", "ana.est@soyuco.edu.co"));
    }

    private Message mensajeCon(String idEvento, long deliveryTag) {
        var payloadJson = """
                {
                    "idEvento": "%s",
                    "solicitudId": "%s",
                    "contenido": "Se aprueba el ampliacion de plazo",
                    "remitenteNombre": "Ana Estudiante",
                    "remitenteEmail": "ana.est@soyuco.edu.co",
                    "responsableNombre": "Pedro Coordinador"
                }
                """.formatted(idEvento, UtilUUID.generarNuevoUUID());

        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setHeader("X-Trace-Id", "trace-123");
        props.setHeader("X-User-Id", "user-456");

        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeNotificarAlRemitente_cuandoElPayloadEsValido() throws Exception {
        // Arrange
        var idEvento = UtilUUID.generarNuevoUUID().toString();

        // Act
        adapter.onSolicitudAmpliacionPlazoRespondida(mensajeCon(idEvento, 1L), channel);

        // Assert
        var captor =
                ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor).ejecutar(captor.capture());

        var command = captor.getValue();
        assertThat(command.idEvento()).isEqualTo(idEvento);
        assertThat(command.tipo()).isEqualTo(TipoNotificacion.SOLICITUD_AMPLIACION_PLAZO_RESPONDIDA);
        assertThat(command.destinatarioNombre()).isEqualTo("Ana Estudiante");
        assertThat(command.destinatarioEmail()).isEqualTo("ana.est@soyuco.edu.co");
        assertThat(command.asunto()).isNotEmpty();
        assertThat(command.cuerpo())
                .contains("Ana Estudiante")
                .contains("Pedro Coordinador")
                .contains("Se aprueba el ampliacion de plazo");
        assertThat(command.pie()).isNotEmpty();
    }

    @Test
    void debeConfirmarElMensaje_cuandoElProcesamientoTermina() throws Exception {
        // Act
        adapter.onSolicitudAmpliacionPlazoRespondida(mensajeCon(UtilUUID.generarNuevoUUID().toString(), 1L), channel);

        // Assert
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElInteractorFalla() throws Exception {
        // Arrange
        doThrow(new RuntimeException("fallo al notificar"))
                .when(enviarNotificacionInteractor).ejecutar(any());

        // Act
        adapter.onSolicitudAmpliacionPlazoRespondida(mensajeCon(UtilUUID.generarNuevoUUID().toString(), 2L), channel);

        // Assert
        verify(channel).basicNack(2L, false, false);
    }

    @Test
    void debeReencolarSinNotificar_cuandoLaPlantillaNoTieneTexto() throws Exception {
        // Arrange
        var sinPlantillas = new CatalogoMensajes() {
            @Override
            public String obtener(ClaveMensaje clave) {
                return CatalogoMensajesPrueba.porDefecto().obtener(clave);
            }

            @Override
            public String formatear(ClaveMensaje clave, Object... args) {
                return CatalogoMensajesPrueba.porDefecto().formatear(clave, args);
            }

            @Override
            public boolean contiene(ClaveMensaje clave) {
                return !(clave instanceof PlantillaKey);
            }
        };
        Mensajes.instalar(sinPlantillas);

        try {
            // Act
            adapter.onSolicitudAmpliacionPlazoRespondida(
                    mensajeCon(UtilUUID.generarNuevoUUID().toString(), 3L), channel);

            // Assert
            verify(enviarNotificacionInteractor, never()).ejecutar(any());
            verify(channel).basicNack(3L, false, true);
        } finally {
            Mensajes.instalar(CatalogoMensajesPrueba.porDefecto());
        }
    }
}
