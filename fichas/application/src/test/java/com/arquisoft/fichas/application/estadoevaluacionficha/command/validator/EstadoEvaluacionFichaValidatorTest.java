package com.arquisoft.fichas.application.estadoevaluacionficha.command.validator;

import com.arquisoft.fichas.application.estadoevaluacionficha.exception.EstadoEvaluacionDuplicadoException;
import com.arquisoft.fichas.application.estadoevaluacionficha.exception.EstadoEvaluacionNoEncontradoException;
import com.arquisoft.fichas.application.estadoevaluacionficha.exception.EvaluacionFichaNoPropiaException;
import com.arquisoft.fichas.application.estadoevaluacionficha.exception.EvaluacionFichaPerfilNoEncontradaException;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.PropietarioEvaluacionCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.port.out.EvaluacionFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.port.out.EstadoEvaluacionFichaOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstadoEvaluacionFichaValidatorTest {

    @Mock
    private EvaluacionFichaPerfilQueryOutputPort evaluacionFichaPerfilQueryOutputPort;

    @Mock
    private EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;

    @InjectMocks
    private EstadoEvaluacionFichaValidator validator;

    @Test
    void debeLanzarExcepcion_cuandoEvaluacionNoExiste() {
        // Arrange
        UUID evaluacion = UUID.randomUUID();
        when(evaluacionFichaPerfilQueryOutputPort.existePorId(evaluacion)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validarEvaluacionExiste(evaluacion))
                .isInstanceOf(EvaluacionFichaPerfilNoEncontradaException.class);
    }

    @Test
    void debePasar_cuandoEvaluacionExiste() {
        // Arrange
        UUID evaluacion = UUID.randomUUID();
        when(evaluacionFichaPerfilQueryOutputPort.existePorId(evaluacion)).thenReturn(true);

        // Act & Assert
        assertThatCode(() -> validator.validarEvaluacionExiste(evaluacion)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoRepresentanteNoEsPropietario() {
        // Arrange
        var criteria = new PropietarioEvaluacionCriteria(UUID.randomUUID(), UUID.randomUUID());
        when(evaluacionFichaPerfilQueryOutputPort.esRepresentantePropietario(criteria)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validarRepresentantePropietario(criteria))
                .isInstanceOf(EvaluacionFichaNoPropiaException.class);
    }

    @Test
    void debePasar_cuandoRepresentanteEsPropietario() {
        // Arrange
        var criteria = new PropietarioEvaluacionCriteria(UUID.randomUUID(), UUID.randomUUID());
        when(evaluacionFichaPerfilQueryOutputPort.esRepresentantePropietario(criteria)).thenReturn(true);

        // Act & Assert
        assertThatCode(() -> validator.validarRepresentantePropietario(criteria)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoEstadoYaFueRegistrado() {
        // Arrange
        UUID evaluacion = UUID.randomUUID();
        when(estadoEvaluacionFichaOutputPort.existePorEvaluacionYEstado(evaluacion, "APROBADA"))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> validator.validarEstadoNoDuplicado(evaluacion, "APROBADA"))
                .isInstanceOf(EstadoEvaluacionDuplicadoException.class);
    }

    @Test
    void debePasar_cuandoEstadoNoFueRegistrado() {
        // Arrange
        UUID evaluacion = UUID.randomUUID();
        when(estadoEvaluacionFichaOutputPort.existePorEvaluacionYEstado(evaluacion, "APROBADA"))
                .thenReturn(false);

        // Act & Assert
        assertThatCode(() -> validator.validarEstadoNoDuplicado(evaluacion, "APROBADA"))
                .doesNotThrowAnyException();
    }

    @Test
    void debeResolverEstado_cuandoCodigoEsConocido() {
        // Act & Assert
        assertThat(validator.resolverEstado("APROBADA")).isEqualTo(EstadoEvaluacion.APROBADA);
    }

    @Test
    void debeLanzarExcepcion_cuandoCodigoDeEstadoEsDesconocido() {
        // Act & Assert
        assertThatThrownBy(() -> validator.resolverEstado("ESTADO_INVENTADO"))
                .isInstanceOf(EstadoEvaluacionNoEncontradoException.class);
    }
}
