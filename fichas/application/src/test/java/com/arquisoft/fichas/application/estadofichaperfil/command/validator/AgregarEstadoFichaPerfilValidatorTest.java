package com.arquisoft.fichas.application.estadofichaperfil.command.validator;

import com.arquisoft.fichas.application.estadofichaperfil.command.validator.impl.AgregarEstadoFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.AgregacionEstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilRepetidoException;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilTerminalException;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilConEvaluacionEnCursoException;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilEnEsperaDeDecisionException;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilSinEstudiantesVigentesException;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.TransicionEstadoFichaNoPermitidaException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ContactoEstudiante;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.IntegranteFicha;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ConteoEvaluacionesPorEstado;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ResumenEvaluacionesFicha;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPerteneceAsesorException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarEstadoFichaPerfilValidatorTest {

    private final AgregarEstadoFichaPerfilValidatorImpl validator = new AgregarEstadoFichaPerfilValidatorImpl();

    private final UUID asesorFicha = UUID.randomUUID();
    private final FichaPerfilDomain ficha = FichaPerfilDomain.reconstruir(UUID.randomUUID(), "Titulo", asesorFicha);
    private final ResumenEvaluacionesFicha sinEvaluaciones = new ResumenEvaluacionesFicha(ficha.getId(), List.of());
    private final List<IntegranteFicha> unIntegrante = List.of(
            new IntegranteFicha(UUID.randomUUID(), new ContactoEstudiante("Ana Gomez", "ana.gomez@soyuco.edu.co")));

    private AgregacionEstadoFichaPerfilDomain entrada(String estadoNuevo) {
        return AgregacionEstadoFichaPerfilDomain.crear(
                EstadoFichaPerfilDomain.crearPorAsesor(ficha.getId(), estadoNuevo), asesorFicha);
    }

    private EstadoFichaPerfilDomain actual(EstadoFicha estadoFicha) {
        return EstadoFichaPerfilDomain.reconstruir(UUID.randomUUID(), ficha.getId(), estadoFicha, Instant.now());
    }

    @Test
    void debePasar_cuandoTodasLasReglasSeCumplen() {
        // Act & Assert
        assertThatCode(() -> validator.validar(entrada("DISPONIBLE_PARA_EVALUACION"), ficha,
                actual(EstadoFicha.EN_CONSTRUCCION), sinEvaluaciones, unIntegrante))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarFichaNoEncontrada_cuandoLaFichaNoExisteAunqueTodoLoDemasFalle() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada("DESCARTADA"), FichaPerfilDomain.VACIO,
                EstadoFichaPerfilDomain.VACIO, sinEvaluaciones, List.of()))
                .isInstanceOf(FichaPerfilNoEncontradaException.class);
    }

    @Test
    void debeLanzarNoPertenece_cuandoElSolicitanteNoEsElAsesorDeLaFicha() {
        // Arrange
        var fichaAjena = FichaPerfilDomain.reconstruir(ficha.getId(), "Titulo", UUID.randomUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada("DESCARTADA"), fichaAjena,
                EstadoFichaPerfilDomain.VACIO, sinEvaluaciones, List.of()))
                .isInstanceOf(FichaNoPerteneceAsesorException.class);
    }

    @Test
    void debeLanzarEstadoNoEncontrado_cuandoLaFichaNoTieneEstadoActual() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada("DESCARTADA"), ficha,
                EstadoFichaPerfilDomain.VACIO, sinEvaluaciones, List.of()))
                .isInstanceOf(EstadoFichaPerfilNoEncontradoException.class);
    }

    @Test
    void debeLanzarTerminal_cuandoLaFichaEstaEnUnEstadoFinal() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada("EN_CONSTRUCCION"), ficha,
                actual(EstadoFicha.APROBADA), sinEvaluaciones, List.of()))
                .isInstanceOf(EstadoFichaPerfilTerminalException.class);
    }

    @Test
    void debeLanzarRepetido_cuandoSePideElMismoEstadoQueElActual() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada("EN_CONSTRUCCION"), ficha,
                actual(EstadoFicha.EN_CONSTRUCCION), sinEvaluaciones, List.of()))
                .isInstanceOf(EstadoFichaPerfilRepetidoException.class);
    }

    @Test
    void debeLanzarTransicionNoPermitida_cuandoSePasaDeDescartadaADisponible() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada("DISPONIBLE_PARA_EVALUACION"), ficha,
                actual(EstadoFicha.DESCARTADA), sinEvaluaciones, List.of()))
                .isInstanceOf(TransicionEstadoFichaNoPermitidaException.class);
    }

    @Test
    void debeLanzarSinEstudiantesVigentes_cuandoLasDemasReglasPasanYNoHayIntegrantes() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada("DESCARTADA"), ficha,
                actual(EstadoFicha.EN_CONSTRUCCION), sinEvaluaciones, List.of()))
                .isInstanceOf(FichaPerfilSinEstudiantesVigentesException.class);
    }

    private ResumenEvaluacionesFicha resumen(ConteoEvaluacionesPorEstado... conteos) {
        return new ResumenEvaluacionesFicha(ficha.getId(), List.of(conteos));
    }

    @ParameterizedTest
    @ValueSource(strings = {"DESCARTADA", "EN_CONSTRUCCION"})
    void debeLanzarEvaluacionEnCurso_cuandoSaleDeDisponibleConEvaluacionesAbiertas(String destino) {
        // Arrange
        var conEvaluacionAbierta = resumen(new ConteoEvaluacionesPorEstado(EstadoEvaluacion.EN_EVALUACION, 1, 0));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(destino), ficha,
                actual(EstadoFicha.DISPONIBLE_PARA_EVALUACION), conEvaluacionAbierta, List.of()))
                .isInstanceOf(FichaPerfilConEvaluacionEnCursoException.class);
    }

    @ParameterizedTest
    @EnumSource(value = EstadoEvaluacion.class, names = {"APROBADA", "APROBADA_CON_OBSERVACIONES", "NO_APROBADA"})
    void debeLanzarEnEsperaDeDecision_cuandoSoloHayFinalizadasYSaleDeDisponible(EstadoEvaluacion finalizada) {
        // Arrange
        var soloFinalizada = resumen(new ConteoEvaluacionesPorEstado(finalizada, 1, 0));

        // Act & Assert
        for (var destino : List.of("DESCARTADA", "EN_CONSTRUCCION")) {
            assertThatThrownBy(() -> validator.validar(entrada(destino), ficha,
                    actual(EstadoFicha.DISPONIBLE_PARA_EVALUACION), soloFinalizada, unIntegrante))
                    .isInstanceOf(FichaPerfilEnEsperaDeDecisionException.class);
        }
    }

    @Test
    void debeLanzarEnEsperaDeDecision_cuandoHayFinalizadasYDescartadas() {
        // Arrange
        var finalizadaYDescartada = resumen(
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.NO_APROBADA, 1, 0),
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.DESCARTADA, 2, 0));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada("DESCARTADA"), ficha,
                actual(EstadoFicha.DISPONIBLE_PARA_EVALUACION), finalizadaYDescartada, unIntegrante))
                .isInstanceOf(FichaPerfilEnEsperaDeDecisionException.class);
    }

    @Test
    void debeLanzarEvaluacionEnCurso_antesQueEnEsperaDeDecision_cuandoHayUnaEnCursoYUnaFinalizada() {
        // Arrange
        var enCursoYFinalizada = resumen(
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.EN_EVALUACION, 1, 0),
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.APROBADA, 1, 0));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada("EN_CONSTRUCCION"), ficha,
                actual(EstadoFicha.DISPONIBLE_PARA_EVALUACION), enCursoYFinalizada, unIntegrante))
                .isInstanceOf(FichaPerfilConEvaluacionEnCursoException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"DESCARTADA", "EN_CONSTRUCCION"})
    void debePasar_cuandoSaleDeDisponibleSinEvaluacionesOConTodasDescartadas(String destino) {
        // Arrange
        var todasDescartadas = resumen(new ConteoEvaluacionesPorEstado(EstadoEvaluacion.DESCARTADA, 2, 0));

        // Act & Assert
        assertThatCode(() -> validator.validar(entrada(destino), ficha,
                actual(EstadoFicha.DISPONIBLE_PARA_EVALUACION), todasDescartadas, unIntegrante))
                .doesNotThrowAnyException();
        assertThatCode(() -> validator.validar(entrada(destino), ficha,
                actual(EstadoFicha.DISPONIBLE_PARA_EVALUACION), sinEvaluaciones, unIntegrante))
                .doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoElEstadoActualNoEsDisponibleAunqueHayaFinalizadas() {
        // Arrange
        var conFinalizada = resumen(new ConteoEvaluacionesPorEstado(EstadoEvaluacion.APROBADA, 1, 0));

        // Act & Assert
        assertThatCode(() -> validator.validar(entrada("DISPONIBLE_PARA_EVALUACION"), ficha,
                actual(EstadoFicha.EN_CONSTRUCCION), conFinalizada, unIntegrante))
                .doesNotThrowAnyException();
        assertThatCode(() -> validator.validar(entrada("EN_CONSTRUCCION"), ficha,
                actual(EstadoFicha.DESCARTADA), conFinalizada, unIntegrante))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarEnEsperaDeDecision_antesQueSinEstudiantesVigentes_cuandoAmbasFallan() {
        // Arrange
        var conFinalizada = resumen(new ConteoEvaluacionesPorEstado(EstadoEvaluacion.APROBADA, 1, 0));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada("DESCARTADA"), ficha,
                actual(EstadoFicha.DISPONIBLE_PARA_EVALUACION), conFinalizada, List.of()))
                .isInstanceOf(FichaPerfilEnEsperaDeDecisionException.class);
    }
}
