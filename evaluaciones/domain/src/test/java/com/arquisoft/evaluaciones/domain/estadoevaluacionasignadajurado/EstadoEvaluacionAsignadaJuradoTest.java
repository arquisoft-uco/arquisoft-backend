package com.arquisoft.evaluaciones.domain.estadoevaluacionasignadajurado;

import com.arquisoft.evaluaciones.domain.estadoevaluacionasignadajurado.exception.EstadoEvaluacionAsignadaJuradoNoEncontradoException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoEvaluacionAsignadaJuradoTest {

    @Test
    void debeResolverLasDosConstantesDelCatalogo_cuandoElIdEsValido() {
        // Act & Assert
        assertThat(EstadoEvaluacionAsignadaJurado.desde("PENDIENTE_POR_EVALUAR"))
                .isEqualTo(EstadoEvaluacionAsignadaJurado.PENDIENTE_POR_EVALUAR);
        assertThat(EstadoEvaluacionAsignadaJurado.desde("EVALUADA")).isEqualTo(EstadoEvaluacionAsignadaJurado.EVALUADA);
        assertThat(EstadoEvaluacionAsignadaJurado.PENDIENTE_POR_EVALUAR.getId()).isEqualTo("PENDIENTE_POR_EVALUAR");
        assertThat(EstadoEvaluacionAsignadaJurado.PENDIENTE_POR_EVALUAR.getNombre()).isEqualTo("Pendiente por evaluar");
        assertThat(EstadoEvaluacionAsignadaJurado.EVALUADA.getNombre()).isEqualTo("Evaluada");
    }

    @Test
    void debeSerValido_cuandoElIdPerteneceAlCatalogo() {
        // Act & Assert
        assertThat(EstadoEvaluacionAsignadaJurado.esValido("EVALUADA")).isTrue();
        assertThat(EstadoEvaluacionAsignadaJurado.esValido("INEXISTENTE")).isFalse();
        assertThat(EstadoEvaluacionAsignadaJurado.esValido("VACIO")).isFalse();
    }

    @Test
    void debeLanzarExcepcion_cuandoElIdNoPerteneceAlCatalogo() {
        // Act & Assert
        assertThatThrownBy(() -> EstadoEvaluacionAsignadaJurado.desde("INEXISTENTE"))
                .isInstanceOfSatisfying(EstadoEvaluacionAsignadaJuradoNoEncontradoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.EvaluacionJurado.ESTADO_NO_ENCONTRADO));
    }

    @Test
    void debeIdentificarseComoVacio_cuandoEsLaConstanteVacio() {
        // Act & Assert
        assertThat(EstadoEvaluacionAsignadaJurado.VACIO.esVacio()).isTrue();
        assertThat(EstadoEvaluacionAsignadaJurado.EVALUADA.esVacio()).isFalse();
    }
}
