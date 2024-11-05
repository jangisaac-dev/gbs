package dev.oth.gbs.filter;

import dev.oth.gbs.common.SelfRoleCheckException;
import dev.oth.gbs.domain.TokenDetailModel;
import dev.oth.gbs.enums.UserRole;
import dev.oth.gbs.interfaces.OwnershipService;
import dev.oth.gbs.providers.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
public class RoleCheckAspect {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private OwnershipService ownershipService;

    @Before("@annotation(requiredRole)")
    public void checkRole(JoinPoint joinPoint, RequiredRole requiredRole) throws SelfRoleCheckException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new SelfRoleCheckException();
        }

        HttpServletRequest request = attributes.getRequest();
        TokenDetailModel tokenData = jwtTokenUtil.getTokenDataFromRequest(request);
        if (tokenData == null) {
            throw new SelfRoleCheckException();
        }

        Long currentUserId = tokenData.getId();
        UserRole currentUserRole = tokenData.getRole();

        boolean hasRequiredRole = Arrays.stream(requiredRole.value())
                .anyMatch(role -> {
                    if (role == UserRole.ROLE_SELF) {
                        return isResourceOwner(joinPoint, currentUserId);
                    }
                    return currentUserRole == role;
                });

        if (!hasRequiredRole) {
            throw new SelfRoleCheckException();
        }
    }

    private boolean isResourceOwner(JoinPoint joinPoint, Long currentUserId) {
        Long entityId = null;

        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof Long) { // ID로 가정
                entityId = (Long) arg;
                break;
            }
        }

        if (entityId != null) {
            return ownershipService.isOwner(entityId, currentUserId);
        }
        return false;
    }
}
