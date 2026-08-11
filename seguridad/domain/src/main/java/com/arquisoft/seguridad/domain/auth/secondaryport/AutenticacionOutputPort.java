package com.arquisoft.seguridad.domain.auth.secondaryport;

import com.arquisoft.seguridad.domain.auth.model.CredencialesSesion;

public interface AutenticacionOutputPort {

    CredencialesSesion autenticar(String correo, String contrasena);

    CredencialesSesion refrescar(String tokenRefresco);

    boolean validarTokenRefresco(String tokenRefresco);
}
