package com.arquisoft.evaluaciones.domain.evaluacion;

import com.arquisoft.evaluaciones.domain.evaluacion.model.ItemCualitativoCalificado;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ItemCuantitativoCalificado;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ResumenEvaluacionDomainTest {

    @Test
    void debeDarNotaMaxima_cuandoTodoCumpleYTodoSePuntuaAlMaximo() {
        // Arrange
        var cualitativos = List.of(
                new ItemCualitativoCalificado(2, "Cumple"), new ItemCualitativoCalificado(3, "Cumple"));
        var cuantitativos = List.of(new ItemCuantitativoCalificado(10, 10));

        // Act
        var resumen = ResumenEvaluacionDomain.calcular(cualitativos, cuantitativos);

        // Assert
        assertThat(resumen.getResumenCualitativo()).isEqualByComparingTo("5.00");
        assertThat(resumen.getResumenCuantitativo()).isEqualByComparingTo("5.00");
        assertThat(resumen.getNotaAcumulada()).isEqualByComparingTo("5.00");
        assertThat(resumen.esVacio()).isFalse();
    }

    @Test
    void debeDarCeroEnCualitativo_cuandoNingunCriterioCumple() {
        // Arrange
        var cualitativos = List.of(
                new ItemCualitativoCalificado(2, "No cumple"), new ItemCualitativoCalificado(3, "Otro"));
        var cuantitativos = List.of(new ItemCuantitativoCalificado(10, 10));

        // Act
        var resumen = ResumenEvaluacionDomain.calcular(cualitativos, cuantitativos);

        // Assert
        assertThat(resumen.getResumenCualitativo()).isEqualByComparingTo("0.00");
        assertThat(resumen.getResumenCuantitativo()).isEqualByComparingTo("5.00");
        assertThat(resumen.getNotaAcumulada()).isEqualByComparingTo("3.33");
    }

    @Test
    void debePonderarPorPesoYPuntaje_cuandoElResultadoEsMixto() {
        // Arrange
        var cualitativos = List.of(
                new ItemCualitativoCalificado(1, "Cumple"), new ItemCualitativoCalificado(3, "No cumple"));
        var cuantitativos = List.of(new ItemCuantitativoCalificado(6, 10), new ItemCuantitativoCalificado(4, 6));

        // Act
        var resumen = ResumenEvaluacionDomain.calcular(cualitativos, cuantitativos);

        // Assert
        assertThat(resumen.getResumenCualitativo()).isEqualByComparingTo("1.25");
        assertThat(resumen.getResumenCuantitativo()).isEqualByComparingTo("3.13");
        assertThat(resumen.getNotaAcumulada()).isEqualByComparingTo("2.75");
    }

    @Test
    void debeValerCeroElBloque_cuandoLaSumaDeMaximosEsCero() {
        // Act
        var resumen = ResumenEvaluacionDomain.calcular(List.of(), List.of(new ItemCuantitativoCalificado(5, 10)));

        // Assert
        assertThat(resumen.getResumenCualitativo()).isEqualByComparingTo("0.00");
        assertThat(resumen.getResumenCuantitativo()).isEqualByComparingTo("2.50");
        assertThat(resumen.getNotaAcumulada()).isEqualByComparingTo("2.50");
    }

    @Test
    void debeRedondearHalfUpADosDecimales_cuandoLaDivisionNoEsExacta() {
        // Arrange
        var cuantitativos = List.of(new ItemCuantitativoCalificado(1, 3));

        // Act
        var resumen = ResumenEvaluacionDomain.calcular(List.of(), cuantitativos);

        // Assert
        assertThat(resumen.getResumenCuantitativo()).isEqualTo(new BigDecimal("1.67"));
    }

    @Test
    void debeReconocerseComoSinCalcular_cuandoEsElCentinelaYNoUnaReconstruccion() {
        // Act
        var reconstruido = ResumenEvaluacionDomain.reconstruir(
                new BigDecimal("1.00"), new BigDecimal("2.00"), new BigDecimal("3.00"));

        // Assert
        assertThat(ResumenEvaluacionDomain.VACIO.esVacio()).isTrue();
        assertThat(reconstruido.esVacio()).isFalse();
        assertThat(reconstruido.getNotaAcumulada()).isEqualByComparingTo("3.00");
    }
}
