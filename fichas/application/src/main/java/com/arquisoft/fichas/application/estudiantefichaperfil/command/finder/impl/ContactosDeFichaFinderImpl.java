package com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.ContactosDeFichaFinder;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.entity.ContactoEstudianteEntity;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ContactoEstudiante;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ContactosDeFichaFinderImpl implements ContactosDeFichaFinder {

    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @Override
    public List<ContactoEstudiante> obtener(UUID fichaPerfilId) {
        return estudianteFichaPerfilOutputPort.obtenerContactosDeFicha(fichaPerfilId).stream()
                .map(ContactosDeFichaFinderImpl::aDominio)
                .toList();
    }

    private static ContactoEstudiante aDominio(ContactoEstudianteEntity entity) {
        return new ContactoEstudiante(entity.nombre(), entity.email());
    }
}
