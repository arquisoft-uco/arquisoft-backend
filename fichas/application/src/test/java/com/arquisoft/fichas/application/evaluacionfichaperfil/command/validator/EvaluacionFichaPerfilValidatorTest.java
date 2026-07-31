package com.arquisoft.fichas.application.evaluacionfichaperfil.command.validator;

import com.arquisoft.fichas.application.evaluacionfichaperfil.exception.EvaluacionFichaPerfilDuplicadaException;
import com.arquisoft.fichas.application.evaluacionfichaperfil.exception.RepresentanteComiteNoEncontradoException;
import com.arquisoft.fichas.application.representantecomite.query.port.out.RepresentanteComiteQueryOutputPort;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.port.out.EvaluacionFichaPerfilOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionFichaPerfilValidatorTest {

    @Mock
    private RepresentanteComiteQueryOutputPort representanteComiteQueryOutputPort;

    @Mock
    private EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;

    @InjectMocks
    private EvaluacionFichaPerfilValidator validator;

    @Test
    void debeLanzarExcepcion_cuandoRepresentanteNoExiste() {
        // Arrange
        UUID representante = UUID.randomUUID();
        when(representanteComiteQueryOutputPort.existePorId(representante)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validarRepresentanteExiste(representante))
                .isInstanceOf(RepresentanteComiteNoEncontradoException.class);
    }

    @Test
    void debePasar_cuandoRepresentanteExiste() {
        // Arrange
        UUID representante = UUID.randomUUID();
        when(representanteComiteQueryOutputPort.existePorId(representante)).thenReturn(true);

        // Act & Assert
        assertThatCode(() -> validator.validarRepresentanteExiste(representante))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoRepresentanteYaEvaluoLaFicha() {
        // Arrange
        UUID representante = UUID.randomUUID();
        UUID ficha = UUID.randomUUID();
        when(evaluacionFichaPerfilOutputPort.existePorRepresentanteYFicha(representante, ficha))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> validator.validarEvaluacionNoDuplicada(representante, ficha))
                .isInstanceOf(EvaluacionFichaPerfilDuplicadaException.class);
    }

    @Test
    void debePasar_cuandoNoHayEvaluacionPrevia() {
        // Arrange
        UUID representante = UUID.randomUUID();
        UUID ficha = UUID.randomUUID();
        when(evaluacionFichaPerfilOutputPort.existePorRepresentanteYFicha(representante, ficha))
                .thenReturn(false);

        // Act & Assert
        assertThatCode(() -> validator.validarEvaluacionNoDuplicada(representante, ficha))
                .doesNotThrowAnyException();
    }
}
