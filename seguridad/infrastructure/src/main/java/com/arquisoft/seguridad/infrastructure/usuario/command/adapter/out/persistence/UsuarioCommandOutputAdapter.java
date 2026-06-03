package com.arquisoft.seguridad.infrastructure.usuario.command.adapter.out.persistence;

import com.arquisoft.seguridad.domain.usuario.aggregate.UsuarioAggregate;
import com.arquisoft.seguridad.domain.usuario.port.out.UsuarioOutputPort;
import com.arquisoft.seguridad.infrastructure.usuario.persistence.UsuarioJpaRepository;
import com.arquisoft.seguridad.infrastructure.usuario.persistence.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UsuarioCommandOutputAdapter implements UsuarioOutputPort {

    private final UsuarioJpaRepository usuarioJpaRepository;

    @Override
    public void save(UsuarioAggregate usuario) {
        usuarioJpaRepository.save(UsuarioMapper.toEntity(usuario));
        log.debug("Usuario persistido: id={} email={}", usuario.getId(), usuario.getEmail());
    }

    @Override
    public Optional<UsuarioAggregate> findById(UUID id) {
        return usuarioJpaRepository.findById(id).map(UsuarioMapper::toDomain);
    }
}
