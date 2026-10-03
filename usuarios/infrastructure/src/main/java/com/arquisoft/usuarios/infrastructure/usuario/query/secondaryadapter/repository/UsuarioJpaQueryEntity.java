package com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.repository;

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

// TODO HU252 (jurados): cuando exista la tabla jurado (la crea HU250), agregar al @Subselect
//  "CASE WHEN EXISTS (SELECT 1 FROM jurado j WHERE j.usuario_id = u.id AND j.eliminado_en IS NULL)
//  THEN TRUE ELSE FALSE END AS es_jurado" y "jurado" a @Synchronize.
// Siempre EXISTS, nunca JOIN: un JOIN duplica filas por rol y rompe la paginacion.
@Entity
@Immutable
@Subselect("""
        SELECT u.id            AS id,
               u.identificador AS identificador,
               u.nombre        AS nombre,
               u.email         AS email,
               u.contacto      AS contacto,
               u.estado_id     AS estado,
               (u.eliminado_en IS NULL) AS vigente,
               CASE WHEN EXISTS (SELECT 1 FROM estudiante e
                       WHERE e.usuario_id = u.id AND e.eliminado_en IS NULL)
                    THEN TRUE ELSE FALSE END AS es_estudiante,
               CASE WHEN EXISTS (SELECT 1 FROM asesor a
                       WHERE a.usuario_id = u.id AND a.eliminado_en IS NULL)
                    THEN TRUE ELSE FALSE END AS es_asesor,
               CASE WHEN EXISTS (SELECT 1 FROM asesor_ficha af
                       WHERE af.usuario_id = u.id AND af.eliminado_en IS NULL)
                    THEN TRUE ELSE FALSE END AS es_asesor_ficha,
               CASE WHEN EXISTS (SELECT 1 FROM coordinador c
                       WHERE c.usuario_id = u.id AND c.eliminado_en IS NULL)
                    THEN TRUE ELSE FALSE END AS es_coordinador,
               CASE WHEN EXISTS (SELECT 1 FROM representante_comite_curriculum r
                       WHERE r.usuario_id = u.id AND r.eliminado_en IS NULL)
                    THEN TRUE ELSE FALSE END AS es_representante_comite,
               CASE WHEN EXISTS (SELECT 1 FROM administrador ad
                       WHERE ad.usuario_id = u.id AND ad.eliminado_en IS NULL)
                    THEN TRUE ELSE FALSE END AS es_administrador,
               CASE WHEN EXISTS (SELECT 1 FROM bibliotecario b
                       WHERE b.usuario_id = u.id AND b.eliminado_en IS NULL)
                    THEN TRUE ELSE FALSE END AS es_bibliotecario
        FROM usuario u
        """)
@Synchronize({"usuario", "estudiante", "asesor", "asesor_ficha", "coordinador", "representante_comite_curriculum",
        "administrador", "bibliotecario"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioJpaQueryEntity {

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

    @Column(name = "vigente")
    private boolean vigente;

    @Column(name = "es_estudiante")
    private boolean esEstudiante;

    @Column(name = "es_asesor")
    private boolean esAsesor;

    @Column(name = "es_asesor_ficha")
    private boolean esAsesorFicha;

    @Column(name = "es_coordinador")
    private boolean esCoordinador;

    @Column(name = "es_representante_comite")
    private boolean esRepresentanteComite;

    @Column(name = "es_administrador")
    private boolean esAdministrador;

    @Column(name = "es_bibliotecario")
    private boolean esBibliotecario;

    // TODO HU252: @Column(name = "es_jurado") private boolean esJurado;
}
