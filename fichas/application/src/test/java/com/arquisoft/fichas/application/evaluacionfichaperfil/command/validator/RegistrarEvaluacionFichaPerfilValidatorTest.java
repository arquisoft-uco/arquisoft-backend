package com.arquisoft.fichas.application.evaluacionfichaperfil.command.validator;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.validator.impl.RegistrarEvaluacionFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.exception.EvaluacionFichaPerfilDuplicadaException;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.exception.RepresentanteComiteNoEncontradoException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarEvaluacionFichaPerfilValidatorTest {

    private final RegistrarEvaluacionFichaPerfilValidatorImpl validator =
            new RegistrarEvaluacionFichaPerfilValidatorImpl();

    private final UUID representante = UUID.randomUUID();
    private final UUID ficha = UUID.randomUUID();
    private final EvaluacionFichaPerfilDomain evaluacion =
            EvaluacionFichaPerfilDomain.crear(representante, ficha);

    @Test
    void debePasar_cuandoFichaYRepresentanteExistenYNoHayDuplicado() {
        // Act / Assert
        assertThatCode(() -> validator.validar(evaluacion, true, true, false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarFichaNoEncontrada_cuandoLaFichaNoExiste() {
        // Act / Assert
        assertThatThrownBy(() -> validator.validar(evaluacion, false, true, false))
                .isInstanceOf(FichaPerfilNoEncontradaException.class)
                .hasMessageContaining(ficha.toString());
    }

    @Test
    void debeLanzarRepresentanteNoEncontrado_cuandoElRepresentanteNoExiste() {
        // Act / Assert
        assertThatThrownBy(() -> validator.validar(evaluacion, true, false, false))
                .isInstanceOf(RepresentanteComiteNoEncontradoException.class)
                .hasMessageContaining(representante.toString());
    }

    @Test
    void debeLanzarEvaluacionDuplicada_cuandoLaEvaluacionYaExiste() {
        // Act / Assert
        assertThatThrownBy(() -> validator.validar(evaluacion, true, true, true))
                .isInstanceOf(EvaluacionFichaPerfilDuplicadaException.class);
    }

    @Test
    void debeReportarPrimeroLaAusenciaDeLaFicha_cuandoTodasLasReglasFallan() {
        // Act / Assert — el orden es parte del contrato
        assertThatThrownBy(() -> validator.validar(evaluacion, false, false, true))
                .isInstanceOf(FichaPerfilNoEncontradaException.class);
    }
}
