package com.arquisoft.solicitudes.application.tiposolicitud.query.usecase;

import com.arquisoft.solicitudes.application.tiposolicitud.query.readmodel.TipoSolicitudReadModel;
import com.arquisoft.shared.usecase.SupplierUseCase;

import java.util.List;

public interface ConsultarTiposSolicitudUseCase extends SupplierUseCase<List<TipoSolicitudReadModel>> {
}
