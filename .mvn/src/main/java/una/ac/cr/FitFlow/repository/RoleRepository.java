package una.ac.cr.FitFlow.repository;

import java.util.Optional;
import una.ac.cr.FitFlow.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByModule(Role.Module module);

    boolean existsByModuleAndPermission(Role.Module module, Role.Permission permission);
    boolean existsByModuleAndPermissionAndIdNot(Role.Module module, Role.Permission permission, Long id);

    Optional<Role> findByModuleAndPermission(Role.Module module, Role.Permission permission);

    Page<Role> findAll(Pageable pageable);
}
