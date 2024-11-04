package dev.oth.gbs.filter;

import dev.oth.gbs.common.Error;
import dev.oth.gbs.common.Response;
import dev.oth.gbs.enums.UserRole;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

// @Aspect: 이 클래스가 AOP(Aspect-Oriented Programming) 역할을 수행한다는 것을 나타냅니다.
// 이 클래스는 특정 메서드 실행 전후에 권한 검사를 수행하는 역할을 합니다.
@Aspect

// @Component: 이 클래스가 Spring 컨텍스트에서 관리되는 빈(bean)임을 나타냅니다.
// Spring AOP를 사용하기 위해서는 이 클래스가 Spring의 관리 하에 있어야 합니다.
@Component
public class RoleCheckAspect {

    // @Around: 메서드 실행 전후에 특정 로직을 수행하도록 설정하는 어노테이션입니다.
    // value: 이 어드바이스가 적용될 메서드 대상에 대한 포인트컷을 지정합니다.
    // "execution(* *(..)) && @within(classRole) || @annotation(methodRole)"는
    // 메서드 또는 클래스에 @RequiredRole이 적용된 경우 이 어드바이스가 실행되도록 합니다.
    // argNames: 어드바이스 메서드에 전달되는 매개변수 이름을 지정합니다.
    @Around(value = "@within(classRole) || @annotation(methodRole)", argNames = "joinPoint,classRole,methodRole")

//    @Around(value = "execution(* *(..)) && @within(classRole) || @annotation(methodRole)", argNames = "joinPoint,classRole,methodRole")
    public Object checkRoleAccess(ProceedingJoinPoint joinPoint, RequiredRole classRole, RequiredRole methodRole) throws Throwable {

        System.out.println("Role Check Aspect");
        // 우선순위 설정: 메서드에 @RequiredRole이 있을 경우 이를 우선 적용하고,
        // 없다면 클래스에 적용된 @RequiredRole을 사용합니다.
        RequiredRole requiredRole = methodRole != null ? methodRole : classRole;

        // `ROLE_PUBLIC` 확인: 별도의 인증이나 권한 없이 접근을 허용
        if (requiredRole.value().length == 1 && requiredRole.value()[0] == UserRole.ROLE_PUBLIC) {
            return joinPoint.proceed();  // `ROLE_PUBLIC`이 설정된 경우 접근 허용
        }

        // 인증 정보 가져오기: 현재 사용자의 인증 정보를 SecurityContext에서 가져옵니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증되지 않은 사용자는 접근이 거부되도록 설정합니다.
        if (authentication == null || !authentication.isAuthenticated()) {
            return Response.error(Error.UNAUTHORIZED_NULL).toResponseEntity();
//            throw new AccessDeniedException("인증되지 않은 사용자입니다.");
        }

        // ROLE_ANY 확인: 사용자가 ROLE_ANY를 가지고 있다면 접근을 허용합니다.
        boolean hasAnyRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals(UserRole.ROLE_ANY.name()));

        // ROLE_ANY 권한이 있으면, 즉시 접근을 허용하고 메서드를 실행합니다.
        if (hasAnyRole) {
            return joinPoint.proceed();
        }

        // 요구되는 역할 검사: @RequiredRole에 지정된 역할 중 하나라도 사용자가 가지고 있는지 검사합니다.
        boolean hasRequiredRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> {
                    for (UserRole userRole : requiredRole.value()) {
                        if (role.equals(userRole.name())) {
                            return true;
                        }
                    }
                    return false;
                });

        // 요구되는 역할을 갖고 있지 않은 경우, 접근 거부 예외를 발생시킵니다.
        if (!hasRequiredRole) {
            return Response.error(Error.FORBIDDEN).toResponseEntity();
//            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        // 모든 권한 조건을 충족한 경우에만 원래 메서드를 실행합니다.
        return joinPoint.proceed();
    }
}