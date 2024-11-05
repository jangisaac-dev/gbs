package dev.oth.gbs.interfaces;

import dev.oth.gbs.domain.Ownable;
import dev.oth.gbs.repositories.OwnableRepository;

public interface OwnershipService {
    boolean isOwner(Long entityId, Long currentUserId);
}
