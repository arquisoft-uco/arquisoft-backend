package com.arquisoft.biblioteca.infrastructure.autordocumentacion.query.secondaryadapter.repository;

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
        SELECT ad.id               AS id,
               ad.documentacion_id AS documentacion_id,
               a.id                AS autor_id,
               a.identificador     AS identificador,
               a.nombre            AS nombre,
               a.email             AS email
        FROM autor_documentacion ad
                 JOIN autor a ON a.id = ad.autor_id
        """)
@Synchronize({"autor_documentacion", "autor"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutorDocumentacionJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "documentacion_id", columnDefinition = "uuid")
    private UUID documentacionId;

    @Column(name = "autor_id", columnDefinition = "uuid")
    private UUID autorId;

    @Column(name = "identificador")
    private String identificador;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "email")
    private String email;
}
