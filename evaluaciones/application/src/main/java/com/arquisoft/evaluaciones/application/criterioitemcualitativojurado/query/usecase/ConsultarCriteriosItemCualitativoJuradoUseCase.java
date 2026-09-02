package com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.usecase;

import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.readmodel.CriterioItemCualitativoJuradoReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarCriteriosItemCualitativoJuradoUseCase
        extends UseCase<Void, List<CriterioItemCualitativoJuradoReadModel>> {
}
