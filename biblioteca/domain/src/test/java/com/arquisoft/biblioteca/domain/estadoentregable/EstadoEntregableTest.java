package com.arquisoft.biblioteca.domain.estadoentregable;

import com.arquisoft.biblioteca.domain.estadoentregable.exception.EstadoEntregableNoEncontradoException;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoEntregableTest {

    @ParameterizedTest
    @CsvSource({
            "NUEVO, Nuevo",
            "DISPONIBLE_EVALUACION, Disponible Evaluacion",
            "EN_EVALUACION, En Evaluacion",
            "APROBADO_CON_OBSERVACIONES, Aprobado Con Observaciones",
            "APROBADO, Aprobado",
            "RECHAZADO, Rechazado",
            "CANCELADO, Cancelado",
            "CERRADO, Cerrado"
    })
    void debeRetornarLaConstanteConIdYNombre_cuandoElIdEstaEnElCatalogo(String id, String nombre) {
        // Act
        var estado = EstadoEntregable.desde(id);

        // Assert
        assertThat(estado.name()).isEqualTo(id);
        assertThat(estado.getId()).isEqualTo(id);
        assertThat(estado.getNombre()).isEqualTo(nombre);
        assertThat(EstadoEntregable.esValido(id)).isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"ESTADO_INEXISTENTE", "VACIO", "Aprobado"})
    void debeLanzarNoEncontrado_cuandoElIdNoEstaEnElCatalogo(String id) {
        // Arrange
        ThrowingCallable accion = () -> EstadoEntregable.desde(id);

        // Act & Assert
        assertThatThrownBy(accion)
                .isInstanceOfSatisfying(EstadoEntregableNoEncontradoException.class,
                        ex -> assertThat(ex.getCodigoError())
                                .isEqualTo(BibliotecaCodes.EstadoEntregable.NO_ENCONTRADO));
        assertThat(EstadoEntregable.esValido(id)).isFalse();
    }

    @Test
    void debeLlevarElIdRecibidoSoloAlDetalle_cuandoSeLanzaLaExcepcion() {
        // Act
        var ex = new EstadoEntregableNoEncontradoException("ESTADO_INEXISTENTE");

        // Assert
        assertThat(ex.getMessage()).contains("ESTADO_INEXISTENTE");
        assertThat(ex.getError().getMessage()).doesNotContain("ESTADO_INEXISTENTE");
    }
}
