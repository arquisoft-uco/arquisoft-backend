package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

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
        SELECT f.id              AS id,
               f.titulo_proyecto AS titulo_proyecto,
               a.id              AS asesor_id,
               a.identificador   AS asesor_identificador,
               a.nombre          AS asesor_nombre,
               a.email           AS asesor_email
        FROM ficha_perfil f
                 JOIN asesor_ficha a ON a.id = f.asesor_ficha_id
        """)
@Synchronize({"ficha_perfil", "asesor_ficha"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FichaPerfilJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "titulo_proyecto")
    private String tituloProyecto;

    @Column(name = "asesor_id", columnDefinition = "uuid")
    private UUID asesorId;

    @Column(name = "asesor_identificador")
    private String asesorIdentificador;

    @Column(name = "asesor_nombre")
    private String asesorNombre;

    @Column(name = "asesor_email")
    private String asesorEmail;
}
