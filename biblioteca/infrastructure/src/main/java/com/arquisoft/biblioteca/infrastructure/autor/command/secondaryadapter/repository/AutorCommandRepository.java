package com.arquisoft.biblioteca.infrastructure.autor.command.secondaryadapter.repository;

import com.arquisoft.biblioteca.infrastructure.autor.command.secondaryadapter.entity.AutorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface AutorCommandRepository extends JpaRepository<AutorJpaEntity, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE AutorJpaEntity b SET b.eliminadoEn = :ocurridoEn, b.ocurridoEn = :ocurridoEn WHERE b.id = :id")
    int eliminarLogica(@Param("id") UUID id, @Param("ocurridoEn") Instant ocurridoEn);

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE AutorJpaEntity b
            SET b.identificador = :identificador, b.nombre = :nombre, b.email = :email,
                b.ocurridoEn = :ocurridoEn, b.eliminadoEn = NULL
            WHERE b.id = :id
            """)
    int reactivar(@Param("id") UUID id, @Param("identificador") String identificador,
                  @Param("nombre") String nombre, @Param("email") String email,
                  @Param("ocurridoEn") Instant ocurridoEn);
}
