package com.arquisoft.seguridad.application.mapeoroles.command.finder.impl;

import com.arquisoft.seguridad.application.mapeoroles.command.finder.MapeoRolesVigenteFinder;
import com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.MapeoRolesOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MapeoRolesVigenteFinderImpl implements MapeoRolesVigenteFinder {

    private final MapeoRolesOutputPort mapeoRolesOutputPort;

    @Override
    public Boolean obtener() {
        return mapeoRolesOutputPort.estaVigente();
    }
}
