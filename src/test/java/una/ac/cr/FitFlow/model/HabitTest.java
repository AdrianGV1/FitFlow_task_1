package una.ac.cr.FitFlow.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.persistence.*;

class HabitTest {

    // -------- Helpers ----------
    private <A extends Annotation> A getAnn(Field f, Class<A> ann) { return f.getAnnotation(ann); }
    private <A extends Annotation> A getTypeAnn(Class<?> c, Class<A> ann) { return c.getAnnotation(ann); }

    // -------- POJO / Lombok ----------
    @Test
    @DisplayName("Builder, getters/setters y @Builder.Default funcionan")
    void builderAndAccessors_ok() {
        Habit h = Habit.builder()
                .id(5L)
                .name("Caminar 30m")
                .category(Habit.Category.PHYSICAL)
                .description("Actividad aeróbica diaria.")
                // sin pasar colecciones para comprobar @Builder.Default
                .build();

        assertThat(h.getId()).isEqualTo(5L);
        assertThat(h.getName()).isEqualTo("Caminar 30m");
        assertThat(h.getCategory()).isEqualTo(Habit.Category.PHYSICAL);
        assertThat(h.getDescription()).isEqualTo("Actividad aeróbica diaria.");

        // Colecciones por defecto no nulas y vacías
        assertThat(h.getUsers()).isNotNull().isEmpty();
        assertThat(h.getRoutineActivities()).isNotNull().isEmpty();
        assertThat(h.getCompletedActivities()).isNotNull().isEmpty();
        assertThat(h.getReminders()).isNotNull().isEmpty();
        assertThat(h.getGuides()).isNotNull().isEmpty();

        // Setters básicos
        h.setName("Caminar 45m");
        assertThat(h.getName()).isEqualTo("Caminar 45m");
    }

    @Test
    @DisplayName("@ToString(onlyExplicitlyIncluded=true) no expone campos por defecto")
    void toString_noFieldsIncludedByDefault() {
        Habit h = Habit.builder()
                .name("Privado")
                .description("No debe aparecer")
                .category(Habit.Category.DIET)
                .build();

        String s = h.toString();
        assertThat(s).contains("Habit");          // nombre de clase
        assertThat(s).doesNotContain("Privado");  // sin @ToString.Include no se imprime
        assertThat(s).doesNotContain("No debe aparecer");
    }

    // -------- Anotaciones de clase ----------
    @Test
    @DisplayName("@Entity y @Table(name=\"habits\") presentes")
    void classAnnotations_ok() {
        assertThat(getTypeAnn(Habit.class, Entity.class)).isNotNull();
        Table t = getTypeAnn(Habit.class, Table.class);
        assertThat(t).isNotNull();
        assertThat(t.name()).isEqualTo("habits");
    }

    // -------- id ----------
    @Test
    @DisplayName("id con @Id y @GeneratedValue(IDENTITY)")
    void idMapping_ok() throws Exception {
        Field f = Habit.class.getDeclaredField("id");
        assertThat(getAnn(f, Id.class)).isNotNull();
        GeneratedValue gv = getAnn(f, GeneratedValue.class);
        assertThat(gv).isNotNull();
        assertThat(gv.strategy()).isEqualTo(GenerationType.IDENTITY);
    }

    // -------- name ----------
    @Test
    @DisplayName("name con @Column(nullable=false, length=100)")
    void nameMapping_ok() throws Exception {
        Field f = Habit.class.getDeclaredField("name");
        Column c = getAnn(f, Column.class);
        assertThat(c).isNotNull();
        assertThat(c.nullable()).isFalse();
        assertThat(c.length()).isEqualTo(100);
    }

    // -------- category ----------
    @Test
    @DisplayName("category con @Enumerated(STRING) y @Column(nullable=false, length=20)")
    void categoryMapping_ok() throws Exception {
        Field f = Habit.class.getDeclaredField("category");
        Enumerated en = getAnn(f, Enumerated.class);
        assertThat(en).isNotNull();
        assertThat(en.value()).isEqualTo(EnumType.STRING);

        Column c = getAnn(f, Column.class);
        assertThat(c).isNotNull();
        assertThat(c.nullable()).isFalse();
        assertThat(c.length()).isEqualTo(20);
    }

    // -------- description ----------
    @Test
    @DisplayName("description con @Column(columnDefinition=\"TEXT\")")
    void descriptionMapping_ok() throws Exception {
        Field f = Habit.class.getDeclaredField("description");
        Column c = getAnn(f, Column.class);
        assertThat(c).isNotNull();
        // nullable por defecto = true
        assertThat(c.nullable()).isTrue();
        // valida el columnDefinition
        assertThat(c.columnDefinition()).isEqualTo("TEXT");
    }

    // -------- users (ManyToMany mapeado por 'habits') ----------
    @Test
    @DisplayName("users con @ManyToMany(mappedBy=\"habits\", LAZY)")
    void usersMapping_ok() throws Exception {
        Field f = Habit.class.getDeclaredField("users");
        ManyToMany m2m = getAnn(f, ManyToMany.class);
        assertThat(m2m).isNotNull();
        assertThat(m2m.mappedBy()).isEqualTo("habits");
        assertThat(m2m.fetch()).isEqualTo(FetchType.LAZY);

        // Tipo de la colección
        assertThat(Set.class.isAssignableFrom(f.getType())).isTrue();
    }

    // -------- routineActivities ----------
    @Test
    @DisplayName("routineActivities con @OneToMany(mappedBy=\"habit\", cascade=ALL, orphanRemoval=true)")
    void routineActivitiesMapping_ok() throws Exception {
        Field f = Habit.class.getDeclaredField("routineActivities");
        OneToMany o2m = getAnn(f, OneToMany.class);
        assertThat(o2m).isNotNull();
        assertThat(o2m.mappedBy()).isEqualTo("habit");
        assertThat(o2m.cascade()).contains(CascadeType.ALL);
        assertThat(o2m.orphanRemoval()).isTrue();

        assertThat(List.class.isAssignableFrom(f.getType())).isTrue();
    }

    // -------- completedActivities ----------
    @Test
    @DisplayName("completedActivities con @OneToMany(mappedBy=\"habit\", cascade=ALL, orphanRemoval=true)")
    void completedActivitiesMapping_ok() throws Exception {
        Field f = Habit.class.getDeclaredField("completedActivities");
        OneToMany o2m = getAnn(f, OneToMany.class);
        assertThat(o2m).isNotNull();
        assertThat(o2m.mappedBy()).isEqualTo("habit");
        assertThat(o2m.cascade()).contains(CascadeType.ALL);
        assertThat(o2m.orphanRemoval()).isTrue();

        assertThat(List.class.isAssignableFrom(f.getType())).isTrue();
    }

    // -------- reminders ----------
    @Test
    @DisplayName("reminders con @OneToMany(mappedBy=\"habit\", cascade=ALL, orphanRemoval=true)")
    void remindersMapping_ok() throws Exception {
        Field f = Habit.class.getDeclaredField("reminders");
        OneToMany o2m = getAnn(f, OneToMany.class);
        assertThat(o2m).isNotNull();
        assertThat(o2m.mappedBy()).isEqualTo("habit");
        assertThat(o2m.cascade()).contains(CascadeType.ALL);
        assertThat(o2m.orphanRemoval()).isTrue();

        assertThat(List.class.isAssignableFrom(f.getType())).isTrue();
    }

    // -------- guides (ManyToMany mapeado por 'recommendedHabits') ----------
    @Test
    @DisplayName("guides con @ManyToMany(mappedBy=\"recommendedHabits\", LAZY)")
    void guidesMapping_ok() throws Exception {
        Field f = Habit.class.getDeclaredField("guides");
        ManyToMany m2m = getAnn(f, ManyToMany.class);
        assertThat(m2m).isNotNull();
        assertThat(m2m.mappedBy()).isEqualTo("recommendedHabits");
        assertThat(m2m.fetch()).isEqualTo(FetchType.LAZY);

        assertThat(Set.class.isAssignableFrom(f.getType())).isTrue();
    }
}
