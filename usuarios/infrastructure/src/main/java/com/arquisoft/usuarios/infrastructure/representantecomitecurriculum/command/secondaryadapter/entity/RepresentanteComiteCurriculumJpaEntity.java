package com.arquisoft.usuarios.infrastructure.representantecomitecurriculum.command.secondaryadapter.entity;

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
@Table(name = "representante_comite_curriculum")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepresentanteComiteCurriculumJpaEntity {

    @Id
    @Column(name = "usuario_id")
    private UUID usuario;
}
