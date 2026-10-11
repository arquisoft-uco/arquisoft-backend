package com.arquisoft.fichas.application.estudiantefichaperfil.command.validator;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.impl.RemoverEstudianteFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilTerminalException;
import com.arquisoft.fichas.domain.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.RemocionEstudianteFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.EstudianteFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;
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
    private final EstadoFichaPerfilDomain enConstruccion = estadoFicha(EstadoFicha.EN_CONSTRUCCION);

    @Test
    void debePasar_cuandoLaFichaExisteElEstudianteExisteYHayVinculo() {
        // Act / Assert
        assertThatCode(() -> validator.validar(entrada, true, enConstruccion, List.of(estudiante), true))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarFichaNoEncontrada_cuandoLaFichaNoExiste() {
        // Act / Assert
        assertThatThrownBy(() -> validator.validar(entrada, false, enConstruccion, List.of(estudiante), true))
                .isInstanceOf(FichaPerfilNoEncontradaException.class)
                .hasMessageContaining(fichaPerfil.toString());
    }

    @Test
    void debeLanzarEstudianteNoEncontrado_cuandoElEstudianteNoExiste() {
        // Act / Assert — lista de existentes vacia: el solicitado no esta
        assertThatThrownBy(() -> validator.validar(entrada, true, enConstruccion, List.of(), true))
                .isInstanceOf(EstudianteNoEncontradoException.class);
    }

    @Test
    void debeLanzarVinculoNoEncontrado_cuandoNoHayVinculo() {
        // Act / Assert
        assertThatThrownBy(() -> validator.validar(entrada, true, enConstruccion, List.of(estudiante), false))
                .isInstanceOf(EstudianteFichaPerfilNoEncontradoException.class);
    }

    @ParameterizedTest
    @EnumSource(value = EstadoFicha.class, names = {"APROBADA", "APROBADA_CON_OBSERVACIONES", "NO_APROBADA"})
    void debeLanzarEstadoTerminal_cuandoLaFichaEstaFinalizada(EstadoFicha estadoTerminal) {
        // Arrange
        var finalizada = estadoFicha(estadoTerminal);

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(entrada, true, finalizada, List.of(estudiante), true))
                .isInstanceOf(EstadoFichaPerfilTerminalException.class);
    }

    @ParameterizedTest
    @EnumSource(value = EstadoFicha.class, names = {"DISPONIBLE_PARA_EVALUACION", "DESCARTADA"})
    void debePasar_cuandoLaFichaEstaEnUnEstadoNoTerminal(EstadoFicha estadoNoTerminal) {
        // Arrange
        var noTerminal = estadoFicha(estadoNoTerminal);

        // Act / Assert
        assertThatCode(() -> validator.validar(entrada, true, noTerminal, List.of(estudiante), true))
                .doesNotThrowAnyException();
    }

    @Test
    void debeReportarPrimeroLaAusenciaDeLaFicha_cuandoTodasLasReglasFallan() {
        // Arrange
        var finalizada = estadoFicha(EstadoFicha.APROBADA);

        // Act / Assert — el orden es parte del contrato: ficha, estudiante, vinculo y por ultimo estado
        assertThatThrownBy(() -> validator.validar(entrada, false, finalizada, List.of(), false))
                .isInstanceOf(FichaPerfilNoEncontradaException.class);
    }

    private EstadoFichaPerfilDomain estadoFicha(EstadoFicha estado) {
        return EstadoFichaPerfilDomain.reconstruir(UUID.randomUUID(), fichaPerfil, estado, Instant.now());
    }
}
