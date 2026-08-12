package com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "evaluacion_ficha_perfil")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluacionFichaPerfilEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "representante_comite_id", nullable = false)
    private UUID representanteComiteId;

    @Column(name = "ficha_perfil_id", nullable = false)
    private UUID fichaPerfilId;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;
}
