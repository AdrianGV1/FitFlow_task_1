package una.ac.cr.FitFlow.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.persistence.*;

class UserTest {

    // ---- Helpers ----
    private <A extends Annotation> A getAnn(Field f, Class<A> ann) { return f.getAnnotation(ann); }
    private <A extends Annotation> A getTypeAnn(Class<?> c, Class<A> ann) { return c.getAnnotation(ann); }

    // ---- POJO / Lombok ----
    @Test
    @DisplayName("Builder, getters/setters y @Builder.Default en colecciones")
    void builderAndAccessors_ok() {
        User coach = User.builder().id(999L).username("coach").password("p").email("c@c.com").build();

        User u = User.builder()
                .id(1L)
                .username("moises")
                .password("secret")
                .email("m@una.ac.cr")
                .coach(coach)
                // no pasamos colecciones para validar @Builder.Default
                .build();

        assertThat(u.getId()).isEqualTo(1L);
        assertThat(u.getUsername()).isEqualTo("moises");
        assertThat(u.getPassword()).isEqualTo("secret");
        assertThat(u.getEmail()).isEqualTo("m@una.ac.cr");
        assertThat(u.getCoach()).isSameAs(coach);

        // Colecciones inicializadas
        assertThat(u.getRoles()).isNotNull().isEmpty();
        assertThat(u.getHabits()).isNotNull().isEmpty();
        assertThat(u.getRoutines()).isNotNull().isEmpty();
        assertThat(u.getProgressLogs()).isNotNull().isEmpty();
        assertThat(u.getReminders()).isNotNull().isEmpty();

        // Setters básicos
        u.setUsername("moises2");
        assertThat(u.getUsername()).isEqualTo("moises2");
    }

    // ---- Anotaciones de clase ----
    @Test
    @DisplayName("@Entity y @Table(name=\"users\") presentes")
    void classAnnotations_ok() {
        assertThat(getTypeAnn(User.class, Entity.class)).isNotNull();
        Table t = getTypeAnn(User.class, Table.class);
        assertThat(t).isNotNull();
        assertThat(t.name()).isEqualTo("users");
    }

    // ---- id ----
    @Test
    @DisplayName("id con @Id y @GeneratedValue(IDENTITY)")
    void idMapping_ok() throws Exception {
        Field f = User.class.getDeclaredField("id");
        assertThat(getAnn(f, Id.class)).isNotNull();
        GeneratedValue gv = getAnn(f, GeneratedValue.class);
        assertThat(gv).isNotNull();
        assertThat(gv.strategy()).isEqualTo(GenerationType.IDENTITY);
    }

    // ---- username ----
    @Test
    @DisplayName("username con @Column(nullable=false, unique=true, length=50)")
    void usernameMapping_ok() throws Exception {
        Field f = User.class.getDeclaredField("username");
        Column c = getAnn(f, Column.class);
        assertThat(c).isNotNull();
        assertThat(c.nullable()).isFalse();
        assertThat(c.unique()).isTrue();
        assertThat(c.length()).isEqualTo(50);
    }

    // ---- password ----
    @Test
    @DisplayName("password con @Column(nullable=false, length=100)")
    void passwordMapping_ok() throws Exception {
        Field f = User.class.getDeclaredField("password");
        Column c = getAnn(f, Column.class);
        assertThat(c).isNotNull();
        assertThat(c.nullable()).isFalse();
        assertThat(c.length()).isEqualTo(100);
    }

    // ---- email ----
    @Test
    @DisplayName("email con @Column(nullable=false, unique=true, length=100)")
    void emailMapping_ok() throws Exception {
        Field f = User.class.getDeclaredField("email");
        Column c = getAnn(f, Column.class);
        assertThat(c).isNotNull();
        assertThat(c.nullable()).isFalse();
        assertThat(c.unique()).isTrue();
        assertThat(c.length()).isEqualTo(100);
    }

    // ---- roles (ManyToMany con JoinTable user_roles) ----
    @Test
    @DisplayName("roles con @ManyToMany(LAZY) y @JoinTable user_roles (user_id, role_id)")
    void rolesMapping_ok() throws Exception {
        Field f = User.class.getDeclaredField("roles");

        ManyToMany m2m = getAnn(f, ManyToMany.class);
        assertThat(m2m).isNotNull();
        assertThat(m2m.fetch()).isEqualTo(FetchType.LAZY);

        JoinTable jt = getAnn(f, JoinTable.class);
        assertThat(jt).isNotNull();
        assertThat(jt.name()).isEqualTo("user_roles");
        assertThat(jt.joinColumns()).hasSize(1);
        assertThat(jt.joinColumns()[0].name()).isEqualTo("user_id");
        assertThat(jt.inverseJoinColumns()).hasSize(1);
        assertThat(jt.inverseJoinColumns()[0].name()).isEqualTo("role_id");

        assertThat(Set.class.isAssignableFrom(f.getType())).isTrue();
    }

    // ---- habits (ManyToMany con JoinTable user_habit) ----
    @Test
    @DisplayName("habits con @ManyToMany(LAZY) y @JoinTable user_habit (user_id, habit_id)")
    void habitsMapping_ok() throws Exception {
        Field f = User.class.getDeclaredField("habits");

        ManyToMany m2m = getAnn(f, ManyToMany.class);
        assertThat(m2m).isNotNull();
        assertThat(m2m.fetch()).isEqualTo(FetchType.LAZY);

        JoinTable jt = getAnn(f, JoinTable.class);
        assertThat(jt).isNotNull();
        assertThat(jt.name()).isEqualTo("user_habit");
        assertThat(jt.joinColumns()).hasSize(1);
        assertThat(jt.joinColumns()[0].name()).isEqualTo("user_id");
        assertThat(jt.inverseJoinColumns()).hasSize(1);
        assertThat(jt.inverseJoinColumns()[0].name()).isEqualTo("habit_id");

        assertThat(Set.class.isAssignableFrom(f.getType())).isTrue();
    }

    // ---- routines ----
    @Test
    @DisplayName("routines con @OneToMany(mappedBy=\"user\", cascade=ALL, orphanRemoval=true)")
    void routinesMapping_ok() throws Exception {
        Field f = User.class.getDeclaredField("routines");
        OneToMany o2m = getAnn(f, OneToMany.class);
        assertThat(o2m).isNotNull();
        assertThat(o2m.mappedBy()).isEqualTo("user");
        assertThat(o2m.cascade()).contains(CascadeType.ALL);
        assertThat(o2m.orphanRemoval()).isTrue();
        assertThat(List.class.isAssignableFrom(f.getType())).isTrue();
    }

    // ---- progressLogs ----
    @Test
    @DisplayName("progressLogs con @OneToMany(mappedBy=\"user\", cascade=ALL, orphanRemoval=true)")
    void progressLogsMapping_ok() throws Exception {
        Field f = User.class.getDeclaredField("progressLogs");
        OneToMany o2m = getAnn(f, OneToMany.class);
        assertThat(o2m).isNotNull();
        assertThat(o2m.mappedBy()).isEqualTo("user");
        assertThat(o2m.cascade()).contains(CascadeType.ALL);
        assertThat(o2m.orphanRemoval()).isTrue();
        assertThat(List.class.isAssignableFrom(f.getType())).isTrue();
    }

    // ---- reminders ----
    @Test
    @DisplayName("reminders con @OneToMany(mappedBy=\"user\", cascade=ALL, orphanRemoval=true)")
    void remindersMapping_ok() throws Exception {
        Field f = User.class.getDeclaredField("reminders");
        OneToMany o2m = getAnn(f, OneToMany.class);
        assertThat(o2m).isNotNull();
        assertThat(o2m.mappedBy()).isEqualTo("user");
        assertThat(o2m.cascade()).contains(CascadeType.ALL);
        assertThat(o2m.orphanRemoval()).isTrue();
        assertThat(List.class.isAssignableFrom(f.getType())).isTrue();
    }

    // ---- coach (self-relation) ----
    @Test
    @DisplayName("coach con @ManyToOne(LAZY) y @JoinColumn(name=\"coach_id\")")
    void coachMapping_ok() throws Exception {
        Field f = User.class.getDeclaredField("coach");

        ManyToOne m2o = getAnn(f, ManyToOne.class);
        assertThat(m2o).isNotNull();
        assertThat(m2o.fetch()).isEqualTo(FetchType.LAZY);
        // optional por defecto = true (no nullable obligatorio)
        JoinColumn jc = getAnn(f, JoinColumn.class);
        assertThat(jc).isNotNull();
        assertThat(jc.name()).isEqualTo("coach_id");
        // no se especificó nullable -> default true
        assertThat(jc.nullable()).isTrue();
    }
}
