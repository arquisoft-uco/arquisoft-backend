package com.arquisoft.artefactos.application.versionartefacto.query.validator.impl;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoDetalleCriteria;
import com.arquisoft.artefactos.domain.versionartefacto.exception.ActorSinAccesoVersionArtefactoException;
import com.arquisoft.artefactos.domain.versionartefacto.exception.VersionArtefactoNoEncontradaException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarVersionArtefactoValidatorImplTest {

    private final ConsultarVersionArtefactoValidatorImpl validator = new ConsultarVersionArtefactoValidatorImpl();
    private final VersionArtefactoDetalleCriteria comoAsesor = new VersionArtefactoDetalleCriteria(
            UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), true, false);

    @Test
    void debeLanzarNoEncontrada_cuandoVersionNoExiste_conPrioridadSobreAcceso() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(comoAsesor, UtilUUID.obtenerUUIDPorDefecto(), false))
                .isInstanceOf(VersionArtefactoNoEncontradaException.class);
    }

    @Test
    void debeLanzarAccesoDenegado_cuandoNoPertenece() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(comoAsesor, UtilUUID.generarNuevoUUID(), true))
                .isInstanceOf(ActorSinAccesoVersionArtefactoException.class);
    }

    @Test
    void debePasar_cuandoPertenece() {
        // Act & Assert
        assertThatCode(() -> validator.validar(comoAsesor, comoAsesor.actor(), false))
                .doesNotThrowAnyException();
    }
}
