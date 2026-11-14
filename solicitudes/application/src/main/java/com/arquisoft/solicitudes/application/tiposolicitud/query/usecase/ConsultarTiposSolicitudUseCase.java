package com.arquisoft.solicitudes.application.tiposolicitud.query.usecase;

import com.arquisoft.solicitudes.application.tiposolicitud.query.criteria.TipoSolicitudCriteria;
import com.arquisoft.solicitudes.application.tiposolicitud.query.readmodel.TipoSolicitudReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarTiposSolicitudUseCase extends UseCase<TipoSolicitudCriteria, List<TipoSolicitudReadModel>> {
}
