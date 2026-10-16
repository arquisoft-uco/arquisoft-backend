package com.arquisoft.mapas_ruta.infrastructure.proyectogrado.command.secondaryadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "proyecto_grado")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoGradoJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "estado_proyecto_grado_id", nullable = false, length = 60)
    private String estadoProyectoGradoId;

    @Column(name = "coordinador_id", nullable = false, columnDefinition = "uuid")
    private UUID coordinadorId;

    @Column(name = "ficha_perfil_id", nullable = false, columnDefinition = "uuid")
    private UUID fichaPerfilId;

    @Column(name = "titulo_proyecto", nullable = false, length = 100)
    private String tituloProyecto;
}
