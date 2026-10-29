package com.arquisoft.entregables.domain.estadoentregable;

import com.arquisoft.entregables.domain.estadoentregable.exception.EstadoEntregableNoEncontradoException;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoEntregableTest {

    @Test
    void debeResolverCadaConstante_conSuIdYNombre() {
        // Act & Assert
        assertThat(EstadoEntregable.desde("DISPONIBLE_EVALUACION")).isEqualTo(EstadoEntregable.DISPONIBLE_EVALUACION);
        assertThat(EstadoEntregable.desde("APROBADO_CON_OBSERVACIONES").getNombre())
                .isEqualTo("Aprobado Con Observaciones");
        assertThat(EstadoEntregable.desde("CERRADO").getId()).isEqualTo("CERRADO");
        assertThat(EstadoEntregable.EN_EVALUACION.getNombre()).isEqualTo("En Evaluacion");
    }

    @Test
    void debeLanzar_cuandoIdNoExisteOEsElCentinela() {
        // Arrange
        var idInexistente = "INEXISTENTE_XYZ";

        // Act & Assert
        assertThatThrownBy(() -> EstadoEntregable.desde(idInexistente))
                .isInstanceOf(EstadoEntregableNoEncontradoException.class)
                .hasFieldOrPropertyWithValue("codigoError", EntregablesCodes.EstadoEntregable.NO_ENCONTRADO)
                .satisfies(excepcion -> assertThat(((EstadoEntregableNoEncontradoException) excepcion)
                        .getError().getMessage()).doesNotContain(idInexistente));
        assertThatThrownBy(() -> EstadoEntregable.desde("VACIO"))
                .isInstanceOf(EstadoEntregableNoEncontradoException.class);
    }

    @Test
    void debeValidarElId_cuandoSeConsultaEsValido() {
        // Act & Assert
        assertThat(EstadoEntregable.esValido("APROBADO")).isTrue();
        assertThat(EstadoEntregable.esValido("VACIO")).isFalse();
        assertThat(EstadoEntregable.esValido("INEXISTENTE")).isFalse();
    }

    @Test
    void debeCoincidirConElCatalogoDelMer_cuandoSeListanLasConstantes() {
        // Act
        var ids = Arrays.stream(EstadoEntregable.values()).map(EstadoEntregable::getId).toList();

        // Assert
        assertThat(ids).containsExactly("DISPONIBLE_EVALUACION", "EN_EVALUACION", "APROBADO_CON_OBSERVACIONES",
                "APROBADO", "RECHAZADO", "CANCELADO", "CERRADO", "VACIO");
    }
}
