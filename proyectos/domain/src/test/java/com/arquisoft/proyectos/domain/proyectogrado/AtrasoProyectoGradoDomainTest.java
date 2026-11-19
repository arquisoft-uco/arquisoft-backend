package com.arquisoft.proyectos.domain.proyectogrado;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AtrasoProyectoGradoDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();

        // Act
        var atraso = AtrasoProyectoGradoDomain.crear(proyectoGrado);

        // Assert
        assertThat(atraso.getProyectoGrado()).isEqualTo(proyectoGrado);
    }

    @Test
    void debeAcumularFieldErrors_cuandoFaltanDatos() {
        // Act & Assert
        assertThatThrownBy(() -> AtrasoProyectoGradoDomain.crear(null))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactly(ProyectosCodes.ProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO));
    }
}
