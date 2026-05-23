package com.arquisoft.fichas.application.fichaperfil.readmodel;

import com.arquisoft.fichas.application.asesorficha.readmodel.AsesorFichaReadModel;
import com.arquisoft.fichas.domain.model.FichaPerfilAggregate;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FichaPerfilReadModel {

    private UUID id;
    private String tituloProyecto;
    private AsesorFichaReadModel asesorFicha;

    public static FichaPerfilReadModel fromDomain(FichaPerfilAggregate ficha) {
        return FichaPerfilReadModel.builder()
                .id(ficha.getId())
                .tituloProyecto(ficha.getTituloProyecto())
                .asesorFicha(AsesorFichaReadModel.fromDomain(ficha.getAsesorFicha()))
                .build();
    }
}
