package com.arquisoft.artefactos.application.observacion.query.secondaryport;

import com.arquisoft.artefactos.application.observacion.query.readmodel.ObservacionReadModel;

import java.util.List;
import java.util.UUID;

public interface ObservacionQueryOutputPort {

    List<ObservacionReadModel> consultarPorRevisionAsesor(UUID revisionAsesor);
}
