package una.ac.cr.FitFlow.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.persistence.*;

class RoleTest {

    // ---- Helpers ----
    private <A extends Annotation> A getAnn(Field f, Class<A> ann) { return f.getAnnotation(ann); }
    private <A extends Annotation> A getTypeAnn(Class<?> c, Class<A> ann) { return c.getAnnotation(ann); }

    // ---- POJO / Lombok ----
    @Test
    @DisplayName("Builder, getters/setters y @Builder.Default funcionan")
    void builderAndAccessors_ok() {
        Role r = Role.builder()
                .id(1L)
                .module(Role.Module.RUTINAS)
                .permission(Role.Permission.EDITOR)
                .build();

        assertThat(r.getId()).isEqualTo(1L);
        assertThat(r.getModule()).isEqualTo(Role.Module.RUTINAS);
        assertThat(r.getPermission()).isEqualTo(Role.Permission.EDITOR);
        // @Builder.Default en users
        assertThat(r.getUsers()).isNotNull().isEmpty();

        // setters
        r.setPermission(Role.Permission.AUDITOR);
        assertThat(r.getPermission()).isEqualTo(Role.Permission.AUDITOR);
    }

    @Test
    @DisplayName("@EqualsAndHashCode(onlyExplicitlyIncluded=true) usa module+permission (id ignorado)")
    void equalsAndHashCode_byModuleAndPermission() {
        Role a = Role.builder()
                .id(10L)
                .module(Role.Module.GUIAS)
                .permission(Role.Permission.EDITOR)
                .build();

        Role b = Role.builder()
                .id(99L) // distinto id
                .module(Role.Module.GUIAS)
                .permission(Role.Permission.EDITOR)
                .build();

        Role c = Role.builder()
                .module(Role.Module.GUIAS)
                .permission(Role.Permission.AUDITOR)
                .build();

        assertThat(a).isEqualTo(b);
        assertThat(a).hasSameHashCodeAs(b);

        assertThat(a).isNotEqualTo(c);
    }

    @Test
    @DisplayName("@ToString(onlyExplicitlyIncluded=true) no expone campos sin @ToString.Include")
    void toString_notIncludingFieldsByDefault() {
        Role r = Role.builder()
                .module(Role.Module.PROGRESO)
                .permission(Role.Permission.AUDITOR)
                .build();

        String s = r.toString();
        assertThat(s).contains("Role"); // nombre de clase
        // no debería incluir valores de campos al no existir @ToString.Include
        assertThat(s).doesNotContain("PROGRESO");
        assertThat(s).doesNotContain("AUDITOR");
    }

    // ---- Anotaciones de clase ----
    @Test
    @DisplayName("@Entity y @Table(name=\"roles\") con unique constraint (module, permission)")
    void classAnnotations_ok() {
        assertThat(getTypeAnn(Role.class, Entity.class)).isNotNull();

        Table t = getTypeAnn(Role.class, Table.class);
        assertThat(t).isNotNull();
        assertThat(t.name()).isEqualTo("roles");

        UniqueConstraint[] ucs = t.uniqueConstraints();
        assertThat(ucs).isNotEmpty();
        // buscamos el constraint por nombre y columnas
        boolean found = false;
        for (UniqueConstraint uc : ucs) {
            if ("uk_roles_module_permission".equals(uc.name())) {
                assertThat(uc.columnNames()).containsExactlyInAnyOrder("module", "permission");
                found = true;
            }
        }
        assertThat(found).as("UniqueConstraint uk_roles_module_permission no encontrado").isTrue();
    }

    // ---- id ----
    @Test
    @DisplayName("id con @Id y @GeneratedValue(IDENTITY)")
    void idMapping_ok() throws Exception {
        Field f = Role.class.getDeclaredField("id");
        assertThat(getAnn(f, Id.class)).isNotNull();

        GeneratedValue gv = getAnn(f, GeneratedValue.class);
        assertThat(gv).isNotNull();
        assertThat(gv.strategy()).isEqualTo(GenerationType.IDENTITY);
    }

    // ---- module ----
    @Test
    @DisplayName("module con @Enumerated(STRING) y @Column(nullable=false, length=20)")
    void moduleMapping_ok() throws Exception {
        Field f = Role.class.getDeclaredField("module");

        Enumerated en = getAnn(f, Enumerated.class);
        assertThat(en).isNotNull();
        assertThat(en.value()).isEqualTo(EnumType.STRING);

        Column c = getAnn(f, Column.class);
        assertThat(c).isNotNull();
        assertThat(c.nullable()).isFalse();
        assertThat(c.length()).isEqualTo(20);
    }

    // ---- permission ----
    @Test
    @DisplayName("permission con @Enumerated(STRING) y @Column(nullable=false, length=20)")
    void permissionMapping_ok() throws Exception {
        Field f = Role.class.getDeclaredField("permission");

        Enumerated en = getAnn(f, Enumerated.class);
        assertThat(en).isNotNull();
        assertThat(en.value()).isEqualTo(EnumType.STRING);

        Column c = getAnn(f, Column.class);
        assertThat(c).isNotNull();
        assertThat(c.nullable()).isFalse();
        assertThat(c.length()).isEqualTo(20);
    }

    // ---- users ----
    @Test
    @DisplayName("users con @ManyToMany(mappedBy=\"roles\", LAZY)")
    void usersMapping_ok() throws Exception {
        Field f = Role.class.getDeclaredField("users");

        ManyToMany m2m = getAnn(f, ManyToMany.class);
        assertThat(m2m).isNotNull();
        assertThat(m2m.mappedBy()).isEqualTo("roles");
        assertThat(m2m.fetch()).isEqualTo(FetchType.LAZY);

        assertThat(Set.class.isAssignableFrom(f.getType())).isTrue();
    }
}
