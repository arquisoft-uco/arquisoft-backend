package com.arquisoft.solicitudes.infrastructure.tiposolicitud.query.secondaryadapter.repository;

import com.arquisoft.solicitudes.application.tiposolicitud.query.readmodel.TipoSolicitudReadModel;
import com.arquisoft.solicitudes.application.tiposolicitud.query.secondaryport.TipoSolicitudQueryOutputPort;
import com.arquisoft.solicitudes.infrastructure.tiposolicitud.query.secondaryadapter.repository.mapper.TipoSolicitudQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TipoSolicitudQueryOutputAdapter implements TipoSolicitudQueryOutputPort {

    private final TipoSolicitudQueryRepository repository;

    @Override
    public List<TipoSolicitudReadModel> findAll() {
        return repository.findAll()
                .stream()
                .map(TipoSolicitudQueryMapper::toReadModel)
                .toList();
    }
}
