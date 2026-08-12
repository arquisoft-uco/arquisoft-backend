package com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence;

import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "estado_ficha_perfil")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoFichaPerfilEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "ficha_perfil_id", nullable = false)
    private UUID fichaPerfilId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_ficha_id", nullable = false)
    private EstadoFichaEntity estadoFicha;

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion;
}
