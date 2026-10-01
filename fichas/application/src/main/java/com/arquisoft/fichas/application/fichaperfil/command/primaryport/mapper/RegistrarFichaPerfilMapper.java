package com.arquisoft.fichas.application.fichaperfil.command.primaryport.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.mapper.AsignarEstadoInicialFichaPerfilMapper;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.mapper.AsignarEstudiantesFichaPerfilMapper;
import com.arquisoft.fichas.application.fichaperfil.command.primaryport.model.RegistrarFichaPerfilCommand;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.RegistroFichaPerfilDomain;

public final class RegistrarFichaPerfilMapper {

    private RegistrarFichaPerfilMapper() {}

    public static RegistroFichaPerfilDomain toDomain(RegistrarFichaPerfilCommand command) {
        var ficha = FichaPerfilDomain.crear(command.tituloProyecto(), command.asesorFicha());

        return RegistroFichaPerfilDomain.crear(
                ficha,
                AsignarEstadoInicialFichaPerfilMapper.toDomain(ficha.getId()),
                AsignarEstudiantesFichaPerfilMapper.toDomain(ficha.getId(), command.estudiantes()));
    }
}
