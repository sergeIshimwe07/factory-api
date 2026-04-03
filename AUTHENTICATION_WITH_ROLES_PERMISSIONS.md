# Authentication with Roles & Permissions - Complete Implementation

## 📋 Overview

Implemented comprehensive authentication system that:
- Fetches users with roles and permissions in a single optimized query
- Validates users have assigned roles before login
- Validates users have permissions assigned to their roles
- Returns detailed role and permission information in login response
- Includes role and permission data in JWT token claims

---

## 🔐 Authentication Flow

```
1. User submits login credentials
   ↓
2. AuthenticationManager validates password
   ↓
3. UserRepository fetches user with roles & permissions (single JOIN FETCH query)
   ↓
4. Validate user has active roles
   ↓
5. Validate user has permissions
   ↓
6. Build role and permission objects
   ↓
7. Generate JWT token with claims
   ↓
8. Return LoginResponse with nested roles & permissions
```

---

## ✅ Validation Rules

### Rule 1: User Must Have At Least One Role
```
if (activeUserRoles.isEmpty()) {
    throw new IllegalArgumentException(
        "User does not have any assigned roles. Please contact administrator."
    );
}
```

**Error Message**: `"User does not have any assigned roles. Please contact administrator."`

### Rule 2: User's Roles Must Have Permissions
```
if (!hasPermissions) {
    throw new IllegalArgumentException(
        "User does not have any permissions assigned to their roles. Please contact administrator."
    );
}
```

**Error Message**: `"User does not have any permissions assigned to their roles. Please contact administrator."`

---

## 📊 LoginResponse Structure

### Complete Response Format

```json
{
  "status": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": "usr_1234567890",
      "name": "John Doe",
      "email": "john@factory.com",
      "avatar": "https://...",
      "roles": [
        {
          "id": 1,
          "roleCode": "ADMIN",
          "roleName": "Administrator",
          "description": "Full system access"
        },
        {
          "id": 2,
          "roleCode": "SALES_MANAGER",
          "roleName": "Sales Manager",
          "description": "Manage sales operations"
        }
      ],
      "permissions": [
        {
          "id": 1,
          "featureCode": "PRODUCTS",
          "featureName": "Product Management",
          "module": "inventory",
          "canRead": true,
          "canEdit": true,
          "canDelete": true
        },
        {
          "id": 2,
          "featureCode": "SALES",
          "featureName": "Sales Orders",
          "module": "sales",
          "canRead": true,
          "canEdit": true,
          "canDelete": false
        },
        {
          "id": 3,
          "featureCode": "CUSTOMERS",
          "featureName": "Customer Management",
          "module": "crm",
          "canRead": true,
          "canEdit": true,
          "canDelete": true
        }
      ]
    }
  }
}
```

---

## 🔑 JWT Token Claims

The JWT token includes the following claims:

```json
{
  "userId": "usr_1234567890",
  "name": "John Doe",
  "email": "john@factory.com",
  "avatar": "https://...",
  "roles": ["ADMIN", "SALES_MANAGER"],
  "permissions": [
    "PRODUCTS:READ",
    "PRODUCTS:EDIT",
    "PRODUCTS:DELETE",
    "SALES:READ",
    "SALES:EDIT",
    "CUSTOMERS:READ",
    "CUSTOMERS:EDIT",
    "CUSTOMERS:DELETE"
  ]
}
```

---

## 🛠️ Implementation Details

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

### AuthService - Build Login Response with Validation

```java
private LoginResponse buildLoginResponseAndUpdateLastLogin(User user) {
    // Get active user roles
    List<com.factory.factory_erp.entity.UserRole> activeUserRoles = user.getUserRoles().stream()
            .filter(userRole -> Boolean.TRUE.equals(userRole.getIsActive()))
            .collect(Collectors.toList());

    // ✅ VALIDATION 1: User must have at least one role
    if (activeUserRoles.isEmpty()) {
        throw new IllegalArgumentException(
            "User does not have any assigned roles. Please contact administrator."
        );
    }

    // ✅ VALIDATION 2: User's roles must have permissions
    boolean hasPermissions = activeUserRoles.stream()
            .flatMap(userRole -> userRole.getRole().getPermissions().stream())
            .findAny()
            .isPresent();

    if (!hasPermissions) {
        throw new IllegalArgumentException(
            "User does not have any permissions assigned to their roles. Please contact administrator."
        );
    }

    // Update last login timestamp
    user.setLastLogin(LocalDateTime.now());
    userRepository.save(user);

    // Build role info objects
    List<LoginResponse.RoleInfo> roleInfos = activeUserRoles.stream()
            .map(userRole -> LoginResponse.RoleInfo.builder()
                    .id(userRole.getRole().getId())
                    .roleCode(userRole.getRole().getRoleCode())
                    .roleName(userRole.getRole().getRoleName())
                    .description(userRole.getRole().getDescription())
                    .build())
            .collect(Collectors.toList());

    // Build permission info objects
    List<LoginResponse.PermissionInfo> permissionInfos = activeUserRoles.stream()
            .flatMap(userRole -> userRole.getRole().getPermissions().stream())
            .map(rolePermission -> LoginResponse.PermissionInfo.builder()
                    .id(rolePermission.getId())
                    .featureCode(rolePermission.getFeature().getFeatureCode())
                    .featureName(rolePermission.getFeature().getFeatureName())
                    .module(rolePermission.getFeature().getModule())
                    .canRead(rolePermission.getCanRead())
                    .canEdit(rolePermission.getCanEdit())
                    .canDelete(rolePermission.getCanDelete())
                    .build())
            .distinct()
            .collect(Collectors.toList());

    // Extract role codes for JWT claims
    List<String> roleCodes = roleInfos.stream()
            .map(LoginResponse.RoleInfo::getRoleCode)
            .collect(Collectors.toList());

    // Extract permission strings for JWT claims
    List<String> permissionStrings = permissionInfos.stream()
            .flatMap(perm -> {
                List<String> perms = new ArrayList<>();
                if (Boolean.TRUE.equals(perm.getCanRead())) {
                    perms.add(perm.getFeatureCode() + ":READ");
                }
                if (Boolean.TRUE.equals(perm.getCanEdit())) {
                    perms.add(perm.getFeatureCode() + ":EDIT");
                }
                if (Boolean.TRUE.equals(perm.getCanDelete())) {
                    perms.add(perm.getFeatureCode() + ":DELETE");
                }
                return perms.stream();
            })
            .distinct()
            .collect(Collectors.toList());

    // Build JWT claims
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", user.getUserId());
    claims.put("name", user.getNames());
    claims.put("email", user.getEmail());
    claims.put("avatar", user.getAvatar());
    claims.put("roles", roleCodes);
    claims.put("permissions", permissionStrings);

    String token = jwtUtil.generateToken(user.getEmail(), claims);
    String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), claims);

    return LoginResponse.builder()
            .accessToken(token)
            .refreshToken(refreshToken)
            .user(LoginResponse.UserInfo.builder()
                    .id(user.getUserId())
                    .name(user.getNames())
                    .email(user.getEmail())
                    .avatar(user.getAvatar())
                    .roles(roleInfos)
                    .permissions(permissionInfos)
                    .build())
            .build();
}
```

---

## 📝 LoginResponse DTO Structure

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private String accessToken;
    private String refreshToken;
    private UserInfo user;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String id;
        private String name;
        private String email;
        private String avatar;
        private List<RoleInfo> roles;
        private List<PermissionInfo> permissions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleInfo {
        private Long id;
        private String roleCode;
        private String roleName;
        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermissionInfo {
        private Long id;
        private String featureCode;
        private String featureName;
        private String module;
        private Boolean canRead;
        private Boolean canEdit;
        private Boolean canDelete;
    }
}
```

---

## 🧪 Test Scenarios

### Scenario 1: User Without Roles (Should Fail)
```
User: test@factory.com
Roles: None assigned
Expected Result: Login fails with error
Error Message: "User does not have any assigned roles. Please contact administrator."
HTTP Status: 400 Bad Request
```

### Scenario 2: User With Roles But No Permissions (Should Fail)
```
User: test@factory.com
Roles: VIEWER (no permissions assigned)
Expected Result: Login fails with error
Error Message: "User does not have any permissions assigned to their roles. Please contact administrator."
HTTP Status: 400 Bad Request
```

### Scenario 3: User With Roles and Permissions (Should Succeed)
```
User: admin@factory.com
Roles: ADMIN, SALES_MANAGER
Permissions: PRODUCTS:READ, PRODUCTS:EDIT, SALES:READ, SALES:EDIT, CUSTOMERS:READ
Expected Result: Login succeeds
Response: Complete user info with roles and permissions
HTTP Status: 200 OK
```

---

## 🔄 Error Handling

### Global Exception Handler Integration

The validation errors are caught by the global exception handler:

```java
@ExceptionHandler(IllegalArgumentException.class)
public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ex.getMessage(), "INVALID_ARGUMENT"));
}
```

### Error Response Format

```json
{
  "status": "error",
  "message": "User does not have any assigned roles. Please contact administrator.",
  "code": "INVALID_ARGUMENT",
  "timestamp": "2026-04-01T15:58:00"
}
```

---

## 🎯 Benefits

✅ **Single Query**: All data fetched in one optimized JOIN FETCH query  
✅ **Validation**: Users without roles/permissions cannot login  
✅ **Rich Response**: Complete role and permission details in login response  
✅ **JWT Claims**: Roles and permissions included in token for authorization  
✅ **Type Safe**: Strongly typed nested objects in response  
✅ **Clear Errors**: Descriptive error messages for administrators  
✅ **Performance**: No N+1 queries, minimal database round trips  

---

## 📌 Key Points

1. **Roles are mandatory** - Users must have at least one active role to login
2. **Permissions are mandatory** - Roles must have at least one permission
3. **Single query** - Uses JOIN FETCH to load all data in one query
4. **Nested objects** - Roles and permissions returned as nested lists in response
5. **JWT integration** - Role codes and permission strings included in JWT claims
6. **Error messages** - Clear, actionable error messages for administrators

---

## ✨ Summary

The authentication system now provides:
- **Robust validation** preventing users without proper role/permission setup from logging in
- **Rich response data** with complete role and permission information
- **Optimized queries** using JOIN FETCH for single-query data retrieval
- **JWT integration** with role and permission claims for authorization
- **Clear error messages** for administrators to troubleshoot access issues

**Status**: ✅ **COMPLETE AND PRODUCTION READY**
