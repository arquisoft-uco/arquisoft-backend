package com.arquisoft.entregables.application.paqueteentregable.query.usecase;

import com.arquisoft.entregables.application.paqueteentregable.query.criteria.DescargaPaqueteEntregableCriteria;
import com.arquisoft.entregables.application.paqueteentregable.query.readmodel.EnlaceDescargaPaqueteEntregableReadModel;
import com.arquisoft.shared.usecase.UseCase;

public interface DescargarPaqueteEntregableUseCase
        extends UseCase<DescargaPaqueteEntregableCriteria, EnlaceDescargaPaqueteEntregableReadModel> {
}
