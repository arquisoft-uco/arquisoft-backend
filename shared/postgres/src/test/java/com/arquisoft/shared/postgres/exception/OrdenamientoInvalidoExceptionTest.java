package com.arquisoft.shared.postgres.exception;

import com.arquisoft.shared.exception.ApplicationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrdenamientoInvalidoExceptionTest {

    @Test
    void debeCrearExcepcion_cuandoSoloSeProveeNombreCampo() {
        // Arrange
        String propertyName = "campoInvalido";

        // Act
        OrdenamientoInvalidoException excepcion = new OrdenamientoInvalidoException(propertyName);

        // Assert
        assertThat(excepcion).isInstanceOf(ApplicationException.class);
        assertThat(excepcion.getMessage()).isEqualTo("El campo de ordenamiento 'campoInvalido' no es válido");
        assertThat(excepcion.getCodigoError()).isEqualTo("ORDENAMIENTO_INVALIDO");
    }

    @Test
    void debeCrearExcepcion_cuandoSeProveeCausa() {
        // Arrange
        String propertyName = "campoInvalido";
        Throwable causa = new IllegalArgumentException("Propiedad no mapeada");

        // Act
        OrdenamientoInvalidoException excepcion = new OrdenamientoInvalidoException(propertyName, causa);

        // Assert
        assertThat(excepcion).isInstanceOf(ApplicationException.class);
        assertThat(excepcion.getMessage()).contains("El campo de ordenamiento 'campoInvalido' no es válido");
        assertThat(excepcion.getCodigoError()).isEqualTo("ORDENAMIENTO_INVALIDO");
        assertThat(excepcion.getCause()).isEqualTo(causa);
    }
}
