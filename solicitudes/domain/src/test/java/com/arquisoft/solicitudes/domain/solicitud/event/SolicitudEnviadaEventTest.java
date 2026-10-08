package com.arquisoft.solicitudes.domain.solicitud.event;

import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.solicitudes.domain.tiposolicitud.exception.TipoSolicitudNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitudEnviadaEventTest {

    @Test
    void debeAsignarTodosLosCampos_cuandoSeConstruye() {
        // Arrange
        var solicitudId = UUID.randomUUID();

        // Act
        var evento = new SolicitudEnviadaEvent(
                TipoSolicitud.NOVEDAD_PARA_EL_ASESOR, solicitudId, "Ana Estudiante", "Pedro Asesor",
                "pedro@uco.edu.co", "novedad para el asesor");

        // Assert
        assertThat(evento.getSolicitudId()).isEqualTo(solicitudId);
        assertThat(evento.getRemitenteNombre()).isEqualTo("Ana Estudiante");
        assertThat(evento.getDestinatarioNombre()).isEqualTo("Pedro Asesor");
        assertThat(evento.getDestinatarioEmail()).isEqualTo("pedro@uco.edu.co");
        assertThat(evento.getMensajeSolicitud()).isEqualTo("novedad para el asesor");
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }

    @ParameterizedTest
    @CsvSource({
            "NOVEDAD_PARA_EL_ASESOR," + EventTopics.Solicitudes.NOVEDAD_ASESOR_ENVIADA
                    + ",SolicitudNovedadAsesorEnviadaEvent",
            "NOVEDAD_PARA_EL_COORDINADOR," + EventTopics.Solicitudes.NOVEDAD_COORDINADOR_ENVIADA
                    + ",SolicitudNovedadCoordinadorEnviadaEvent",
            "CAMBIO_DE_ASESOR," + EventTopics.Solicitudes.CAMBIO_ASESOR_ENVIADA
                    + ",SolicitudCambioAsesorEnviadaEvent",
            "AMPLIACION_DE_PLAZO," + EventTopics.Solicitudes.AMPLIACION_PLAZO_ENVIADA
                    + ",SolicitudAmpliacionPlazoEnviadaEvent"
    })
    void debeExponerElTemaYElTipoDeEventoDelTipoDeSolicitud(
            TipoSolicitud tipo, String temaEsperado, String tipoEventoEsperado) {
        // Act
        var evento = new SolicitudEnviadaEvent(
                tipo, UUID.randomUUID(), "Ana Estudiante", "Pedro Asesor", "pedro@uco.edu.co", "mensaje");

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo(temaEsperado);
        assertThat(evento.getTipoEvento()).isEqualTo(tipoEventoEsperado);
    }

    @Test
    void debeConservarLosTemasPublicadosHastaAhora_paraNoRomperLasColasDeNotificaciones() {
        // Act & Assert
        assertThat(new SolicitudEnviadaEvent(TipoSolicitud.NOVEDAD_PARA_EL_ASESOR, UUID.randomUUID(),
                "a", "b", "c@uco.edu.co", "m").getTemaEvento())
                .isEqualTo("solicitudes.solicitud.novedad_asesor_enviada");
    }

    @Test
    void debeLanzarTipoNoEncontrado_cuandoElTipoNoSeEnvia() {
        // Act & Assert
        assertThatThrownBy(() -> new SolicitudEnviadaEvent(
                TipoSolicitud.REGISTRO_Y_MODIFICACION_DE_USUARIOS, UUID.randomUUID(), "a", "b",
                "c@uco.edu.co", "m"))
                .isInstanceOf(TipoSolicitudNoEncontradoException.class);
    }

    @Test
    void debeLanzarTipoNoEncontrado_cuandoElTipoEsElCentinelaVacio() {
        // Act & Assert
        assertThatThrownBy(() -> new SolicitudEnviadaEvent(
                TipoSolicitud.VACIO, UUID.randomUUID(), "a", "b", "c@uco.edu.co", "m"))
                .isInstanceOf(TipoSolicitudNoEncontradoException.class);
    }
}
