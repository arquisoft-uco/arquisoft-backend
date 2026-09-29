package com.arquisoft.usuarios.application.representantecomite.command.finder.impl;

import com.arquisoft.usuarios.application.representantecomite.command.finder.RepresentanteComitePorUsuarioFinder;
import com.arquisoft.usuarios.application.representantecomite.command.secondaryport.RepresentanteComiteOutputPort;
import com.arquisoft.usuarios.application.representantecomite.command.secondaryport.mapper.RepresentanteComiteMapper;
import com.arquisoft.usuarios.domain.representantecomite.RepresentanteComiteDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RepresentanteComitePorUsuarioFinderImpl implements RepresentanteComitePorUsuarioFinder {

    private final RepresentanteComiteOutputPort representanteComiteOutputPort;

    @Override
    public RepresentanteComiteDomain obtener(UUID usuario) {
        return representanteComiteOutputPort.obtenerPorUsuario(usuario).map(RepresentanteComiteMapper::toDomain)
                .orElse(RepresentanteComiteDomain.VACIO);
    }
}
