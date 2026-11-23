package com.arquisoft.evaluaciones.application.evaluacion.command.validator.impl;

import com.arquisoft.evaluaciones.domain.asesor.model.ContactoAsesor;
import com.arquisoft.evaluaciones.domain.coordinadorproyectogrado.model.ContactoCoordinador;
import com.arquisoft.evaluaciones.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.evaluaciones.domain.evaluacion.ResumenEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.exception.DestinatariosEvaluacionIncompletosException;
import com.arquisoft.evaluaciones.domain.evaluacion.exception.ResumenesEvaluacionIncompletosException;
import com.arquisoft.evaluaciones.domain.evaluacion.model.DestinatariosEvaluacionFinalizada;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ResumenesEvaluacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FinalizarEvaluacionValidatorImplTest {

    private static final ResumenEvaluacionDomain CALCULADO =
            ResumenEvaluacionDomain.reconstruir(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);

    private final FinalizarEvaluacionValidatorImpl validator = new FinalizarEvaluacionValidatorImpl();

    private static DestinatariosEvaluacionFinalizada destinatariosCompletos() {
        return new DestinatariosEvaluacionFinalizada(
                UtilUUID.generarNuevoUUID(),
                new ContactoCoordinador(UtilUUID.generarNuevoUUID(), "Coord", "coord@uco.edu.co"),
                List.of(new ContactoEstudiante(UtilUUID.generarNuevoUUID(), "Est", "est@uco.edu.co")),
                new ContactoAsesor(UtilUUID.generarNuevoUUID(), "Asesor", "asesor@uco.edu.co"));
    }

    private static DestinatariosEvaluacionFinalizada destinatariosIncompletos() {
        return new DestinatariosEvaluacionFinalizada(
                UtilUUID.generarNuevoUUID(), ContactoCoordinador.VACIO, List.of(), ContactoAsesor.VACIO);
    }

    @Test
    void debePermitirFlujo_cuandoResumenesYDestinatariosEstanCompletos() {
        // Arrange
        var resumenes = new ResumenesEvaluacion(UtilUUID.generarNuevoUUID(), CALCULADO, List.of(CALCULADO), 1);

        // Act & Assert
        assertThatCode(() -> validator.validar(resumenes, destinatariosCompletos())).doesNotThrowAnyException();
    }

    @Test
    void debeGanarLaRegladeResumenes_cuandoTambienFaltanDestinatarios() {
        // Arrange
        var resumenes = new ResumenesEvaluacion(
                UtilUUID.generarNuevoUUID(), ResumenEvaluacionDomain.VACIO, List.of(CALCULADO), 1);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(resumenes, destinatariosIncompletos()))
                .isInstanceOf(ResumenesEvaluacionIncompletosException.class);
    }

    @Test
    void debeLanzarDestinatariosIncompletos_cuandoLosResumenesEstanCompletos() {
        // Arrange
        var resumenes = new ResumenesEvaluacion(UtilUUID.generarNuevoUUID(), CALCULADO, List.of(CALCULADO), 1);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(resumenes, destinatariosIncompletos()))
                .isInstanceOf(DestinatariosEvaluacionIncompletosException.class);
    }
}
