package una.ac.cr.FitFlow.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import una.ac.cr.FitFlow.dto.Role.RoleInputDTO;
import una.ac.cr.FitFlow.dto.Role.RoleOutputDTO;
import una.ac.cr.FitFlow.model.Role;

class MapperForRoleTest {

  private final MapperForRole mapper = new MapperForRole();

  /* ====================== toDto ====================== */

  @Test
  @DisplayName("toDto: mapea todos los campos y deriva el name como MODULE_PERMISSION")
  void toDto_ok() {
    Role role = Role.builder()
        .id(10L)
        .module(Role.Module.RUTINAS)
        .permission(Role.Permission.EDITOR)
        .build();

    RoleOutputDTO dto = mapper.toDto(role);

    assertThat(dto).isNotNull();
    assertThat(dto.getId()).isEqualTo(10L);
    assertThat(dto.getModule()).isEqualTo(Role.Module.RUTINAS);
    assertThat(dto.getPermissions()).isEqualTo(Role.Permission.EDITOR);
    assertThat(dto.getName()).isEqualTo("RUTINAS_EDITOR"); // module + "_" + permission
  }

  @Test
  @DisplayName("toDto: devuelve null si role es null")
  void toDto_nullRole_returnsNull() {
    assertThat(mapper.toDto(null)).isNull();
  }

  /* ====================== toEntity ====================== */

  @Test
  @DisplayName("toEntity: crea Role a partir de RoleInputDTO")
  void toEntity_ok() {
    RoleInputDTO dto = RoleInputDTO.builder()
        .id(99L)
        .module(Role.Module.PROGRESO)
        .permissions(Role.Permission.AUDITOR)
        .build();

    Role role = mapper.toEntity(dto);

    assertThat(role).isNotNull();
    assertThat(role.getId()).isEqualTo(99L);
    assertThat(role.getModule()).isEqualTo(Role.Module.PROGRESO);
    assertThat(role.getPermission()).isEqualTo(Role.Permission.AUDITOR);
  }

  @Test
  @DisplayName("toEntity: devuelve null si dto es null")
  void toEntity_nullDto_returnsNull() {
    assertThat(mapper.toEntity(null)).isNull();
  }

  /* ====================== copyToEntity ====================== */

  @Test
  @DisplayName("copyToEntity: solo actualiza los campos no nulos del dto")
  void copyToEntity_updatesOnlyNonNullFields() {
    Role target = Role.builder()
        .id(5L)
        .module(Role.Module.GUIAS)
        .permission(Role.Permission.AUDITOR)
        .build();

    RoleInputDTO dto = RoleInputDTO.builder()
        .module(Role.Module.RECORDATORIOS) // cambia módulo
        .permissions(null)                  // permiso NO cambia
        .build();

    mapper.copyToEntity(dto, target);

    assertThat(target.getModule()).isEqualTo(Role.Module.RECORDATORIOS);
    assertThat(target.getPermission()).isEqualTo(Role.Permission.AUDITOR); // se mantiene
  }

  @Test
  @DisplayName("copyToEntity: actualiza módulo y permiso cuando ambos vienen en el dto")
  void copyToEntity_updatesBothFields() {
    Role target = Role.builder()
        .id(6L)
        .module(Role.Module.ACTIVIDADES)
        .permission(Role.Permission.AUDITOR)
        .build();

    RoleInputDTO dto = RoleInputDTO.builder()
        .module(Role.Module.RUTINAS)
        .permissions(Role.Permission.EDITOR)
        .build();

    mapper.copyToEntity(dto, target);

    assertThat(target.getModule()).isEqualTo(Role.Module.RUTINAS);
    assertThat(target.getPermission()).isEqualTo(Role.Permission.EDITOR);
  }
}