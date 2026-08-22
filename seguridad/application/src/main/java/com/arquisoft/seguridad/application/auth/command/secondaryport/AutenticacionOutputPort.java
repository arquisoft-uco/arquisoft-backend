package com.arquisoft.seguridad.application.auth.command.secondaryport;

import com.arquisoft.seguridad.application.auth.command.secondaryport.model.CredencialesProveedor;

public interface AutenticacionOutputPort {

    CredencialesProveedor autenticar(String correo, String contrasena);

    CredencialesProveedor refrescar(String tokenRefresco);
}
