package com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "estado_ficha_perfil")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoFichaPerfilJpaEntity {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(name = "ficha_perfil_id", nullable = false)
    private UUID fichaPerfilId;

    @Column(name = "estado_ficha_id", nullable = false)
    private UUID estadoFichaId;

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion;
}
