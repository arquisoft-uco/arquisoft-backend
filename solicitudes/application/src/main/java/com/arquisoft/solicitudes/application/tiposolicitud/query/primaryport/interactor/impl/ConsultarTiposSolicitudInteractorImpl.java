package com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.interactor.ConsultarTiposSolicitudInteractor;
import com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.mapper.ConsultarTiposSolicitudMapper;
import com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.model.ConsultarTiposSolicitudQuery;
import com.arquisoft.solicitudes.application.tiposolicitud.query.readmodel.TipoSolicitudReadModel;
import com.arquisoft.solicitudes.application.tiposolicitud.query.usecase.ConsultarTiposSolicitudUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarTiposSolicitudInteractorImpl implements ConsultarTiposSolicitudInteractor {

    private final ConsultarTiposSolicitudUseCase consultarTiposSolicitudUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "solicitudesTransactionManager")
    public List<TipoSolicitudReadModel> ejecutar(ConsultarTiposSolicitudQuery entrada) {
        return consultarTiposSolicitudUseCase.ejecutar(ConsultarTiposSolicitudMapper.toCriteria(entrada));
    }
}
