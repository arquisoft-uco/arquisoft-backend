package com.arquisoft.artefactos.application.observacion.command.validator.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoNoEncontradoException;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoOmitidoException;
import com.arquisoft.artefactos.domain.artefacto.exception.AsesorNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.observacion.ObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.RegistroObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.exception.ObservacionDuplicadaException;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoAdmiteObservacionesException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoEncontradaException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarObservacionValidatorImplTest {

    private final RegistrarObservacionValidatorImpl validator = new RegistrarObservacionValidatorImpl();

    private final UUID revisionId = UtilUUID.generarNuevoUUID();
    private final UUID asesorId = UtilUUID.generarNuevoUUID();
    private final RegistroObservacionDomain registro = RegistroObservacionDomain.crear(
            ObservacionDomain.crear(revisionId, "Detalle"), asesorId);
    private final ArtefactoDomain artefacto = ArtefactoDomain.reconstruir(
            UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), asesorId, null);

    private RevisionAsesorDomain revision(EstadoRevisionAsesor estado) {
        return RevisionAsesorDomain.reconstruir(revisionId, UtilUUID.generarNuevoUUID(), estado, UtilFecha.generarInstanteActual());
    }

    @Test
    void noDebeLanzar_cuandoTodoEsValido() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatCode(() -> validator.validar(registro, revision, artefacto, false)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarRevisionNoEncontrada_antesQueArtefactoNoEncontrado_cuandoAmbosFaltan() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(registro, RevisionAsesorDomain.VACIO, ArtefactoDomain.VACIO, true))
                .isInstanceOf(RevisionAsesorNoEncontradaException.class);
    }

    @Test
    void debeLanzar_cuandoElArtefactoNoExiste() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(registro, revision, ArtefactoDomain.VACIO, false))
                .isInstanceOf(ArtefactoNoEncontradoException.class);
    }

    @Test
    void debeLanzar_cuandoElLlamanteNoEsElAsesorDelArtefacto() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);
        var ajeno = ArtefactoDomain.reconstruir(artefacto.getId(), artefacto.getProyectoGrado(),
                artefacto.getVersionRepositorioArtefacto(), UtilUUID.generarNuevoUUID(), null);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(registro, revision, ajeno, false))
                .isInstanceOf(AsesorNoPropietarioArtefactoException.class);
    }

    @Test
    void debeLanzarArtefactoOmitido_antesQueRevisionNoPendiente_cuandoAmbosAplican() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.CERRADA);
        var omitido = ArtefactoDomain.reconstruir(artefacto.getId(), artefacto.getProyectoGrado(),
                artefacto.getVersionRepositorioArtefacto(), asesorId, UtilFecha.generarInstanteActual());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(registro, revision, omitido, true))
                .isInstanceOf(ArtefactoOmitidoException.class);
    }

    @Test
    void debeLanzarNoAdmite_antesQueDuplicada_cuandoLaRevisionNoEstaPendiente() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.EN_PROGRESO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(registro, revision, artefacto, true))
                .isInstanceOf(RevisionAsesorNoAdmiteObservacionesException.class);
    }

    @Test
    void debeLanzar_cuandoLaDescripcionYaExiste() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(registro, revision, artefacto, true))
                .isInstanceOf(ObservacionDuplicadaException.class);
    }
}
