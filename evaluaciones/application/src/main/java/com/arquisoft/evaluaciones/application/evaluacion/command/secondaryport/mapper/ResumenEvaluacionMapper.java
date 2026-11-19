package com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ItemCualitativoCalificadoEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ItemCuantitativoCalificadoEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ResumenEvaluacionEntity;
import com.arquisoft.evaluaciones.domain.evaluacion.ResumenEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ItemCualitativoCalificado;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ItemCuantitativoCalificado;
import com.arquisoft.shared.util.UtilObjeto;

public final class ResumenEvaluacionMapper {

    private ResumenEvaluacionMapper() {}

    public static ResumenEvaluacionEntity toEntity(ResumenEvaluacionDomain domain) {
        if (domain.esVacio()) {
            return new ResumenEvaluacionEntity(null, null, null);
        }
        return new ResumenEvaluacionEntity(
                domain.getResumenCualitativo(), domain.getResumenCuantitativo(), domain.getNotaAcumulada());
    }

    public static ResumenEvaluacionDomain toDomain(ResumenEvaluacionEntity entity) {
        if (UtilObjeto.esNulo(entity.notaAcumulada())) {
            return ResumenEvaluacionDomain.VACIO;
        }
        return ResumenEvaluacionDomain.reconstruir(
                entity.resumenCualitativo(), entity.resumenCuantitativo(), entity.notaAcumulada());
    }

    public static ItemCualitativoCalificado toDomain(ItemCualitativoCalificadoEntity entity) {
        return new ItemCualitativoCalificado(entity.peso(), entity.criterio());
    }

    public static ItemCuantitativoCalificado toDomain(ItemCuantitativoCalificadoEntity entity) {
        return new ItemCuantitativoCalificado(entity.puntaje(), entity.valorMaximo());
    }
}
