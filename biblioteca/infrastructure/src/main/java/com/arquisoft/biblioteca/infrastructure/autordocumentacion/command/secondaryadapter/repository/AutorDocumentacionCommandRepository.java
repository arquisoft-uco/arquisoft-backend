package com.arquisoft.biblioteca.infrastructure.autordocumentacion.command.secondaryadapter.repository;

import com.arquisoft.biblioteca.infrastructure.autordocumentacion.command.secondaryadapter.entity.AutorDocumentacionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AutorDocumentacionCommandRepository extends JpaRepository<AutorDocumentacionJpaEntity, UUID> {
}
