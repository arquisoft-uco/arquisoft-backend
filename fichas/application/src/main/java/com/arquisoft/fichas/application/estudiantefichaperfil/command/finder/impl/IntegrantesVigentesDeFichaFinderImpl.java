package com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.IntegrantesVigentesDeFichaFinder;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.entity.IntegranteFichaEntity;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ContactoEstudiante;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.IntegranteFicha;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IntegrantesVigentesDeFichaFinderImpl implements IntegrantesVigentesDeFichaFinder {

    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @Override
    public List<IntegranteFicha> obtener(UUID fichaPerfil) {
        return estudianteFichaPerfilOutputPort.obtenerIntegrantesVigentesDeFicha(fichaPerfil).stream()
                .map(IntegrantesVigentesDeFichaFinderImpl::aDominio)
                .toList();
    }

    private static IntegranteFicha aDominio(IntegranteFichaEntity entity) {
        return new IntegranteFicha(entity.estudiante(), new ContactoEstudiante(entity.nombre(), entity.email()));
    }
}
