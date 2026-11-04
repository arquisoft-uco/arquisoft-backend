package com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.command.finder.CriteriosCualitativosAsesorExistentesFinder;
import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.command.secondaryport.CriterioItemCualitativoAsesorOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CriteriosCualitativosAsesorExistentesFinderImpl implements CriteriosCualitativosAsesorExistentesFinder {

    private final CriterioItemCualitativoAsesorOutputPort criterioItemCualitativoAsesorOutputPort;

    @Override
    public Set<UUID> obtener(Set<UUID> ids) {
        return criterioItemCualitativoAsesorOutputPort.consultarIdsExistentes(ids);
    }
}
