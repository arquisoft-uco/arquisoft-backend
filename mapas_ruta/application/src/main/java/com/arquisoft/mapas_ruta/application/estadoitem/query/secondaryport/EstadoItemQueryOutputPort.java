package com.arquisoft.mapas_ruta.application.estadoitem.query.secondaryport;

import com.arquisoft.mapas_ruta.application.estadoitem.query.readmodel.EstadoItemReadModel;

import java.util.List;

public interface EstadoItemQueryOutputPort {

    List<EstadoItemReadModel> findAll();
}
