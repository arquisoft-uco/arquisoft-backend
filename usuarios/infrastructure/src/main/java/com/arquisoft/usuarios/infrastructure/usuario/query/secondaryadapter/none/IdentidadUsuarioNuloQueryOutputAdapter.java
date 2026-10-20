package com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.none;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.usuarios.ProveedorIdentidadKey;
import com.arquisoft.usuarios.application.usuario.query.criteria.IdentidadUsuarioCriteria;
import com.arquisoft.usuarios.application.usuario.query.readmodel.IdentidadUsuarioReadModel;
import com.arquisoft.usuarios.application.usuario.query.secondaryport.IdentidadUsuarioQueryOutputPort;
import com.arquisoft.usuarios.infrastructure.usuario.exception.ProveedorIdentidadUsuarioNoDisponibleException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "security.identity-provisioning.provider", havingValue = "none")
public class IdentidadUsuarioNuloQueryOutputAdapter implements IdentidadUsuarioQueryOutputPort {

    @Override
    public IdentidadUsuarioReadModel consultar(IdentidadUsuarioCriteria criteria) {
        throw new ProveedorIdentidadUsuarioNoDisponibleException(
                Mensajes.obtener(ProveedorIdentidadKey.ERROR_NO_DISPONIBLE));
    }
}
