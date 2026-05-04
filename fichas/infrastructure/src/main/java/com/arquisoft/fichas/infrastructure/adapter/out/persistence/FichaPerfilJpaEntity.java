package com.arquisoft.fichas.infrastructure.adapter.out.persistence;

import com.arquisoft.fichas.domain.model.FichaPerfil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "ficha_perfil")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FichaPerfilJpaEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "titulo_proyecto", nullable = false, length = 100)
    private String tituloProyecto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asesor_ficha_id", nullable = false)
    private AsesorFichaJpaEntity asesorFicha;

    public FichaPerfil toDomain() {
        return FichaPerfil.rebuild(id, tituloProyecto, asesorFicha.toDomain());
    }
}
