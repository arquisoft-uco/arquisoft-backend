package com.arquisoft.usuarios.infrastructure.usuario.command.adapter.out.persistence;

import com.arquisoft.usuarios.domain.usuario.aggregate.UsuarioAggregate;
import com.arquisoft.usuarios.domain.usuario.port.out.UsuarioOutputPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
public class UsuarioCommandOutputAdapter implements UsuarioOutputPort {

    @Override
    public void save(UsuarioAggregate usuario) {
        log.debug("Mock — usuario no persistido: id={} email={}", usuario.getId(), usuario.getEmail());
    }

    @Override
    public Optional<UsuarioAggregate> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public boolean existePorEmail(String email) {
        log.debug("Mock — verificacion de email omitida: email={}", email);
        return false;
    }
}
