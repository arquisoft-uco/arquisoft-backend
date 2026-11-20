package com.arquisoft.evaluaciones.domain.evaluacion;

import com.arquisoft.evaluaciones.domain.evaluacion.model.PesosFinalizacion;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

public final class ConsolidacionNotasEvaluacionDomain {

    private static final int DECIMALES_FRACCION = 4;

    private UUID evaluacion;
    private BigDecimal notaAsesor;
    private BigDecimal notaJurados;
    private BigDecimal pesoAsesor;
    private BigDecimal pesoJurados;
    private int cantidadJurados;

    private ConsolidacionNotasEvaluacionDomain() {}

    public static ConsolidacionNotasEvaluacionDomain calcular(
            UUID evaluacion,
            ResumenEvaluacionDomain asesor,
            List<ResumenEvaluacionDomain> jurados,
            PesosFinalizacion pesos) {
        var consolidacion = new ConsolidacionNotasEvaluacionDomain();
        var resultado = new ValidationResult();

        consolidacion.setEvaluacion(evaluacion, resultado);
        consolidacion.setNotaAsesor(asesor, resultado);
        consolidacion.setNotaJurados(jurados, resultado);
        var pesosValidos = consolidacion.pesosValidos(pesos, resultado);

        resultado.lanzarSiTieneErrores();
        consolidacion.setPesos(pesos, pesosValidos);
        return consolidacion;
    }

    private void setEvaluacion(UUID evaluacion, ValidationResult resultado) {
        if (!ValidatorObjeto.noNulo(
                evaluacion,
                EvaluacionesFields.ConsolidacionNotasEvaluacion.EVALUACION,
                EvaluacionesCodes.ConsolidacionNotasEvaluacion.EVALUACION_REQUERIDA,
                resultado)) {
            return;
        }
        this.evaluacion = evaluacion;
    }

    private void setNotaAsesor(ResumenEvaluacionDomain asesor, ValidationResult resultado) {
        var asesorCalculado = UtilObjeto.noEsNulo(asesor) && !asesor.esVacio() ? asesor : null;
        if (!ValidatorObjeto.noNulo(
                asesorCalculado,
                EvaluacionesFields.ConsolidacionNotasEvaluacion.NOTA_ASESOR,
                EvaluacionesCodes.ConsolidacionNotasEvaluacion.NOTA_ASESOR_REQUERIDA,
                resultado)) {
            return;
        }
        this.notaAsesor = asesorCalculado.getNotaAcumulada();
    }

    private void setNotaJurados(List<ResumenEvaluacionDomain> jurados, ValidationResult resultado) {
        if (!ValidatorColeccion.noVacia(
                jurados,
                EvaluacionesFields.ConsolidacionNotasEvaluacion.NOTAS_JURADOS,
                EvaluacionesCodes.ConsolidacionNotasEvaluacion.NOTAS_JURADOS_REQUERIDAS,
                resultado)) {
            return;
        }
        var juradosCalculados = jurados.stream().allMatch(jurado -> UtilObjeto.noEsNulo(jurado) && !jurado.esVacio());
        if (!ValidatorObjeto.noNulo(
                juradosCalculados ? jurados : null,
                EvaluacionesFields.ConsolidacionNotasEvaluacion.NOTAS_JURADOS,
                EvaluacionesCodes.ConsolidacionNotasEvaluacion.NOTAS_JURADOS_REQUERIDAS,
                resultado)) {
            return;
        }
        this.cantidadJurados = jurados.size();
        this.notaJurados = jurados.stream()
                .map(ResumenEvaluacionDomain::getNotaAcumulada)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(jurados.size()),
                        EvaluacionesLimits.Evaluacion.DECIMALES_NOTA, RoundingMode.HALF_UP);
    }

    private boolean pesosValidos(PesosFinalizacion pesos, ValidationResult resultado) {
        var pesoAsesorValido = pesoValido(
                UtilObjeto.noEsNulo(pesos) ? pesos.pesoAsesor() : null,
                EvaluacionesFields.ConsolidacionNotasEvaluacion.PESO_ASESOR,
                EvaluacionesCodes.ConsolidacionNotasEvaluacion.PESO_ASESOR_INVALIDO,
                resultado);
        var pesoJuradoValido = pesoValido(
                UtilObjeto.noEsNulo(pesos) ? pesos.pesoJurado() : null,
                EvaluacionesFields.ConsolidacionNotasEvaluacion.PESO_JURADO,
                EvaluacionesCodes.ConsolidacionNotasEvaluacion.PESO_JURADO_INVALIDO,
                resultado);
        return pesoAsesorValido && pesoJuradoValido;
    }

    private static boolean pesoValido(BigDecimal peso, String campo, String codigo, ValidationResult resultado) {
        return ValidatorObjeto.noNulo(peso, campo, codigo, resultado)
                && ValidatorNumero.valorMinimo(
                        peso, EvaluacionesLimits.Evaluacion.PESO_FINALIZACION_MIN, campo, codigo, resultado);
    }

    private void setPesos(PesosFinalizacion pesos, boolean pesosValidos) {
        if (!pesosValidos) {
            return;
        }
        var totalJurados = pesos.pesoJurado().multiply(BigDecimal.valueOf(cantidadJurados));
        this.pesoAsesor = pesos.pesoAsesor()
                .divide(pesos.pesoAsesor().add(totalJurados), DECIMALES_FRACCION, RoundingMode.HALF_UP);
        this.pesoJurados = BigDecimal.ONE.setScale(DECIMALES_FRACCION).subtract(pesoAsesor);
    }

    public UUID getEvaluacion() {
        return evaluacion;
    }

    public BigDecimal getNotaAsesor() {
        return notaAsesor;
    }

    public BigDecimal getNotaJurados() {
        return notaJurados;
    }

    public BigDecimal getPesoAsesor() {
        return pesoAsesor;
    }

    public BigDecimal getPesoJurados() {
        return pesoJurados;
    }

    public int getCantidadJurados() {
        return cantidadJurados;
    }
}
