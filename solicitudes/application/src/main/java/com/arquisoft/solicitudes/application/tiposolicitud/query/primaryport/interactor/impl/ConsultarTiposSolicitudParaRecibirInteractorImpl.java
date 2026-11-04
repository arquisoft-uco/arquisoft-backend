package com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.interactor.ConsultarTiposSolicitudParaRecibirInteractor;
import com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.mapper.ConsultarTiposSolicitudParaRecibirMapper;
import com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.model.ConsultarTiposSolicitudParaRecibirQuery;
import com.arquisoft.solicitudes.application.tiposolicitud.query.readmodel.TipoSolicitudReadModel;
import com.arquisoft.solicitudes.application.tiposolicitud.query.usecase.ConsultarTiposSolicitudParaRecibirUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarTiposSolicitudParaRecibirInteractorImpl implements ConsultarTiposSolicitudParaRecibirInteractor {

    private final ConsultarTiposSolicitudParaRecibirUseCase consultarTiposSolicitudParaRecibirUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "solicitudesTransactionManager")
    public List<TipoSolicitudReadModel> ejecutar(ConsultarTiposSolicitudParaRecibirQuery entrada) {
        return consultarTiposSolicitudParaRecibirUseCase.ejecutar(
                ConsultarTiposSolicitudParaRecibirMapper.toCriteria(entrada));
    }
}
