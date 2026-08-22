package com.arquisoft.fichas.application.estudiantefichaperfil.command.validator;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.impl.RemoverEstudianteFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.RemocionEstudianteFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.EstudianteFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverEstudianteFichaPerfilValidatorTest {

    private final RemoverEstudianteFichaPerfilValidatorImpl validator =
            new RemoverEstudianteFichaPerfilValidatorImpl();

    private final UUID fichaPerfil = UUID.randomUUID();
    private final UUID estudiante = UUID.randomUUID();
    private final RemocionEstudianteFichaPerfilDomain entrada =
            RemocionEstudianteFichaPerfilDomain.crear(fichaPerfil, estudiante);

    @Test
    void debePasar_cuandoLaFichaExisteElEstudianteExisteYHayVinculo() {
        // Act / Assert
        assertThatCode(() -> validator.validar(entrada, true, List.of(estudiante), true))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarFichaNoEncontrada_cuandoLaFichaNoExiste() {
        // Act / Assert
        assertThatThrownBy(() -> validator.validar(entrada, false, List.of(estudiante), true))
                .isInstanceOf(FichaPerfilNoEncontradaException.class)
                .hasMessageContaining(fichaPerfil.toString());
    }

    @Test
    void debeLanzarEstudianteNoEncontrado_cuandoElEstudianteNoExiste() {
        // Act / Assert — lista de existentes vacia: el solicitado no esta
        assertThatThrownBy(() -> validator.validar(entrada, true, List.of(), true))
                .isInstanceOf(EstudianteNoEncontradoException.class);
    }

    @Test
    void debeLanzarVinculoNoEncontrado_cuandoNoHayVinculo() {
        // Act / Assert
        assertThatThrownBy(() -> validator.validar(entrada, true, List.of(estudiante), false))
                .isInstanceOf(EstudianteFichaPerfilNoEncontradoException.class);
    }

    @Test
    void debeReportarPrimeroLaAusenciaDeLaFicha_cuandoTodasLasReglasFallan() {
        // Act / Assert — el orden es parte del contrato: ficha, estudiante y por ultimo vinculo
        assertThatThrownBy(() -> validator.validar(entrada, false, List.of(), false))
                .isInstanceOf(FichaPerfilNoEncontradaException.class);
    }
}
