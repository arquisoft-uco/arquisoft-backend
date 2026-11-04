package com.arquisoft.solicitudes.application.solicitud.command.validator;

import com.arquisoft.solicitudes.application.solicitud.command.validator.impl.EliminarSolicitudValidatorImpl;
import com.arquisoft.solicitudes.domain.solicitud.EliminacionSolicitudDomain;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudConRespuestasException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEncontradaException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoPropiaException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideAmpliacionPlazoException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideAsesorException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideCambioAsesorException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideRegistroModificacionUsuariosException;
import com.arquisoft.solicitudes.domain.solicitud.model.ResumenSolicitud;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import com.arquisoft.shared.util.UtilUUID;

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
        var solicitud = UUID.randomUUID();
        var solicitante = UUID.randomUUID();
        var tipo = TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR;

        // Act & Assert
        assertThatCode(() -> validator.validar(
                entrada(solicitud, solicitante, tipo), resumen(solicitud, solicitante, tipo), false))
                .doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoLasCuatroReglasSeCumplenParaElAsesor() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var solicitante = UUID.randomUUID();
        var tipo = TipoSolicitud.NOVEDAD_PARA_EL_ASESOR;

        // Act & Assert
        assertThatCode(() -> validator.validar(
                entrada(solicitud, solicitante, tipo), resumen(solicitud, solicitante, tipo), false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSolicitudNoEncontrada_cuandoElResumenEsVacio() {
        // Arrange
        var solicitud = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(solicitud, UUID.randomUUID(), TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR),
                ResumenSolicitud.VACIO, false))
                .isInstanceOf(SolicitudNoEncontradaException.class);
    }

    @Test
    void debeLanzarSolicitudNoPropia_cuandoElRemitenteNoEsElSolicitante() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var tipo = TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR;

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(solicitud, UUID.randomUUID(), tipo), resumen(solicitud, UUID.randomUUID(), tipo), false))
                .isInstanceOf(SolicitudNoPropiaException.class);
    }

    @Test
    void debeLanzarElErrorDelCoordinador_cuandoSeEsperabaCoordinadorYLaSolicitudEsDeOtroTipo() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var solicitante = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(solicitud, solicitante, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR),
                resumen(solicitud, solicitante, TipoSolicitud.CAMBIO_DE_ASESOR), false))
                .isInstanceOf(SolicitudTipoNoCoincideException.class);
    }

    @Test
    void debeLanzarElErrorDelAsesor_cuandoSeEsperabaAsesorYLaSolicitudEsDeOtroTipo() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var solicitante = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(solicitud, solicitante, TipoSolicitud.NOVEDAD_PARA_EL_ASESOR),
                resumen(solicitud, solicitante, TipoSolicitud.CAMBIO_DE_ASESOR), false))
                .isInstanceOf(SolicitudTipoNoCoincideAsesorException.class);
    }

    @Test
    void debeLanzarSolicitudConRespuestas_cuandoLaSolicitudTieneRespuestas() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var solicitante = UUID.randomUUID();
        var tipo = TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR;

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(solicitud, solicitante, tipo), resumen(solicitud, solicitante, tipo), true))
                .isInstanceOf(SolicitudConRespuestasException.class);
    }

    @Test
    void debePasar_cuandoLasCuatroReglasSeCumplenParaCambioAsesor() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var solicitante = UUID.randomUUID();
        var tipo = TipoSolicitud.CAMBIO_DE_ASESOR;

        // Act & Assert
        assertThatCode(() -> validator.validar(
                entrada(solicitud, solicitante, tipo), resumen(solicitud, solicitante, tipo), false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarElErrorDeCambioAsesor_cuandoSeEsperabaCambioAsesorYLaSolicitudEsDeOtroTipo() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var solicitante = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(solicitud, solicitante, TipoSolicitud.CAMBIO_DE_ASESOR),
                resumen(solicitud, solicitante, TipoSolicitud.NOVEDAD_PARA_EL_ASESOR), false))
                .isInstanceOf(SolicitudTipoNoCoincideCambioAsesorException.class);
    }

    @Test
    void debePasar_cuandoLasCuatroReglasSeCumplenParaAmpliacionPlazo() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();
        var tipo = TipoSolicitud.AMPLIACION_DE_PLAZO;

        // Act & Assert
        assertThatCode(() -> validator.validar(
                entrada(solicitud, solicitante, tipo), resumen(solicitud, solicitante, tipo), false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarElErrorDeAmpliacionPlazo_cuandoSeEsperabaAmpliacionPlazoYLaSolicitudEsDeOtroTipo() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(solicitud, solicitante, TipoSolicitud.AMPLIACION_DE_PLAZO),
                resumen(solicitud, solicitante, TipoSolicitud.NOVEDAD_PARA_EL_ASESOR), false))
                .isInstanceOf(SolicitudTipoNoCoincideAmpliacionPlazoException.class);
    }

    @Test
    void debePasar_cuandoLasCuatroReglasSeCumplenParaRegistroModificacionUsuarios() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();
        var tipo = TipoSolicitud.REGISTRO_Y_MODIFICACION_DE_USUARIOS;

        // Act & Assert
        assertThatCode(() -> validator.validar(
                entrada(solicitud, solicitante, tipo), resumen(solicitud, solicitante, tipo), false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarElErrorDeRegistroModificacionUsuarios_cuandoSeEsperabaRegistroModificacionUsuariosYLaSolicitudEsDeOtroTipo() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(solicitud, solicitante, TipoSolicitud.REGISTRO_Y_MODIFICACION_DE_USUARIOS),
                resumen(solicitud, solicitante, TipoSolicitud.NOVEDAD_PARA_EL_ASESOR), false))
                .isInstanceOf(SolicitudTipoNoCoincideRegistroModificacionUsuariosException.class);
    }
}
