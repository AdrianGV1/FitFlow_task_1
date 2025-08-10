package una.ac.cr.FitFlow.repository;

import java.util.List;

import una.ac.cr.FitFlow.model.Role;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByModule(Role.Module module);
    List<Role> findByPermission(Role.Permission permission);
}
