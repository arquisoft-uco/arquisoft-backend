package com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.repository;

import com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UsuarioCommandRepository extends JpaRepository<UsuarioJpaEntity, UUID> {

    boolean existsByIdentificador(String identificador);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByContacto(String contacto);
}
