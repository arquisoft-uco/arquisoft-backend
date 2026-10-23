package com.arquisoft.seguridad.application.autenticacion.command.secondaryport;

import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.model.CredencialesProveedor;

public interface AutenticacionOutputPort {

    CredencialesProveedor autenticar(String correo, String contrasena);

    CredencialesProveedor refrescar(String tokenRefresco);
}
