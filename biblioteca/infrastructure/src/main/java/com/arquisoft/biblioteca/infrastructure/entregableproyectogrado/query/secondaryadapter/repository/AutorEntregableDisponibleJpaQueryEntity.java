package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.secondaryadapter.repository;

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
        SELECT epg.id                AS id,
               epg.proyecto_grado_id AS proyecto_grado_id,
               a.id                  AS autor_id,
               a.identificador       AS identificador,
               a.nombre              AS nombre
        FROM estudiante_proyecto_grado epg
                 JOIN autor a ON a.id = epg.autor_id
        WHERE epg.eliminado_en IS NULL
        """)
@Synchronize({"estudiante_proyecto_grado", "autor"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutorEntregableDisponibleJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "proyecto_grado_id", columnDefinition = "uuid")
    private UUID proyectoGradoId;

    @Column(name = "autor_id", columnDefinition = "uuid")
    private UUID autorId;

    @Column(name = "identificador")
    private String identificador;

    @Column(name = "nombre")
    private String nombre;
}
