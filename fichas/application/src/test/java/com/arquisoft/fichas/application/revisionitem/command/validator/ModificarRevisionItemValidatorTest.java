package com.arquisoft.fichas.application.revisionitem.command.validator;

import com.arquisoft.fichas.application.revisionitem.command.validator.impl.ModificarRevisionItemValidatorImpl;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPerteneceAsesorException;
import com.arquisoft.fichas.domain.revisionitem.ModificacionRevisionItemDomain;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemNoEncontradaException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificarRevisionItemValidatorTest {

    private final ModificarRevisionItemValidatorImpl validator = new ModificarRevisionItemValidatorImpl();

    private final UUID fichaPerfil = UUID.randomUUID();

    @Test
    void debePasar_cuandoLaRevisionExisteYElAsesorEsPropietario() {
        // Arrange
        var entrada = modificacionValida();

        // Act & Assert
        assertThatCode(() -> validator.validar(entrada, 1L, fichaPerfil, true))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarRevisionNoEncontrada_cuandoLaRevisionNoExiste() {
        // Arrange
        var entrada = modificacionValida();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, 0L, fichaPerfil, true))
                .isInstanceOf(RevisionItemNoEncontradaException.class);
    }

    @Test
    void debeLanzarFichaNoPerteneceAsesor_cuandoElAsesorNoEsPropietario() {
        // Arrange
        var entrada = modificacionValida();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, 1L, fichaPerfil, false))
                .isInstanceOf(FichaNoPerteneceAsesorException.class);
    }

    @Test
    void debeReportarPrimeroLaExistenciaDeLaRevision_cuandoExistenciaYPropiedadFallanALaVez() {
        // Arrange — el orden es parte del contrato: primero existencia, después propiedad
        var entrada = modificacionValida();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, 0L, fichaPerfil, false))
                .isInstanceOf(RevisionItemNoEncontradaException.class);
    }

    private static ModificacionRevisionItemDomain modificacionValida() {
        return ModificacionRevisionItemDomain.crear(UUID.randomUUID(), "VISUALIZADA", UUID.randomUUID());
    }
}
