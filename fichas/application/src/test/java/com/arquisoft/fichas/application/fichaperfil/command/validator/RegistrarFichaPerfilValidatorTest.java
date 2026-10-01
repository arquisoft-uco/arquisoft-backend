package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.application.fichaperfil.command.validator.impl.RegistrarFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaTituloDuplicadoException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarFichaPerfilValidatorTest {

    private final RegistrarFichaPerfilValidatorImpl validator = new RegistrarFichaPerfilValidatorImpl();

    @Test
    void debePasar_cuandoElAsesorExisteYElTituloEstaLibre() {
        // Arrange
        var ficha = FichaPerfilDomain.crear("Titulo de prueba", UUID.randomUUID());

        // Act / Assert
        assertThatCode(() -> validator.validar(ficha, true, false)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarAsesorNoEncontrado_cuandoElAsesorNoExiste() {
        // Arrange
        UUID asesor = UUID.randomUUID();
        var ficha = FichaPerfilDomain.crear("Titulo de prueba", asesor);

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(ficha, false, false))
                .isInstanceOf(AsesorFichaNoEncontradoException.class)
                .hasMessageContaining(asesor.toString());
    }

    @Test
    void debeLanzarTituloDuplicado_cuandoElTituloYaEstaTomado() {
        // Arrange
        var ficha = FichaPerfilDomain.crear("Titulo tomado", UUID.randomUUID());

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(ficha, true, true))
                .isInstanceOf(FichaTituloDuplicadoException.class)
                .hasMessageContaining("Titulo tomado");
    }

    @Test
    void debeReportarPrimeroLaAusenciaDelAsesor_cuandoAmbasReglasFallan() {
        // Arrange — el orden es parte del contrato: primero existencia, despues unicidad
        var ficha = FichaPerfilDomain.crear("Titulo tomado", UUID.randomUUID());

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(ficha, false, true))
                .isInstanceOf(AsesorFichaNoEncontradoException.class);
    }
}
