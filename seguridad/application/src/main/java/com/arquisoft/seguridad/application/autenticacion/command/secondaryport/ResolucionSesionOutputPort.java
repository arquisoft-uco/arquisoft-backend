package com.arquisoft.seguridad.application.autenticacion.command.secondaryport;

import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.model.ResolucionSesion;

public interface ResolucionSesionOutputPort {

    ResolucionSesion resolver(String credencial);
}
