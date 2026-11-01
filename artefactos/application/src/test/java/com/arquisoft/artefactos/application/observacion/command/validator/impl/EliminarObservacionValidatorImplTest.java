package com.arquisoft.artefactos.application.observacion.command.validator.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoNoEncontradoException;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoOmitidoException;
import com.arquisoft.artefactos.domain.artefacto.exception.AsesorNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.observacion.EliminacionObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.ObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.exception.ObservacionNoEncontradaException;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoAdmiteEliminarObservacionesException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EliminarObservacionValidatorImplTest {

    private final EliminarObservacionValidatorImpl validator = new EliminarObservacionValidatorImpl();

    private final UUID asesorId = UtilUUID.generarNuevoUUID();
    private final EliminacionObservacionDomain eliminacion = EliminacionObservacionDomain.crear(
            UtilUUID.generarNuevoUUID(), asesorId);
    private final ArtefactoDomain artefacto = ArtefactoDomain.reconstruir(
            UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), asesorId, null);

    private final ObservacionDomain observacion = ObservacionDomain.reconstruir(
            eliminacion.getObservacion(), UtilUUID.generarNuevoUUID(), "Texto", EstadoObservacion.NUEVA,
            UtilUUID.obtenerUUIDPorDefecto());

    private RevisionAsesorDomain revision(EstadoRevisionAsesor estado) {
        return RevisionAsesorDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), estado,
                UtilFecha.generarInstanteActual());
    }

    @Test
    void noDebeLanzar_cuandoTodoEsValido() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatCode(() -> validator.validar(eliminacion, observacion, revision, artefacto)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarObservacionNoEncontrada_antesQueElResto_cuandoTodoFalta() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                eliminacion, ObservacionDomain.VACIO, RevisionAsesorDomain.VACIO, ArtefactoDomain.VACIO))
                .isInstanceOf(ObservacionNoEncontradaException.class);
    }

    @Test
    void debeLanzar_cuandoElArtefactoNoExiste() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(eliminacion, observacion, revision, ArtefactoDomain.VACIO))
                .isInstanceOf(ArtefactoNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoPropietario_antesQueRevisionNoPendiente_cuandoAmbosAplican() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.CERRADA);
        var ajeno = ArtefactoDomain.reconstruir(artefacto.getId(), artefacto.getProyectoGrado(),
                artefacto.getVersionRepositorioArtefacto(), UtilUUID.generarNuevoUUID(), null);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(eliminacion, observacion, revision, ajeno))
                .isInstanceOf(AsesorNoPropietarioArtefactoException.class);
    }

    @Test
    void debeLanzarArtefactoOmitido_antesQueRevisionNoPendiente_cuandoAmbosAplican() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.EN_PROGRESO);
        var omitido = ArtefactoDomain.reconstruir(artefacto.getId(), artefacto.getProyectoGrado(),
                artefacto.getVersionRepositorioArtefacto(), asesorId, UtilFecha.generarInstanteActual());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(eliminacion, observacion, revision, omitido))
                .isInstanceOf(ArtefactoOmitidoException.class);
    }

    @Test
    void debeLanzar_cuandoLaRevisionNoEstaPendiente() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.EN_PROGRESO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(eliminacion, observacion, revision, artefacto))
                .isInstanceOf(RevisionAsesorNoAdmiteEliminarObservacionesException.class);
    }
}
