package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.secondaryadapter.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.util.UUID;

@Entity
@Immutable
@Subselect("""
        SELECT efp.id              AS id,
               efp.ficha_perfil_id AS ficha_perfil_id,
               efp.estudiante_id   AS estudiante_id,
               e.nombre            AS nombre,
               e.email             AS email
        FROM estudiante_ficha_perfil efp
                 JOIN estudiante e ON e.id = efp.estudiante_id
        """)
@Synchronize({"estudiante_ficha_perfil", "estudiante"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstudianteFichaPerfilJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "ficha_perfil_id", columnDefinition = "uuid")
    private UUID fichaPerfilId;

    @Column(name = "estudiante_id", columnDefinition = "uuid")
    private UUID estudianteId;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "email")
    private String email;
}
