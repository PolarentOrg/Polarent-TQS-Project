package com.tqs.polarent.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void shouldHaveOnlyAdminAndUserRoles() {
        Role[] roles = Role.values();
        assertEquals(2, roles.length);
        assertArrayEquals(new Role[]{Role.ADMIN, Role.USER}, roles);
    }

    @Test
    void shouldReturnCorrectRoleFromString() {
        assertEquals(Role.ADMIN, Role.valueOf("ADMIN"));
        assertEquals(Role.USER, Role.valueOf("USER"));
    }

    @Test
    void shouldThrowExceptionForInvalidRole() {
        assertThrows(IllegalArgumentException.class, () -> Role.valueOf("OWNER"));
        assertThrows(IllegalArgumentException.class, () -> Role.valueOf("RENTER"));
    }
}
