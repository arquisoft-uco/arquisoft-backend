package com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.repository;

import com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface UsuarioCommandRepository extends JpaRepository<UsuarioJpaEntity, UUID> {

    boolean existsByIdentificador(String identificador);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByContacto(String contacto);

    boolean existsByIdentificadorAndIdNot(String identificador, UUID usuario);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, UUID usuario);

    boolean existsByContactoAndIdNot(String contacto, UUID usuario);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE UsuarioJpaEntity u SET u.eliminadoEn = :eliminadoEn WHERE u.id = :usuario")
    int eliminarLogica(@Param("usuario") UUID usuario, @Param("eliminadoEn") Instant eliminadoEn);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE UsuarioJpaEntity u SET u.estadoId = :estado, u.eliminadoEn = :eliminadoEn WHERE u.id = :usuario")
    int cambiarEstado(@Param("usuario") UUID usuario, @Param("estado") String estado,
                      @Param("eliminadoEn") Instant eliminadoEn);
}
