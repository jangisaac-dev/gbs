package dev.oth.gbs.interfaces.impl;

import dev.oth.gbs.interfaces.OwnershipService;
import dev.oth.gbs.repositories.OwnableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OwnershipServiceImpl implements OwnershipService {

    @Autowired
    private ApplicationContext applicationContext;

    @SuppressWarnings("unchecked")
    @Override
    public boolean isOwner(Long entityId, Long currentUserId) {
        for (String beanName : applicationContext.getBeanNamesForType(OwnableRepository.class)) {
            OwnableRepository<?, Long> repository = (OwnableRepository<?, Long>) applicationContext.getBean(beanName);

            Optional<Long> ownerIdOptional = repository.findOwnerIdById(entityId);
            if (ownerIdOptional.isPresent()) {
                Long ownerId = ownerIdOptional.get();
                return ownerId.equals(currentUserId);
            }
        }
        return false;
    }
}