package com.arquisoft.mapas_ruta.infrastructure.tarea.query.secondaryadapter.repository;

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

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Immutable
@Subselect("""
        SELECT t.id             AS id,
               t.descripcion    AS descripcion,
               t.mapa_ruta_id   AS mapa_ruta_id,
               m.fecha_inicio   AS fecha_inicio_mapa_ruta,
               m.fecha_fin      AS fecha_fin_mapa_ruta,
               t.estado_item_id AS estado_item_id,
               e.nombre         AS nombre_estado_item,
               t.asesor_id      AS asesor_id,
               a.nombre         AS nombre_asesor,
               a.email          AS email_asesor
        FROM tarea t
                 JOIN mapa_ruta m ON m.id = t.mapa_ruta_id
                 JOIN estado_item e ON e.id = t.estado_item_id
                 JOIN asesor a ON a.id = t.asesor_id
        """)
@Synchronize({"tarea", "mapa_ruta", "estado_item", "asesor"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TareaJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "mapa_ruta_id", columnDefinition = "uuid")
    private UUID mapaRutaId;

    @Column(name = "fecha_inicio_mapa_ruta")
    private LocalDate fechaInicioMapaRuta;

    @Column(name = "fecha_fin_mapa_ruta")
    private LocalDate fechaFinMapaRuta;

    @Column(name = "estado_item_id")
    private String estadoItemId;

    @Column(name = "nombre_estado_item")
    private String nombreEstadoItem;

    @Column(name = "asesor_id", columnDefinition = "uuid")
    private UUID asesorId;

    @Column(name = "nombre_asesor")
    private String nombreAsesor;

    @Column(name = "email_asesor")
    private String emailAsesor;
}
