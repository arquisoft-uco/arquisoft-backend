package com.arquisoft.seguridad.infrastructure.usuario.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, UUID> {
}
