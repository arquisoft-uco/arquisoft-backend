package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.application.fichaperfil.command.validator.impl.ModificarFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPropietarioException;
import com.arquisoft.fichas.domain.fichaperfil.ModificacionFichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaTituloDuplicadoException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificarFichaPerfilValidatorTest {

    private final ModificarFichaPerfilValidatorImpl validator = new ModificarFichaPerfilValidatorImpl();

    private ModificacionFichaPerfilDomain modificacion(UUID ficha, UUID estudiante, String titulo) {
        return ModificacionFichaPerfilDomain.crear(ficha, titulo, estudiante);
    }

    @Test
    void debePasar_cuandoEsPropietarioYElTituloEstaLibre() {
        // Arrange
        var modificacion = modificacion(UUID.randomUUID(), UUID.randomUUID(), "Titulo nuevo");

        // Act / Assert
        assertThatCode(() -> validator.validar(modificacion, true, false)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoPropietario_cuandoElEstudianteNoEsDuenoDeLaFicha() {
        // Arrange
        UUID ficha = UUID.randomUUID();
        var modificacion = modificacion(ficha, UUID.randomUUID(), "Titulo nuevo");

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(modificacion, false, false))
                .isInstanceOf(FichaNoPropietarioException.class)
                .hasMessageContaining(ficha.toString());
    }

    @Test
    void debeLanzarTituloDuplicado_cuandoElTituloYaEstaTomado() {
        // Arrange
        var modificacion = modificacion(UUID.randomUUID(), UUID.randomUUID(), "Titulo tomado");

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(modificacion, true, true))
                .isInstanceOf(FichaTituloDuplicadoException.class)
                .hasMessageContaining("Titulo tomado");
    }

    @Test
    void debeReportarPrimeroLaPropiedad_cuandoAmbasReglasFallan() {
        // Arrange — el orden es parte del contrato: primero propiedad, despues unicidad
        var modificacion = modificacion(UUID.randomUUID(), UUID.randomUUID(), "Titulo tomado");

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(modificacion, false, true))
                .isInstanceOf(FichaNoPropietarioException.class);
    }
}
