# Role-Based Access Control (RBAC) Implementation

## Overview

This factory ERP system implements a sophisticated Role-Based Access Control (RBAC) system that allows:

1. **Multiple Roles per User**: Users can have multiple role groups assigned simultaneously
2. **Feature-Based Permissions**: Each role group has specific permissions for different system features
3. **Granular Permission Control**: Three permission types per feature: READ, EDIT, DELETE
4. **Flexible Role Management**: Roles can be assigned/removed dynamically

## Database Schema

### Core Tables

#### 1. `features`
Defines all system features/modules that can be protected.

- `feature_code`: Unique identifier (e.g., 'USER_MANAGEMENT', 'SALES_ORDER_MANAGEMENT')
- `feature_name`: Human-readable name
- `module`: Grouping category (e.g., 'ADMINISTRATION', 'SALES', 'INVENTORY')

#### 2. `role_groups`
Defines role groups that users can be assigned to.

- `role_code`: Unique identifier (e.g., 'ADMIN', 'SALES_AGENT')
- `role_name`: Human-readable name
- `description`: Role description

#### 3. `role_permissions`
Links roles to features with specific permissions.

- `role_id`: Foreign key to role_groups
- `feature_id`: Foreign key to features
- `can_read`: Boolean permission
- `can_edit`: Boolean permission
- `can_delete`: Boolean permission

#### 4. `user_roles`
Many-to-many relationship between users and role groups.

- `user_id`: Foreign key to users
- `role_id`: Foreign key to role_groups
- `assigned_by`: User who assigned the role
- `is_active`: Whether the role assignment is active

## Pre-configured Roles

### 1. ADMIN
Full system access with all permissions on all features.

### 2. SALES_AGENT
- **Full Access**: Customer Management, Sales Orders, Quotations, Invoices, Commissions
- **Read-Only**: Product Management, Inventory, Sales Reports

### 3. PRODUCTION_MANAGER
- **Full Access**: Production Management, BOM Management, Inventory Management
- **Read-Only**: Product Management, Production Reports, Inventory Reports

### 4. ACCOUNTANT
- **Full Access**: Chart of Accounts, Journal Entries, Invoices
- **Read-Only**: Financial Reports, Sales Reports, Sales Orders, Purchase Orders

### 5. STORE_MANAGER
- **Full Access**: Inventory Management, Product Management
- **Read-Only**: Inventory Reports, Purchase Orders, Sales Orders, Production

### 6. PROCUREMENT_OFFICER
- **Full Access**: Supplier Management, Purchase Orders
- **Read-Only**: Product Management, Inventory Management, Inventory Reports

## System Features

### Administration Module
- `USER_MANAGEMENT`: Manage system users
- `ROLE_MANAGEMENT`: Manage roles and permissions

### CRM Module
- `CUSTOMER_MANAGEMENT`: Manage customer records

### Procurement Module
- `SUPPLIER_MANAGEMENT`: Manage supplier records
- `PURCHASE_ORDER_MANAGEMENT`: Manage purchase orders

### Inventory Module
- `PRODUCT_MANAGEMENT`: Manage product catalog
- `INVENTORY_MANAGEMENT`: Manage stock and inventory

### Production Module
- `BOM_MANAGEMENT`: Manage Bill of Materials
- `PRODUCTION_MANAGEMENT`: Manage production entries

### Sales Module
- `SALES_ORDER_MANAGEMENT`: Manage sales orders
- `QUOTATION_MANAGEMENT`: Manage quotations
- `INVOICE_MANAGEMENT`: Manage invoices
- `COMMISSION_MANAGEMENT`: Manage sales commissions

### Accounting Module
- `ACCOUNT_MANAGEMENT`: Manage chart of accounts
- `JOURNAL_ENTRY_MANAGEMENT`: Manage journal entries
- `FINANCIAL_REPORTS`: View financial reports

### Reports Module
- `SALES_REPORTS`: View sales analytics
- `INVENTORY_REPORTS`: View inventory reports
- `PRODUCTION_REPORTS`: View production reports

## Usage Examples

### 1. Using the @RequirePermission Annotation

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @GetMapping
    @RequirePermission(feature = "PRODUCT_MANAGEMENT", permission = RequirePermission.PermissionType.READ)
    public ResponseEntity<List<Product>> getAllProducts() {
        // Only users with READ permission on PRODUCT_MANAGEMENT can access
        return ResponseEntity.ok(productService.findAll());
    }
    
    @PostMapping
    @RequirePermission(feature = "PRODUCT_MANAGEMENT", permission = RequirePermission.PermissionType.EDIT)
    public ResponseEntity<Product> createProduct(@RequestBody ProductDTO dto) {
        // Only users with EDIT permission on PRODUCT_MANAGEMENT can access
        return ResponseEntity.ok(productService.create(dto));
    }
    
    @DeleteMapping("/{id}")
    @RequirePermission(feature = "PRODUCT_MANAGEMENT", permission = RequirePermission.PermissionType.DELETE)
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        // Only users with DELETE permission on PRODUCT_MANAGEMENT can access
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

### 2. Programmatic Permission Checking

```java
@Service
@RequiredArgsConstructor
public class SomeService {
    
    private final PermissionService permissionService;
    
    public void performAction(String userEmail) {
        if (permissionService.canEdit(userEmail, "INVENTORY_MANAGEMENT")) {
            // User has edit permission
            updateInventory();
        } else {
            throw new AccessDeniedException("Insufficient permissions");
        }
    }
}
```

### 3. Assigning Roles to Users

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final RoleManagementService roleManagementService;
    
    public void setupNewEmployee(Long userId) {
        // Assign multiple roles to a user
        roleManagementService.assignRoleToUser(userId, "SALES_AGENT", adminUserId);
        roleManagementService.assignRoleToUser(userId, "STORE_MANAGER", adminUserId);
        
        // User now has permissions from both roles
    }
}
```

## API Endpoints

### Role Management

#### Get All Roles
```
GET /api/roles
Requires: ROLE_MANAGEMENT READ permission
```

#### Assign Role to User
```
POST /api/roles/assign?userId={userId}&roleCode={roleCode}
Requires: ROLE_MANAGEMENT EDIT permission
```

#### Remove Role from User
```
DELETE /api/roles/remove?userId={userId}&roleCode={roleCode}
Requires: ROLE_MANAGEMENT DELETE permission
```

#### Get User's Roles
```
GET /api/roles/user/{userId}
Requires: USER_MANAGEMENT READ permission
```

#### Check Permission
```
GET /api/roles/check-permission?featureCode={code}&permission={type}
Returns: { "hasPermission": true/false }
```

## Migration Notes

The system automatically migrates existing users from the old enum-based role system:
- Old `admin` → New `ADMIN` role group
- Old `sales_agent` → New `SALES_AGENT` role group
- Old `production_manager` → New `PRODUCTION_MANAGER` role group
- Old `accountant` → New `ACCOUNTANT` role group
- Old `store_manager` → New `STORE_MANAGER` role group

## Security Considerations

1. **Permission Aspect**: Automatically intercepts methods annotated with `@RequirePermission`
2. **Spring Security Integration**: Works seamlessly with existing JWT authentication
3. **Multiple Roles**: User permissions are aggregated from all active role assignments
4. **Audit Trail**: Tracks who assigned roles and when

## Adding New Features

To add a new protected feature:

1. Insert into `features` table:
```sql
INSERT INTO features (feature_code, feature_name, description, module) 
VALUES ('NEW_FEATURE', 'New Feature', 'Description', 'MODULE_NAME');
```

2. Assign permissions to roles:
```sql
INSERT INTO role_permissions (role_id, feature_id, can_read, can_edit, can_delete)
SELECT r.id, f.id, TRUE, TRUE, FALSE
FROM role_groups r
CROSS JOIN features f
WHERE r.role_code = 'SALES_AGENT' AND f.feature_code = 'NEW_FEATURE';
```

3. Use in code:
```java
@RequirePermission(feature = "NEW_FEATURE", permission = RequirePermission.PermissionType.READ)
```

## Best Practices

1. **Always use feature codes**: Reference features by their `feature_code` constant
2. **Principle of least privilege**: Assign minimum required permissions
3. **Regular audits**: Review role assignments periodically
4. **Document custom roles**: If creating new roles, document their purpose
5. **Test permissions**: Always test permission checks for new features
