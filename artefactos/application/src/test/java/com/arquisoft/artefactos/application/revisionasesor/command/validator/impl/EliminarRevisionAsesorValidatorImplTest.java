package com.arquisoft.artefactos.application.revisionasesor.command.validator.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoNoEncontradoException;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoOmitidoException;
import com.arquisoft.artefactos.domain.artefacto.exception.AsesorNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.revisionasesor.EliminacionRevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoEliminableException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoEncontradaException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EliminarRevisionAsesorValidatorImplTest {

    private final EliminarRevisionAsesorValidatorImpl validator = new EliminarRevisionAsesorValidatorImpl();

    private final UUID asesorId = UtilUUID.generarNuevoUUID();
    private final EliminacionRevisionAsesorDomain eliminacion = EliminacionRevisionAsesorDomain.crear(
            UtilUUID.generarNuevoUUID(), asesorId);
    private final ArtefactoDomain artefacto = ArtefactoDomain.reconstruir(
            UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), asesorId, null);

    private RevisionAsesorDomain revision(EstadoRevisionAsesor estado) {
        return RevisionAsesorDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), estado,
                UtilFecha.generarInstanteActual());
    }

    @Test
    void debePasar_cuandoTodoEsValido() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatCode(() -> validator.validar(eliminacion, revision, artefacto)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoLaRevisionNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(eliminacion, RevisionAsesorDomain.VACIO, artefacto))
                .isInstanceOf(RevisionAsesorNoEncontradaException.class);
    }

    @Test
    void debeLanzar_cuandoElArtefactoNoExiste() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(eliminacion, revision, ArtefactoDomain.VACIO))
                .isInstanceOf(ArtefactoNoEncontradoException.class);
    }

    @Test
    void debeLanzar_cuandoElAsesorNoEsResponsable() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);
        var ajeno = ArtefactoDomain.reconstruir(artefacto.getId(), artefacto.getProyectoGrado(),
                artefacto.getVersionRepositorioArtefacto(), UtilUUID.generarNuevoUUID(), null);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(eliminacion, revision, ajeno))
                .isInstanceOf(AsesorNoPropietarioArtefactoException.class);
    }

    @Test
    void debeLanzar_cuandoElArtefactoEstaOmitido() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);
        var omitido = ArtefactoDomain.reconstruir(artefacto.getId(), artefacto.getProyectoGrado(),
                artefacto.getVersionRepositorioArtefacto(), asesorId, UtilFecha.generarInstanteActual());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(eliminacion, revision, omitido))
                .isInstanceOf(ArtefactoOmitidoException.class);
    }

    @Test
    void debeLanzar_cuandoLaRevisionNoEstaPendiente() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.EN_PROGRESO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(eliminacion, revision, artefacto))
                .isInstanceOf(RevisionAsesorNoEliminableException.class);
    }

    @Test
    void debeReportarLaRevisionInexistente_antesQueElArtefacto() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(eliminacion, RevisionAsesorDomain.VACIO, ArtefactoDomain.VACIO))
                .isInstanceOf(RevisionAsesorNoEncontradaException.class);
    }
}
