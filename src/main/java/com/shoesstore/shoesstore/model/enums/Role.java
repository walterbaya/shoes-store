package com.shoesstore.shoesstore.model.enums;

import com.google.common.collect.Sets;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.shoesstore.shoesstore.model.enums.ApplicationUserPermission.*;

public enum Role {
    USER(Sets.newHashSet(PRODUCT_READ, SALE_READ)),
    SELLER(Sets.newHashSet(PRODUCT_READ, PRODUCT_WRITE, SALE_READ, SALE_WRITE)),
    ADMIN(Sets.newHashSet(PRODUCT_READ, PRODUCT_WRITE, SALE_READ, SALE_WRITE, SALE_DELETE, USER_READ, USER_WRITE, SUPPLIER_READ, SUPPLIER_WRITE, PURCHASE_ORDER_READ, PURCHASE_ORDER_WRITE)),
    STOCK_MANAGER(Sets.newHashSet(PRODUCT_READ, PRODUCT_WRITE, SALE_READ, SALE_WRITE));

    private final Set<ApplicationUserPermission> permissions;

    Role(Set<ApplicationUserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<ApplicationUserPermission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        Set<SimpleGrantedAuthority> authorities = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name())); // Añadir el rol como autoridad
        return authorities;
    }
}
