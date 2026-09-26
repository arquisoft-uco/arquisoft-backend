package com.arquisoft.usuarios.infrastructure.asesorficha.query.secondaryadapter.repository;

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
        SELECT u.id            AS id,
               u.identificador AS identificador,
               u.nombre        AS nombre,
               u.email         AS email,
               u.contacto      AS contacto,
               u.estado_id     AS estado
        FROM asesor_ficha af
                 JOIN usuario u ON u.id = af.usuario_id
        WHERE af.eliminado_en IS NULL
        """)
@Synchronize({"asesor_ficha", "usuario"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsesorFichaVigenteJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "identificador")
    private String identificador;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "email")
    private String email;

    @Column(name = "contacto")
    private String contacto;

    @Column(name = "estado")
    private String estado;
}
