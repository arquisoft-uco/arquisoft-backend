package com.arquisoft.solicitudes.application.tiposolicitud.query.usecase;

import com.arquisoft.solicitudes.application.tiposolicitud.query.criteria.TipoSolicitudParaRecibirCriteria;
import com.arquisoft.solicitudes.application.tiposolicitud.query.readmodel.TipoSolicitudReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarTiposSolicitudParaRecibirUseCase
        extends UseCase<TipoSolicitudParaRecibirCriteria, List<TipoSolicitudReadModel>> {
}
