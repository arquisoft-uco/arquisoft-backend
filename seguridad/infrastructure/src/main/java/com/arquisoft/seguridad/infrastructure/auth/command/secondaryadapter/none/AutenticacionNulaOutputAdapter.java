package com.arquisoft.seguridad.infrastructure.auth.command.secondaryadapter.none;

import com.arquisoft.seguridad.application.auth.command.secondaryport.AutenticacionOutputPort;
import com.arquisoft.seguridad.application.auth.command.secondaryport.model.CredencialesProveedor;
import com.arquisoft.seguridad.infrastructure.auth.exception.ProveedorIdentidadNoDisponibleException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.seguridad.IniciarSesionKey;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "security.direct-login.provider", havingValue = "none")
public class AutenticacionNulaOutputAdapter implements AutenticacionOutputPort {

    @Override
    public CredencialesProveedor autenticar(String correo, String contrasena) {
        throw noDisponible();
    }

    @Override
    public CredencialesProveedor refrescar(String tokenRefresco) {
        throw noDisponible();
    }

    private static ProveedorIdentidadNoDisponibleException noDisponible() {
        return new ProveedorIdentidadNoDisponibleException(
                Mensajes.obtener(IniciarSesionKey.ERROR_SERVICIO_NO_DISPONIBLE), null);
    }
}
