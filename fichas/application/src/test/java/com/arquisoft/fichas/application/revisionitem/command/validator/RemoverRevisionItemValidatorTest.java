package com.arquisoft.fichas.application.revisionitem.command.validator;

import com.arquisoft.fichas.application.revisionitem.command.validator.impl.RemoverRevisionItemValidatorImpl;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPerteneceAsesorException;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemCerradaException;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverRevisionItemValidatorTest {

    private final RemoverRevisionItemValidatorImpl validator = new RemoverRevisionItemValidatorImpl();

    private final UUID revisionItem = UtilUUID.generarNuevoUUID();
    private final UUID asesorSolicitante = UtilUUID.generarNuevoUUID();
    private final UUID otroAsesor = UtilUUID.generarNuevoUUID();
    private final UUID fichaPerfil = UtilUUID.generarNuevoUUID();

    @ParameterizedTest
    @EnumSource(value = EstadoRevision.class, names = {"NUEVA", "VISUALIZADA", "EN_PROGRESO", "CORRECCION_DISPONIBLE"})
    void debePasar_cuandoLaRevisionExisteElAsesorEsElDeLaFichaYNoEstaCerrada(EstadoRevision estado) {
        // Act & Assert
        assertThatCode(() -> validator.validar(revisionItem, asesorSolicitante, fichaPerfil,
                asesorSolicitante, true, estado.getId()))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarRevisionNoEncontrada_cuandoLaRevisionNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(revisionItem, asesorSolicitante,
                UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(), false,
                EstadoRevision.VACIO.getId()))
                .isInstanceOf(RevisionItemNoEncontradoException.class);
    }

    @Test
    void debeLanzarFichaNoPerteneceAsesor_cuandoElSolicitanteNoEsElAsesorDeLaFicha() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(revisionItem, asesorSolicitante, fichaPerfil,
                otroAsesor, true, EstadoRevision.NUEVA.getId()))
                .isInstanceOf(FichaNoPerteneceAsesorException.class);
    }

    @Test
    void debeLanzarRevisionCerrada_cuandoLaRevisionEstaCerrada() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(revisionItem, asesorSolicitante, fichaPerfil,
                asesorSolicitante, true, EstadoRevision.CERRADA.getId()))
                .isInstanceOf(RevisionItemCerradaException.class);
    }

    @Test
    void debeGanarLaExistencia_cuandoLaRevisionNoExisteYElAsesorNoCoincide() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(revisionItem, asesorSolicitante,
                UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(), false,
                EstadoRevision.CERRADA.getId()))
                .isInstanceOf(RevisionItemNoEncontradoException.class);
    }

    @Test
    void debeGanarLaPropiedad_cuandoElAsesorNoCoincideYLaRevisionEstaCerrada() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(revisionItem, asesorSolicitante, fichaPerfil,
                otroAsesor, true, EstadoRevision.CERRADA.getId()))
                .isInstanceOf(FichaNoPerteneceAsesorException.class);
    }
}
