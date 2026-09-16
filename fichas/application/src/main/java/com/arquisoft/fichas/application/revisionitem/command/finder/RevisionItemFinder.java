package com.arquisoft.fichas.application.revisionitem.command.finder;

import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.RevisionItemEntity;
import com.arquisoft.shared.finder.Finder;

import java.util.Optional;
import java.util.UUID;

public interface RevisionItemFinder extends Finder<UUID, Optional<RevisionItemEntity>> {
}
