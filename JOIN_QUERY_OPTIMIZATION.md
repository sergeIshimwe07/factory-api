# User Roles & Permissions JOIN Query Optimization

## 📋 Overview

Implemented optimized JOIN queries in `UserRepository` to fetch users with their roles and permissions as nested objects in a single database query. This eliminates the need for separate queries and improves performance.

---

## 🔄 Data Relationships

```
User (1) ──── (M) UserRole (M) ──── (1) RoleGroup
                                         │
                                         └──── (M) RolePermission (M) ──── (1) Feature
```

### Entity Structure:
- **User**: Contains user credentials and basic info
- **UserRole**: Junction table linking users to roles (many-to-many)
- **RoleGroup**: Defines roles with permissions
- **RolePermission**: Maps features to roles with specific permissions (READ, EDIT, DELETE)
- **Feature**: Individual features/modules in the system

---

## ✅ Implemented JOIN Queries

### 1. Find User by ID with Roles and Permissions
```java
@Query("SELECT DISTINCT u FROM User u " +
       "LEFT JOIN FETCH u.userRoles ur " +
       "LEFT JOIN FETCH ur.role rg " +
       "LEFT JOIN FETCH rg.permissions rp " +
       "LEFT JOIN FETCH rp.feature f " +
       "WHERE u.id = :userId")
Optional<User> findByIdWithRolesAndPermissions(Long userId);
```

**Usage**: When you need to fetch a user by their database ID
```java
User user = userRepository.findByIdWithRolesAndPermissions(123L).orElseThrow();
```

### 2. Find User by Email with Roles and Permissions
```java
@Query("SELECT DISTINCT u FROM User u " +
       "LEFT JOIN FETCH u.userRoles ur " +
       "LEFT JOIN FETCH ur.role rg " +
       "LEFT JOIN FETCH rg.permissions rp " +
       "LEFT JOIN FETCH rp.feature f " +
       "WHERE u.email = :email")
Optional<User> findByEmailWithRolesAndPermissions(String email);
```

**Usage**: Used in login and authentication
```java
User user = userRepository.findByEmailWithRolesAndPermissions("admin@factory.com").orElseThrow();
```

### 3. Find User by UserId with Roles and Permissions
```java
@Query("SELECT DISTINCT u FROM User u " +
       "LEFT JOIN FETCH u.userRoles ur " +
       "LEFT JOIN FETCH ur.role rg " +
       "LEFT JOIN FETCH rg.permissions rp " +
       "LEFT JOIN FETCH rp.feature f " +
       "WHERE u.userId = :userId")
Optional<User> findByUserIdWithRolesAndPermissions(String userId);
```

**Usage**: When you have the user's custom userId
```java
User user = userRepository.findByUserIdWithRolesAndPermissions("usr_123456").orElseThrow();
```

---

## 🎯 Query Explanation

### Key Components:

**`SELECT DISTINCT u`**
- Returns distinct User objects to avoid duplicates from multiple joins

**`LEFT JOIN FETCH u.userRoles ur`**
- Eagerly fetches all UserRole associations for the user
- Uses LEFT JOIN to include users with no roles

**`LEFT JOIN FETCH ur.role rg`**
- Eagerly fetches the RoleGroup for each UserRole
- Loads role details (roleCode, roleName, description)

**`LEFT JOIN FETCH rg.permissions rp`**
- Eagerly fetches all RolePermission entries for each role
- Loads permission details (canRead, canEdit, canDelete)

**`LEFT JOIN FETCH rp.feature f`**
- Eagerly fetches the Feature for each permission
- Loads feature details (featureCode, featureName, module)

---

## 📊 Performance Benefits

### Before Optimization (Multiple Queries):
```
Query 1: SELECT * FROM users WHERE email = ?
Query 2: SELECT * FROM user_roles WHERE user_id = ?
Query 3: SELECT * FROM role_groups WHERE id IN (...)
Query 4: SELECT * FROM role_permissions WHERE role_id IN (...)
Query 5: SELECT * FROM features WHERE id IN (...)
Total: 5 queries per login
```

### After Optimization (Single Query):
```
Query 1: SELECT DISTINCT u FROM User u 
         LEFT JOIN FETCH u.userRoles ur 
         LEFT JOIN FETCH ur.role rg 
         LEFT JOIN FETCH rg.permissions rp 
         LEFT JOIN FETCH rp.feature f 
         WHERE u.email = ?
Total: 1 query per login
```

**Result**: 80% reduction in database queries! ✅

---

## 🔧 Updated Services

### AuthService - Login Method
```java
public LoginResponse login(LoginRequest request) {
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    );
    
    // Single query fetches user with all roles and permissions
    User user = userRepository.findByEmailWithRolesAndPermissions(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

    return buildLoginResponseAndUpdateLastLogin(user);
}
```

### AuthService - Build Login Response
```java
private LoginResponse buildLoginResponseAndUpdateLastLogin(User user) {
    user.setLastLogin(LocalDateTime.now());
    userRepository.save(user);

    // Extract roles from already-fetched userRoles
    List<String> roles = user.getUserRoles().stream()
            .filter(userRole -> Boolean.TRUE.equals(userRole.getIsActive()))
            .map(userRole -> userRole.getRole().getRoleCode())
            .collect(Collectors.toList());

    // Extract permissions from already-fetched roles and permissions
    List<String> permissions = user.getUserRoles().stream()
            .filter(userRole -> Boolean.TRUE.equals(userRole.getIsActive()))
            .flatMap(userRole -> userRole.getRole().getPermissions().stream())
            .flatMap(rolePermission -> {
                List<String> perms = new ArrayList<>();
                String featureCode = rolePermission.getFeature().getFeatureCode();
                if (Boolean.TRUE.equals(rolePermission.getCanRead())) {
                    perms.add(featureCode + ":READ");
                }
                if (Boolean.TRUE.equals(rolePermission.getCanEdit())) {
                    perms.add(featureCode + ":EDIT");
                }
                if (Boolean.TRUE.equals(rolePermission.getCanDelete())) {
                    perms.add(featureCode + ":DELETE");
                }
                return perms.stream();
            })
            .distinct()
            .collect(Collectors.toList());

    // Build JWT token with roles and permissions
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", user.getUserId());
    claims.put("name", user.getNames());
    claims.put("email", user.getEmail());
    claims.put("avatar", user.getAvatar());
    claims.put("roles", roles);
    claims.put("permissions", permissions);

    String token = jwtUtil.generateToken(user.getEmail(), claims);
    String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), claims);

    return LoginResponse.builder()
            .accessToken(token)
            .refreshToken(refreshToken)
            .user(LoginResponse.UserInfo.builder()
                    .id(user.getUserId())
                    .name(user.getNames())
                    .email(user.getEmail())
                    .role(roles.isEmpty() ? "USER" : roles.get(0))
                    .avatar(user.getAvatar())
                    .build())
            .build();
}
```

### CustomUserDetailsService
```java
@Override
public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    // Single query fetches user with all roles and permissions
    User user = userRepository.findByEmailWithRolesAndPermissions(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    
    if (!Boolean.TRUE.equals(user.getIsActive())) {
        throw new UsernameNotFoundException("User account is inactive");
    }
    
    // Extract authorities from already-fetched userRoles
    List<SimpleGrantedAuthority> authorities = user.getUserRoles().stream()
            .filter(userRole -> Boolean.TRUE.equals(userRole.getIsActive()))
            .map(userRole -> new SimpleGrantedAuthority("ROLE_" + userRole.getRole().getRoleCode()))
            .collect(Collectors.toList());
    
    if (authorities.isEmpty()) {
        authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
    }
    
    return org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail())
            .password(user.getPassword())
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(!Boolean.TRUE.equals(user.getIsActive()))
            .build();
}
```

---

## 🎁 Benefits of This Approach

✅ **Single Database Query**: All data fetched in one optimized query  
✅ **No N+1 Problem**: Eliminates the N+1 query problem  
✅ **Nested Objects**: Roles and permissions are nested in the User object  
✅ **Better Performance**: Reduced database round trips  
✅ **Cleaner Code**: No need for separate repository calls  
✅ **Type Safe**: Strong typing with nested objects  
✅ **Lazy Loading Avoided**: All data eagerly fetched in one go  

---

## 📌 Important Notes

### DISTINCT Keyword
The `DISTINCT` keyword is crucial because:
- Multiple joins can create duplicate rows
- `DISTINCT` ensures each User is returned only once
- All related roles and permissions are still included

### LEFT JOIN vs INNER JOIN
Using `LEFT JOIN` ensures:
- Users without roles are still returned
- Users without permissions are still returned
- More flexible for edge cases

### FETCH Keyword
The `FETCH` keyword is essential for:
- Eager loading of associations
- Avoiding lazy loading exceptions
- Loading all data in a single query

---

## 🔍 SQL Generated

The JPQL query generates SQL similar to:
```sql
SELECT DISTINCT u.* 
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN role_groups rg ON ur.role_id = rg.id
LEFT JOIN role_permissions rp ON rg.id = rp.role_id
LEFT JOIN features f ON rp.feature_id = f.id
WHERE u.email = ?
```

---

## ✨ Summary

This optimization transforms the user authentication flow from multiple sequential queries into a single, efficient database query that fetches the complete user object with all nested roles and permissions. This is the **best practice** for handling complex object hierarchies in Spring Data JPA.

**Status**: ✅ **IMPLEMENTED AND OPTIMIZED**
