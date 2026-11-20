package com.arquisoft.evaluaciones.domain.evaluacion;

import com.arquisoft.evaluaciones.domain.evaluacion.model.CumplimientoCriterio;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ItemCualitativoCalificado;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ItemCuantitativoCalificado;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilNumero;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public final class ResumenEvaluacionDomain {

    public static final ResumenEvaluacionDomain VACIO = reconstruir(
            UtilNumero.CERO_DECIMAL, UtilNumero.CERO_DECIMAL, UtilNumero.CERO_DECIMAL);

    private BigDecimal resumenCualitativo;
    private BigDecimal resumenCuantitativo;
    private BigDecimal notaAcumulada;

    private ResumenEvaluacionDomain() {}

    public static ResumenEvaluacionDomain calcular(
            List<ItemCualitativoCalificado> cualitativos, List<ItemCuantitativoCalificado> cuantitativos) {
        var puntosCualitativos = cualitativos.stream()
                .filter(item -> CumplimientoCriterio.cumple(item.criterio()))
                .mapToLong(ItemCualitativoCalificado::peso)
                .sum();
        var maximoCualitativo = cualitativos.stream().mapToLong(ItemCualitativoCalificado::peso).sum();
        var puntosCuantitativos = cuantitativos.stream().mapToLong(ItemCuantitativoCalificado::puntaje).sum();
        var maximoCuantitativo = cuantitativos.stream().mapToLong(ItemCuantitativoCalificado::valorMaximo).sum();

        return reconstruir(
                nota(puntosCualitativos, maximoCualitativo),
                nota(puntosCuantitativos, maximoCuantitativo),
                nota(puntosCualitativos + puntosCuantitativos, maximoCualitativo + maximoCuantitativo));
    }

    public static ResumenEvaluacionDomain reconstruir(
            BigDecimal resumenCualitativo, BigDecimal resumenCuantitativo, BigDecimal notaAcumulada) {
        var resumen = new ResumenEvaluacionDomain();
        resumen.resumenCualitativo = resumenCualitativo;
        resumen.resumenCuantitativo = resumenCuantitativo;
        resumen.notaAcumulada = notaAcumulada;
        return resumen;
    }

    private static BigDecimal nota(long puntos, long maximo) {
        if (maximo == 0) {
            return BigDecimal.ZERO.setScale(EvaluacionesLimits.Evaluacion.DECIMALES_NOTA);
        }
        return BigDecimal.valueOf(puntos)
                .multiply(BigDecimal.valueOf(EvaluacionesLimits.Evaluacion.NOTA_MAXIMA))
                .divide(BigDecimal.valueOf(maximo),
                        EvaluacionesLimits.Evaluacion.DECIMALES_NOTA, RoundingMode.HALF_UP);
    }

    public boolean esVacio() {
        return this == VACIO;
    }

    public BigDecimal getResumenCualitativo() {
        return resumenCualitativo;
    }

    public BigDecimal getResumenCuantitativo() {
        return resumenCuantitativo;
    }

    public BigDecimal getNotaAcumulada() {
        return notaAcumulada;
    }
}
