package com.arquisoft.fichas.application.fichaperfil.query.readmodel;

import com.arquisoft.fichas.application.asesorficha.query.readmodel.AsesorFichaReadModel;
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
}
