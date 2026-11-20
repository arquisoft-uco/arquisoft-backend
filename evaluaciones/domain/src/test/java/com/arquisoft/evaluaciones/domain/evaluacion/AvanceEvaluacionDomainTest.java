package com.arquisoft.evaluaciones.domain.evaluacion;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.AvanceEvaluacionDomain.DatosEntregable;
import com.arquisoft.evaluaciones.domain.evaluacion.AvanceEvaluacionDomain.ProgresoParticipantes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AvanceEvaluacionDomainTest {

    private static AvanceEvaluacionDomain avance(
            EstadoEvaluacion estado, long vigentes, long evaluados, boolean asesorCompleto) {
        return AvanceEvaluacionDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), estado,
                new DatosEntregable(UtilUUID.generarNuevoUUID(), "Proyecto", 1),
                new ProgresoParticipantes(vigentes, evaluados, asesorCompleto));
    }

    @Test
    void debePoderFinalizarse_cuandoTodosTerminaron() {
        // Act
        var puede = avance(EstadoEvaluacion.EN_PROGRESO, 2, 2, true).puedeFinalizarse();

        // Assert
        assertThat(puede).isTrue();
    }

    @Test
    void debeNoPoderFinalizarse_cuandoYaEstaFinalizada() {
        // Act
        var puede = avance(EstadoEvaluacion.FINALIZADA, 2, 2, true).puedeFinalizarse();

        // Assert
        assertThat(puede).isFalse();
    }

    @Test
    void debeNoPoderFinalizarse_cuandoNoHayJuradosVigentes() {
        // Act
        var puede = avance(EstadoEvaluacion.EN_PROGRESO, 0, 0, true).puedeFinalizarse();

        // Assert
        assertThat(puede).isFalse();
    }

    @Test
    void debeNoPoderFinalizarse_cuandoUnJuradoSigueSinEvaluar() {
        // Act
        var puede = avance(EstadoEvaluacion.EN_PROGRESO, 2, 1, true).puedeFinalizarse();

        // Assert
        assertThat(puede).isFalse();
    }

    @Test
    void debeNoPoderFinalizarse_cuandoElAsesorNoCompleto() {
        // Act
        var puede = avance(EstadoEvaluacion.EN_PROGRESO, 2, 2, false).puedeFinalizarse();

        // Assert
        assertThat(puede).isFalse();
    }

    @Test
    void debeNoPoderFinalizarse_cuandoEsElCentinelaVacio() {
        // Act & Assert
        assertThat(AvanceEvaluacionDomain.VACIO.esVacio()).isTrue();
        assertThat(AvanceEvaluacionDomain.VACIO.puedeFinalizarse()).isFalse();
    }
}
