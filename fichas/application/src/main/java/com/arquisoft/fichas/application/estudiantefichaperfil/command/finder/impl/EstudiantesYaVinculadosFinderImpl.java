package com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.EstudiantesYaVinculadosFinder;
import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.EstudianteFichaPerfilOutputPort;
import com.arquisoft.shared.util.UtilColeccion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudiantesYaVinculadosFinderImpl implements EstudiantesYaVinculadosFinder {

    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @Override
    public List<UUID> obtener(List<EstudianteFichaPerfilDomain> relaciones) {
        if (UtilColeccion.esVaciaONula(relaciones)) {
            return List.of();
        }
        return relaciones.stream()
                .filter(relacion -> estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(
                        relacion.getFichaPerfilId(), relacion.getEstudianteId()))
                .map(EstudianteFichaPerfilDomain::getEstudianteId)
                .toList();
    }
}
