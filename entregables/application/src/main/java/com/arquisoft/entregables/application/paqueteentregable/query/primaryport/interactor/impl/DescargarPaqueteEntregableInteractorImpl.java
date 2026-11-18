package com.arquisoft.entregables.application.paqueteentregable.query.primaryport.interactor.impl;

import com.arquisoft.entregables.application.paqueteentregable.query.primaryport.interactor.DescargarPaqueteEntregableInteractor;
import com.arquisoft.entregables.application.paqueteentregable.query.primaryport.mapper.DescargarPaqueteEntregableMapper;
import com.arquisoft.entregables.application.paqueteentregable.query.primaryport.model.DescargarPaqueteEntregableQuery;
import com.arquisoft.entregables.application.paqueteentregable.query.readmodel.EnlaceDescargaPaqueteEntregableReadModel;
import com.arquisoft.entregables.application.paqueteentregable.query.usecase.DescargarPaqueteEntregableUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DescargarPaqueteEntregableInteractorImpl implements DescargarPaqueteEntregableInteractor {

    private final DescargarPaqueteEntregableUseCase descargarPaqueteEntregableUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "entregablesTransactionManager")
    public EnlaceDescargaPaqueteEntregableReadModel ejecutar(DescargarPaqueteEntregableQuery entrada) {
        var criteria = DescargarPaqueteEntregableMapper.toCriteria(entrada);
        return descargarPaqueteEntregableUseCase.ejecutar(criteria);
    }
}
