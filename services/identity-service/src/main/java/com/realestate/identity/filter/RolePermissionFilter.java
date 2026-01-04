package com.realestate.identity.filter;

import com.realestate.identity.annotation.RequiresPermission;
import com.realestate.identity.annotation.RequiresRole;
import com.realestate.identity.entity.Permission;
import com.realestate.identity.entity.Role;
import com.realestate.identity.entity.User;
import com.realestate.identity.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RolePermissionFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final RequestMappingHandlerMapping handlerMapping;

    public RolePermissionFilter(
            UserRepository userRepository,
            @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping) {
        this.userRepository = userRepository;
        this.handlerMapping = handlerMapping;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // If not authenticated, let Spring Security handle it
        if (authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Get the handler method for the current request
            HandlerMethod handlerMethod = getHandlerMethod(request);
            if (handlerMethod == null) {
                // No handler method found, continue filter chain
                filterChain.doFilter(request, response);
                return;
            }

            // Check for @RequiresRole annotation on method or class
            RequiresRole requiresRole = AnnotationUtils.findAnnotation(
                    handlerMethod.getMethod(), RequiresRole.class);
            if (requiresRole == null) {
                // Check class level annotation
                requiresRole = AnnotationUtils.findAnnotation(
                        handlerMethod.getBeanType(), RequiresRole.class);
            }

            // Only check if annotation is present
            if (requiresRole != null && requiresRole.value().length > 0) {
                if (!hasRequiredRole(authentication, requiresRole.value())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Insufficient role permissions\",\"message\":\"Required roles: " + String.join(", ", requiresRole.value()) + "\"}");
                    return;
                }
            }

            // Check for @RequiresPermission annotation on method or class
            RequiresPermission requiresPermission = AnnotationUtils.findAnnotation(
                    handlerMethod.getMethod(), RequiresPermission.class);
            if (requiresPermission == null) {
                // Check class level annotation
                requiresPermission = AnnotationUtils.findAnnotation(
                        handlerMethod.getBeanType(), RequiresPermission.class);
            }

            // Only check if annotation is present
            if (requiresPermission != null) {
                if (!hasRequiredPermission(authentication, requiresPermission.resource(), requiresPermission.action())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Insufficient permissions\",\"message\":\"Required permission: " + requiresPermission.resource() + ":" + requiresPermission.action() + "\"}");
                    return;
                }
            }

        } catch (Exception e) {
            logger.error("Error checking roles/permissions", e);
            // On error, continue filter chain to avoid blocking legitimate requests
        }

        filterChain.doFilter(request, response);
    }

    private HandlerMethod getHandlerMethod(HttpServletRequest request) {
        try {
            HandlerExecutionChain handlerExecutionChain = handlerMapping.getHandler(request);
            if (handlerExecutionChain != null && handlerExecutionChain.getHandler() instanceof HandlerMethod) {
                return (HandlerMethod) handlerExecutionChain.getHandler();
            }
            return null;
        } catch (Exception e) {
            logger.debug("Could not get handler method", e);
            return null;
        }
    }

    private boolean hasRequiredRole(Authentication authentication, String[] requiredRoles) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Set<String> userRoles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(auth -> auth.startsWith("ROLE_") ? auth.substring(5) : auth)
                .collect(Collectors.toSet());

        // SUPER_ADMIN bypass all role checks
        if (userRoles.contains("SUPER_ADMIN")) {
            return true;
        }

        for (String requiredRole : requiredRoles) {
            if (userRoles.contains(requiredRole)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasRequiredPermission(Authentication authentication, String resource, String action) {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).orElse(null);
        if (user == null) {
            return false;
        }

        Set<Role> roles = user.getRoles();
        
        // Check if user has SUPER_ADMIN role - bypass all permission checks
        for (Role role : roles) {
            if ("SUPER_ADMIN".equals(role.getName())) {
                return true;
            }
        }

        // Check for FULL_ACCESS permission (resource: "*", action: "*")
        for (Role role : roles) {
            Set<Permission> permissions = role.getPermissions();
            for (Permission permission : permissions) {
                // Check for full access permission
                if ("*".equals(permission.getResource()) && "*".equals(permission.getAction())) {
                    return true;
                }
                // Check for specific permission
                if (permission.getResource().equals(resource) && 
                    permission.getAction().equals(action)) {
                    return true;
                }
            }
        }
        return false;
    }
}

