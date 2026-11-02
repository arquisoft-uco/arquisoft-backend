package com.arquisoft.artefactos.application.observacion.command.validator.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoNoEncontradoException;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoOmitidoException;
import com.arquisoft.artefactos.domain.artefacto.exception.EstudianteNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.observacion.CambioEstadoEstudianteObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.ObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.exception.EstadoDestinoObservacionEstudianteNoPermitidoException;
import com.arquisoft.artefactos.domain.observacion.exception.ObservacionNoEditablePorEstudianteException;
import com.arquisoft.artefactos.domain.observacion.exception.ObservacionNoEncontradaException;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoAdmiteAvanceObservacionException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoEncontradaException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarEstadoEstudianteObservacionValidatorImplTest {

    private final CambiarEstadoEstudianteObservacionValidatorImpl validator =
            new CambiarEstadoEstudianteObservacionValidatorImpl();

    private final UUID observacionId = UtilUUID.generarNuevoUUID();
    private final UUID revisionId = UtilUUID.generarNuevoUUID();
    private final UUID estudianteId = UtilUUID.generarNuevoUUID();
    private final RevisionAsesorDomain revision = revision(EstadoRevisionAsesor.EN_PROGRESO);
    private final ArtefactoDomain artefacto = ArtefactoDomain.reconstruir(
            UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
            UtilUUID.generarNuevoUUID(), null);
    private final ObservacionDomain observacion = observacion(EstadoObservacion.EN_PROGRESO);
    private final CambioEstadoEstudianteObservacionDomain cambio = cambio("SOLUCIONADA");

    private ObservacionDomain observacion(EstadoObservacion estado) {
        return ObservacionDomain.reconstruir(observacionId, revisionId, "Texto", estado,
                UtilUUID.obtenerUUIDPorDefecto());
    }

    private RevisionAsesorDomain revision(EstadoRevisionAsesor estado) {
        return RevisionAsesorDomain.reconstruir(
                revisionId, UtilUUID.generarNuevoUUID(), estado, UtilFecha.generarInstanteActual());
    }

    private CambioEstadoEstudianteObservacionDomain cambio(String estado) {
        return CambioEstadoEstudianteObservacionDomain.crear(observacionId, estado, estudianteId);
    }

    @Test
    void debePasar_cuandoTodoEsValido() {
        // Act & Assert
        assertThatCode(() -> validator.validar(cambio, observacion, revision, artefacto, true))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoLaObservacionNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, ObservacionDomain.VACIO, RevisionAsesorDomain.VACIO, ArtefactoDomain.VACIO, false))
                .isInstanceOf(ObservacionNoEncontradaException.class);
    }

    @Test
    void debeLanzar_cuandoLaRevisionNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, observacion, RevisionAsesorDomain.VACIO, ArtefactoDomain.VACIO, false))
                .isInstanceOf(RevisionAsesorNoEncontradaException.class);
    }

    @Test
    void debeLanzar_cuandoElArtefactoNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, observacion, revision, ArtefactoDomain.VACIO, false))
                .isInstanceOf(ArtefactoNoEncontradoException.class);
    }

    @Test
    void debeLanzar_cuandoElEstudianteNoEstaVinculado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, observacion, revision, artefacto, false))
                .isInstanceOf(EstudianteNoPropietarioArtefactoException.class);
    }

    @Test
    void debeLanzar_cuandoElArtefactoEstaOmitido() {
        // Arrange
        var omitido = ArtefactoDomain.reconstruir(artefacto.getId(), artefacto.getProyectoGrado(),
                artefacto.getVersionRepositorioArtefacto(), artefacto.getAsesor(),
                UtilFecha.generarInstanteActual());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, observacion, revision, omitido, true))
                .isInstanceOf(ArtefactoOmitidoException.class);
    }

    @Test
    void debeLanzar_cuandoLaRevisionNoEstaEnProgresoAunqueLaObservacionNoSeaEditable() {
        // Arrange
        var resuelta = revision(EstadoRevisionAsesor.RESUELTA);
        var cerrada = observacion(EstadoObservacion.CERRADA);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, cerrada, resuelta, artefacto, true))
                .isInstanceOf(RevisionAsesorNoAdmiteAvanceObservacionException.class);
    }

    @Test
    void debeLanzar_cuandoLaObservacionNoEsEditableAunqueElDestinoNoEstePermitido() {
        // Arrange
        var reabierta = observacion(EstadoObservacion.REABIERTA);
        var haciaCerrada = cambio("CERRADA");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(haciaCerrada, reabierta, revision, artefacto, true))
                .isInstanceOf(ObservacionNoEditablePorEstudianteException.class);
    }

    @Test
    void debeLanzar_cuandoElDestinoNoEstaPermitido() {
        // Arrange
        var haciaCerrada = cambio("CERRADA");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(haciaCerrada, observacion, revision, artefacto, true))
                .isInstanceOf(EstadoDestinoObservacionEstudianteNoPermitidoException.class);
    }
}
