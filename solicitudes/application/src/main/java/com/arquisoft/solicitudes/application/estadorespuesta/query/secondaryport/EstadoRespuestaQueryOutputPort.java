package com.arquisoft.solicitudes.application.estadorespuesta.query.secondaryport;

import com.arquisoft.solicitudes.application.estadorespuesta.query.readmodel.EstadoRespuestaReadModel;

import java.util.List;

public interface EstadoRespuestaQueryOutputPort {

    List<EstadoRespuestaReadModel> findAll();
}
