package com.arquisoft.fichas.application.estudiantefichaperfil.command.validator;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.impl.AsignarEstudiantesFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilNoEnConstruccionException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.AgregacionEstudiantesFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.CupoEstudiantesExcedidoException;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsignarEstudiantesFichaPerfilValidatorTest {

    private static final long CUPO_COMPLETO = 3L;

    private final AsignarEstudiantesFichaPerfilValidatorImpl validator =
            new AsignarEstudiantesFichaPerfilValidatorImpl();

    private final FichaPerfilDomain ficha = FichaPerfilDomain.crear("Sistema de gestión", UUID.randomUUID());
    private final UUID fichaPerfil = ficha.getId();
    private final UUID estudiante = UUID.randomUUID();
    private final AgregacionEstudiantesFichaPerfilDomain entrada = AgregacionEstudiantesFichaPerfilDomain.crear(
            EstudianteFichaPerfilDomain.crear(fichaPerfil, List.of(estudiante)));
    private final EstadoFichaPerfilDomain enConstruccion = estadoFicha(EstadoFicha.EN_CONSTRUCCION);

    @Test
    void debePasar_cuandoLaFichaEstaEnConstruccionYHayCupo() {
        // Act / Assert
        assertThatCode(() -> validator.validar(entrada, ficha, enConstruccion, List.of(estudiante), List.of(), 0L))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoFicha.class, names = {
            "DISPONIBLE_PARA_EVALUACION", "DESCARTADA", "APROBADA", "APROBADA_CON_OBSERVACIONES", "NO_APROBADA"})
    void debeLanzarNoEnConstruccion_cuandoLaFichaNoEstaEnConstruccion(EstadoFicha noPermitido) {
        // Arrange
        var estado = estadoFicha(noPermitido);

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(entrada, ficha, estado, List.of(estudiante), List.of(), 0L))
                .isInstanceOf(FichaPerfilNoEnConstruccionException.class);
    }

    @Test
    void debeLanzarEstadoNoEncontrado_cuandoLaFichaNoTieneEstado() {
        // Act / Assert
        assertThatThrownBy(() -> validator.validar(
                entrada, ficha, EstadoFichaPerfilDomain.VACIO, List.of(estudiante), List.of(), 0L))
                .isInstanceOf(EstadoFichaPerfilNoEncontradoException.class);
    }

    @Test
    void debeLanzarCupoExcedido_cuandoLaFichaNoTieneCupo() {
        // Act / Assert
        assertThatThrownBy(() -> validator.validar(
                entrada, ficha, enConstruccion, List.of(estudiante), List.of(), CUPO_COMPLETO))
                .isInstanceOf(CupoEstudiantesExcedidoException.class);
    }

    @Test
    void debeReportarPrimeroLaAusenciaDeLaFicha_cuandoLaFichaNoExisteYEstaFinalizada() {
        // Arrange
        var finalizada = estadoFicha(EstadoFicha.APROBADA);

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(
                entrada, FichaPerfilDomain.VACIO, finalizada, List.of(estudiante), List.of(), 0L))
                .isInstanceOf(FichaPerfilNoEncontradaException.class);
    }

    @Test
    void debeLanzarNoEnConstruccion_antesQueCupo() {
        // Arrange
        var finalizada = estadoFicha(EstadoFicha.NO_APROBADA);

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(
                entrada, ficha, finalizada, List.of(estudiante), List.of(), CUPO_COMPLETO))
                .isInstanceOf(FichaPerfilNoEnConstruccionException.class);
    }

    private EstadoFichaPerfilDomain estadoFicha(EstadoFicha estado) {
        return EstadoFichaPerfilDomain.reconstruir(UUID.randomUUID(), fichaPerfil, estado, Instant.now());
    }
}
