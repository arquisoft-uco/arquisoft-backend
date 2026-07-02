package com.arquisoft.fichas.application.estadoficha.query.port.in;

import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
import com.arquisoft.shared.inputport.InputPort;

import java.util.List;

public interface ConsultarEstadosFichaInputPort extends InputPort<Void, List<EstadoFichaReadModel>> {
}
