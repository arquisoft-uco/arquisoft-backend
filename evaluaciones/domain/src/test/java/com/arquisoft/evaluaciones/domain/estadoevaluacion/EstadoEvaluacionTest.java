package com.arquisoft.evaluaciones.domain.estadoevaluacion;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.exception.EstadoEvaluacionNoEncontradoException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoEvaluacionTest {

    @Test
    void debeResolverLasTresConstantesDelCatalogo_cuandoElIdEsValido() {
        // Act & Assert
        assertThat(EstadoEvaluacion.desde("PENDIENTE")).isEqualTo(EstadoEvaluacion.PENDIENTE);
        assertThat(EstadoEvaluacion.desde("EN_PROGRESO")).isEqualTo(EstadoEvaluacion.EN_PROGRESO);
        assertThat(EstadoEvaluacion.desde("FINALIZADA")).isEqualTo(EstadoEvaluacion.FINALIZADA);
        assertThat(EstadoEvaluacion.PENDIENTE.getNombre()).isEqualTo("Pendiente");
        assertThat(EstadoEvaluacion.EN_PROGRESO.getNombre()).isEqualTo("En progreso");
        assertThat(EstadoEvaluacion.FINALIZADA.getNombre()).isEqualTo("Finalizada");
    }

    @Test
    void debeSerValido_cuandoElIdPerteneceAlCatalogo() {
        // Act & Assert
        assertThat(EstadoEvaluacion.esValido("PENDIENTE")).isTrue();
        assertThat(EstadoEvaluacion.esValido("INEXISTENTE")).isFalse();
        assertThat(EstadoEvaluacion.esValido("VACIO")).isFalse();
    }

    @Test
    void debeLanzarExcepcion_cuandoElIdNoPerteneceAlCatalogo() {
        // Act & Assert
        assertThatThrownBy(() -> EstadoEvaluacion.desde("INEXISTENTE"))
                .isInstanceOfSatisfying(EstadoEvaluacionNoEncontradoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.Evaluacion.ESTADO_NO_ENCONTRADO));
    }

    @Test
    void debeIdentificarseComoVacio_cuandoEsLaConstanteVacio() {
        // Act & Assert
        assertThat(EstadoEvaluacion.VACIO.esVacio()).isTrue();
        assertThat(EstadoEvaluacion.PENDIENTE.esVacio()).isFalse();
    }
}
