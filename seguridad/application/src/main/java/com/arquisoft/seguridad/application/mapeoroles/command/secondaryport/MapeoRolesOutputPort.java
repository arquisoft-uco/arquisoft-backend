package com.arquisoft.seguridad.application.mapeoroles.command.secondaryport;

import com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.entity.MapeoRolesEntity;

public interface MapeoRolesOutputPort {

    boolean estaVigente();

    boolean reservarActualizacion();

    void guardar(MapeoRolesEntity mapeo);
}
