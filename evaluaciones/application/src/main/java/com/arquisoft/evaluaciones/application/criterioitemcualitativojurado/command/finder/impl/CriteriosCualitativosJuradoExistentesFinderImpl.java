package com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.command.finder.CriteriosCualitativosJuradoExistentesFinder;
import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.command.secondaryport.CriterioItemCualitativoJuradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CriteriosCualitativosJuradoExistentesFinderImpl
        implements CriteriosCualitativosJuradoExistentesFinder {

    private final CriterioItemCualitativoJuradoOutputPort criterioItemCualitativoJuradoOutputPort;

    @Override
    public Set<UUID> obtener(Set<UUID> ids) {
        return criterioItemCualitativoJuradoOutputPort.consultarIdsExistentes(ids);
    }
}
