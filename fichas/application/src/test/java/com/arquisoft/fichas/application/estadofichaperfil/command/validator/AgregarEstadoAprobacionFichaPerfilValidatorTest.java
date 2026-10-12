package com.arquisoft.fichas.application.estadofichaperfil.command.validator;

import com.arquisoft.fichas.application.estadofichaperfil.command.validator.impl.AgregarEstadoAprobacionFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.DecisionFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.AprobacionSinEvaluacionAprobatoriaException;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilNoDisponibleParaEvaluacionException;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilSinEstudiantesVigentesException;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilSinEvaluacionFinalizadaException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ContactoEstudiante;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.IntegranteFicha;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ConteoEvaluacionesPorEstado;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ResumenEvaluacionesFicha;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarEstadoAprobacionFichaPerfilValidatorTest {

    private final AgregarEstadoAprobacionFichaPerfilValidatorImpl validator =
            new AgregarEstadoAprobacionFichaPerfilValidatorImpl();

    private final UUID asesorFicha = UUID.randomUUID();
    private final FichaPerfilDomain ficha = FichaPerfilDomain.reconstruir(UUID.randomUUID(), "Titulo", asesorFicha);
    private final AsesorFichaDomain asesor = AsesorFichaDomain.reconstruir(asesorFicha, "1020", "Carlos Ruiz",
            "carlos.ruiz@soyuco.edu.co", Instant.parse("2026-09-01T10:00:00Z"), null);
    private final List<IntegranteFicha> unIntegrante = List.of(
            new IntegranteFicha(UUID.randomUUID(), new ContactoEstudiante("Ana Gomez", "ana.gomez@soyuco.edu.co")));

    private DecisionFichaPerfilDomain decision(boolean acepta) {
        return DecisionFichaPerfilDomain.crear(ficha.getId(), acepta, UUID.randomUUID());
    }

    private EstadoFichaPerfilDomain estado(EstadoFicha estadoFicha) {
        return EstadoFichaPerfilDomain.reconstruir(UUID.randomUUID(), ficha.getId(), estadoFicha, Instant.now());
    }

    private ResumenEvaluacionesFicha resumen(ConteoEvaluacionesPorEstado... conteos) {
        return new ResumenEvaluacionesFicha(ficha.getId(), List.of(conteos));
    }

    private ResumenEvaluacionesFicha resumenAprobatorio() {
        return resumen(new ConteoEvaluacionesPorEstado(EstadoEvaluacion.APROBADA, 1, 0));
    }

    @Test
    void debePasar_cuandoTodasLasReglasSeCumplen() {
        // Act & Assert
        assertThatCode(() -> validator.validar(decision(true), ficha, asesor,
                estado(EstadoFicha.DISPONIBLE_PARA_EVALUACION), resumenAprobatorio(), unIntegrante))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarFichaNoEncontrada_cuandoLaFichaNoExisteAunqueTodoLoDemasFalle() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(decision(true), FichaPerfilDomain.VACIO, AsesorFichaDomain.VACIO,
                EstadoFichaPerfilDomain.VACIO, resumen(), List.of()))
                .isInstanceOf(FichaPerfilNoEncontradaException.class);
    }

    @Test
    void debeLanzarAsesorNoEncontrado_cuandoLaFichaExistePeroSuAsesorNo() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(decision(true), ficha, AsesorFichaDomain.VACIO,
                estado(EstadoFicha.EN_CONSTRUCCION), resumen(), List.of()))
                .isInstanceOf(AsesorFichaNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoDisponible_cuandoLaFichaYaTieneUnEstadoTerminal() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(decision(true), ficha, asesor,
                estado(EstadoFicha.APROBADA), resumen(), List.of()))
                .isInstanceOf(FichaPerfilNoDisponibleParaEvaluacionException.class);
    }

    @Test
    void debeLanzarSinEvaluacionFinalizada_antesQueLaReglaAprobatoria_cuandoSoloHayEvaluacionesAbiertas() {
        // Arrange
        var soloAbiertas = resumen(new ConteoEvaluacionesPorEstado(EstadoEvaluacion.EN_EVALUACION, 2, 0));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(decision(true), ficha, asesor,
                estado(EstadoFicha.DISPONIBLE_PARA_EVALUACION), soloAbiertas, List.of()))
                .isInstanceOf(FichaPerfilSinEvaluacionFinalizadaException.class);
    }

    @Test
    void debeLanzarAprobacionSinRespaldo_cuandoAceptaYTodasLasFinalizadasSonNoAprobadas() {
        // Arrange
        var soloNoAprobadas = resumen(new ConteoEvaluacionesPorEstado(EstadoEvaluacion.NO_APROBADA, 2, 0));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(decision(true), ficha, asesor,
                estado(EstadoFicha.DISPONIBLE_PARA_EVALUACION), soloNoAprobadas, List.of()))
                .isInstanceOf(AprobacionSinEvaluacionAprobatoriaException.class);
    }

    @Test
    void debePasar_cuandoNoAceptaConUnaEvaluacionNoAprobada() {
        // Arrange
        var soloNoAprobadas = resumen(new ConteoEvaluacionesPorEstado(EstadoEvaluacion.NO_APROBADA, 1, 0));

        // Act & Assert
        assertThatCode(() -> validator.validar(decision(false), ficha, asesor,
                estado(EstadoFicha.DISPONIBLE_PARA_EVALUACION), soloNoAprobadas, unIntegrante))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSinEstudiantesVigentes_cuandoLasDemasReglasPasanYNoHayIntegrantes() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(decision(true), ficha, asesor,
                estado(EstadoFicha.DISPONIBLE_PARA_EVALUACION), resumenAprobatorio(), List.of()))
                .isInstanceOf(FichaPerfilSinEstudiantesVigentesException.class);
    }
}
