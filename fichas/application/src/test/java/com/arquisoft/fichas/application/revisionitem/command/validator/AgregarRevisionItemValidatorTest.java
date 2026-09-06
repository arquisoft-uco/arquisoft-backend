package com.arquisoft.fichas.application.revisionitem.command.validator;

import com.arquisoft.fichas.application.revisionitem.command.validator.impl.AgregarRevisionItemValidatorImpl;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPerteneceAsesorException;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.revisionitem.AgregacionRevisionItemDomain;
import com.arquisoft.fichas.domain.revisionitem.RevisionItemDomain;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemYaExisteException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarRevisionItemValidatorTest {

    private final AgregarRevisionItemValidatorImpl validator = new AgregarRevisionItemValidatorImpl();

    private final UUID fichaPerfil = UUID.randomUUID();

    @Test
    void debePasar_cuandoElItemExisteEsPropietarioYNoHayDuplicado() {
        // Arrange
        var entrada = agregacionValida();

        // Act & Assert
        assertThatCode(() -> validator.validar(entrada, true, fichaPerfil, entrada.getAsesorFicha(), 0L))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarItemNoEncontrado_cuandoElItemNoExiste() {
        // Arrange
        var entrada = agregacionValida();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, false, fichaPerfil, entrada.getAsesorFicha(), 0L))
                .isInstanceOf(ItemFichaPerfilNoEncontradoException.class);
    }

    @Test
    void debeLanzarFichaNoPerteneceAsesor_cuandoElAsesorNoEsPropietario() {
        // Arrange
        var entrada = agregacionValida();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, true, fichaPerfil, UUID.randomUUID(), 0L))
                .isInstanceOf(FichaNoPerteneceAsesorException.class);
    }

    @Test
    void debeLanzarRevisionYaExiste_cuandoLaRevisionYaExiste() {
        // Arrange
        var entrada = agregacionValida();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, true, fichaPerfil, entrada.getAsesorFicha(), 1L))
                .isInstanceOf(RevisionItemYaExisteException.class);
    }

    @Test
    void debeReportarPrimeroLaExistenciaDelItem_cuandoExistenciaYPropiedadFallanALaVez() {
        // Arrange — el orden es parte del contrato: primero existencia, después propiedad
        var entrada = agregacionValida();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, false, fichaPerfil, UUID.randomUUID(), 0L))
                .isInstanceOf(ItemFichaPerfilNoEncontradoException.class);
    }

    @Test
    void debeReportarPrimeroLaPropiedad_cuandoPropiedadYDuplicidadFallanALaVez() {
        // Arrange — el orden es parte del contrato: propiedad antes que unicidad
        var entrada = agregacionValida();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, true, fichaPerfil, UUID.randomUUID(), 1L))
                .isInstanceOf(FichaNoPerteneceAsesorException.class);
    }

    private static AgregacionRevisionItemDomain agregacionValida() {
        var revisionItem = RevisionItemDomain.crear(UUID.randomUUID());
        return AgregacionRevisionItemDomain.crear(revisionItem, UUID.randomUUID());
    }
}
