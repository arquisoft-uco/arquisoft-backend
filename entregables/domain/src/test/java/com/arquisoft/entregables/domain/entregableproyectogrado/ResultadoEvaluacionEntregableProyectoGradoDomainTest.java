package com.arquisoft.entregables.domain.entregableproyectogrado;

import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResultadoEvaluacionEntregableProyectoGradoDomainTest {

    @Test
    void debeCrearComoAprobado_cuandoEstadoEsAprobado() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();

        // Act
        var resultado = ResultadoEvaluacionEntregableProyectoGradoDomain.crear(entregable, "APROBADO");

        // Assert
        assertThat(resultado.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(resultado.getEstadoEntregable()).isEqualTo(EstadoEntregable.APROBADO);
        assertThat(resultado.esAprobado()).isTrue();
        assertThat(resultado.esRechazado()).isFalse();
    }

    @Test
    void debeCrearComoAprobado_cuandoEstadoEsAprobadoConObservaciones() {
        // Act
        var resultado = ResultadoEvaluacionEntregableProyectoGradoDomain.crear(
                UtilUUID.generarNuevoUUID(), " APROBADO_CON_OBSERVACIONES ");

        // Assert
        assertThat(resultado.getEstadoEntregable()).isEqualTo(EstadoEntregable.APROBADO_CON_OBSERVACIONES);
        assertThat(resultado.esAprobado()).isTrue();
        assertThat(resultado.esRechazado()).isFalse();
    }

    @Test
    void debeCrearComoRechazado_cuandoEstadoEsRechazado() {
        // Act
        var resultado = ResultadoEvaluacionEntregableProyectoGradoDomain.crear(
                UtilUUID.generarNuevoUUID(), "RECHAZADO");

        // Assert
        assertThat(resultado.getEstadoEntregable()).isEqualTo(EstadoEntregable.RECHAZADO);
        assertThat(resultado.esRechazado()).isTrue();
        assertThat(resultado.esAprobado()).isFalse();
    }

    @Test
    void debeLanzar_cuandoEstadoNoEsResultadoDeEvaluacionOEsDesconocido() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> ResultadoEvaluacionEntregableProyectoGradoDomain.crear(entregable, "CERRADO"))
                .isInstanceOfSatisfying(DomainValidationException.class, ex -> assertThat(
                        ex.getValidationResult().getErrores())
                        .extracting(e -> e.codigoError())
                        .containsExactly(EntregablesCodes.EntregableProyectoGrado.RESULTADO_EVALUACION_INVALIDO));
        assertThatThrownBy(() -> ResultadoEvaluacionEntregableProyectoGradoDomain.crear(entregable, "INEXISTENTE"))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void debeAcumularFieldErrors_cuandoFaltanEntregableYEstado() {
        // Act & Assert
        assertThatThrownBy(() -> ResultadoEvaluacionEntregableProyectoGradoDomain.crear(null, " "))
                .isInstanceOfSatisfying(DomainValidationException.class, ex -> assertThat(
                        ex.getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactlyInAnyOrder(
                                EntregablesFields.EntregableProyectoGrado.ID,
                                EntregablesFields.EntregableProyectoGrado.ESTADO_ENTREGABLE));
    }
}
