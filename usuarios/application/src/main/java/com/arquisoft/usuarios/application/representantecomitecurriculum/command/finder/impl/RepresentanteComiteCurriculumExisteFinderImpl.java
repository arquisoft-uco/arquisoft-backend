package com.arquisoft.usuarios.application.representantecomitecurriculum.command.finder.impl;

import com.arquisoft.usuarios.application.representantecomitecurriculum.command.finder.RepresentanteComiteCurriculumExisteFinder;
import com.arquisoft.usuarios.application.representantecomitecurriculum.command.secondaryport.RepresentanteComiteCurriculumOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RepresentanteComiteCurriculumExisteFinderImpl implements RepresentanteComiteCurriculumExisteFinder {

    private final RepresentanteComiteCurriculumOutputPort outputPort;

    @Override
    public Boolean obtener(UUID usuario) {
        return outputPort.existePorUsuario(usuario);
    }
}
