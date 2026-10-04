package com.arquisoft.solicitudes.application.solicitud.command.validator;

import com.arquisoft.solicitudes.application.solicitud.command.validator.impl.EliminarSolicitudValidatorImpl;
import com.arquisoft.solicitudes.domain.solicitud.EliminacionSolicitudDomain;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudConRespuestasException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEncontradaException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoPropiaException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideAsesorException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideException;
import com.arquisoft.solicitudes.domain.solicitud.model.ResumenSolicitud;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EliminarSolicitudValidatorImplTest {

    private final EliminarSolicitudValidatorImpl validator = new EliminarSolicitudValidatorImpl();

    private static EliminacionSolicitudDomain entrada(UUID solicitud, UUID solicitante, TipoSolicitud esperado) {
        return EliminacionSolicitudDomain.crear(solicitud, solicitante, esperado);
    }

    private static ResumenSolicitud resumen(UUID solicitud, UUID remitente, TipoSolicitud tipo) {
        return new ResumenSolicitud(solicitud, remitente, UUID.randomUUID(), tipo.getId());
    }

    @Test
    void debePasar_cuandoLasCuatroReglasSeCumplenParaElCoordinador() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        UUID solicitante = UUID.randomUUID();
        var tipo = TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR;

        // Act & Assert
        assertThatCode(() -> validator.validar(
                entrada(solicitud, solicitante, tipo), resumen(solicitud, solicitante, tipo), false))
                .doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoLasCuatroReglasSeCumplenParaElAsesor() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        UUID solicitante = UUID.randomUUID();
        var tipo = TipoSolicitud.NOVEDAD_PARA_EL_ASESOR;

        // Act & Assert
        assertThatCode(() -> validator.validar(
                entrada(solicitud, solicitante, tipo), resumen(solicitud, solicitante, tipo), false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSolicitudNoEncontrada_cuandoElResumenEsVacio() {
        // Arrange
        UUID solicitud = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(solicitud, UUID.randomUUID(), TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR),
                ResumenSolicitud.VACIO, false))
                .isInstanceOf(SolicitudNoEncontradaException.class)
                .hasMessageContaining(solicitud.toString());
    }

    @Test
    void debeLanzarSolicitudNoPropia_cuandoElRemitenteNoEsElSolicitante() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        var tipo = TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR;

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(solicitud, UUID.randomUUID(), tipo), resumen(solicitud, UUID.randomUUID(), tipo), false))
                .isInstanceOf(SolicitudNoPropiaException.class);
    }

    @Test
    void debeLanzarElErrorDelCoordinador_cuandoSeEsperabaCoordinadorYLaSolicitudEsDeOtroTipo() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        UUID solicitante = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(solicitud, solicitante, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR),
                resumen(solicitud, solicitante, TipoSolicitud.CAMBIO_DE_ASESOR), false))
                .isInstanceOf(SolicitudTipoNoCoincideException.class);
    }

    @Test
    void debeLanzarElErrorDelAsesor_cuandoSeEsperabaAsesorYLaSolicitudEsDeOtroTipo() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        UUID solicitante = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(solicitud, solicitante, TipoSolicitud.NOVEDAD_PARA_EL_ASESOR),
                resumen(solicitud, solicitante, TipoSolicitud.CAMBIO_DE_ASESOR), false))
                .isInstanceOf(SolicitudTipoNoCoincideAsesorException.class);
    }

    @Test
    void debeLanzarSolicitudConRespuestas_cuandoLaSolicitudTieneRespuestas() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        UUID solicitante = UUID.randomUUID();
        var tipo = TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR;

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(solicitud, solicitante, tipo), resumen(solicitud, solicitante, tipo), true))
                .isInstanceOf(SolicitudConRespuestasException.class);
    }
}
