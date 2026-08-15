package com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.repository;

import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.usuarios.UsuarioKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
public class UsuarioCommandOutputAdapter implements UsuarioOutputPort {

    @Override
    public void save(UsuarioDomain usuario) {
        log.debug(Mensajes.obtener(UsuarioKey.LOG_MOCK_NO_PERSISTIDO),
                usuario.getId(), usuario.getEmail());
    }

    @Override
    public Optional<UsuarioDomain> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public boolean existePorEmail(String email) {
        log.debug(Mensajes.obtener(UsuarioKey.LOG_MOCK_VERIFICACION_OMITIDA), email);
        return false;
    }
}
