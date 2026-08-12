package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
    name = "estudiante_ficha_perfil",
    uniqueConstraints = @UniqueConstraint(columnNames = {"ficha_perfil_id", "estudiante_id"})
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstudianteFichaPerfilEntity {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "ficha_perfil_id", nullable = false, columnDefinition = "UUID")
    private UUID fichaPerfilId;

    @Column(name = "estudiante_id", nullable = false, columnDefinition = "UUID")
    private UUID estudianteId;
}
