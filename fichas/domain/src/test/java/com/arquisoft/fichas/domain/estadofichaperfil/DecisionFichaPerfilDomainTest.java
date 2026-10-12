package com.arquisoft.fichas.domain.estadofichaperfil;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DecisionFichaPerfilDomainTest {

    @Test
    void debeCrearLaDecision_cuandoDatosValidos() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var coordinador = UUID.randomUUID();

        // Act
        var decision = DecisionFichaPerfilDomain.crear(fichaPerfil, true, coordinador);

        // Assert
        assertThat(decision.getFichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(decision.isAcepta()).isTrue();
        assertThat(decision.getCoordinador()).isEqualTo(coordinador);
    }

    @Test
    void debeAcumularAmbosErrores_cuandoFichaPerfilYCoordinadorSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> DecisionFichaPerfilDomain.crear(null, false, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(2);
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(FichasFields.EstadoFichaPerfil.FICHA_PERFIL);
                        assertThat(error.codigoError())
                                .isEqualTo(FichasCodes.EstadoFichaPerfil.FICHA_PERFIL_ID_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(FichasFields.EstadoFichaPerfil.COORDINADOR);
                        assertThat(error.codigoError())
                                .isEqualTo(FichasCodes.EstadoFichaPerfil.COORDINADOR_ID_REQUERIDO);
                    });
                });
    }
}
