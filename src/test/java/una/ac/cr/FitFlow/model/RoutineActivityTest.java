package una.ac.cr.FitFlow.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.persistence.*;

class RoutineActivityTest {

    // ---- Helpers ----
    private <A extends Annotation> A getAnn(Field f, Class<A> ann) { return f.getAnnotation(ann); }
    private <A extends Annotation> A getTypeAnn(Class<?> c, Class<A> ann) { return c.getAnnotation(ann); }

    // ---- POJO / Lombok ----
    @Test
    @DisplayName("Builder, getters/setters funcionan")
    void builderAndAccessors_ok() {
        Routine routine = new Routine();
        Habit habit = new Habit();

        RoutineActivity ra = RoutineActivity.builder()
                .id(77L)
                .routine(routine)
                .habit(habit)
                .duration(30)
                .notes("Hacer cardio")
                .build();

        assertThat(ra.getId()).isEqualTo(77L);
        assertThat(ra.getRoutine()).isSameAs(routine);
        assertThat(ra.getHabit()).isSameAs(habit);
        assertThat(ra.getDuration()).isEqualTo(30);
        assertThat(ra.getNotes()).isEqualTo("Hacer cardio");

        // setters
        ra.setDuration(45);
        assertThat(ra.getDuration()).isEqualTo(45);
    }

    // ---- Clase ----
    @Test
    @DisplayName("@Entity y @Table(name=\"routine_activities\") con unique constraint")
    void classAnnotations_ok() {
        assertThat(getTypeAnn(RoutineActivity.class, Entity.class)).isNotNull();

        Table t = getTypeAnn(RoutineActivity.class, Table.class);
        assertThat(t).isNotNull();
        assertThat(t.name()).isEqualTo("routine_activities");

        UniqueConstraint[] ucs = t.uniqueConstraints();
        boolean found = false;
        for (UniqueConstraint uc : ucs) {
            if ("uk_routine_habit".equals(uc.name())) {
                assertThat(uc.columnNames()).containsExactlyInAnyOrder("routine_id", "habit_id");
                found = true;
            }
        }
        assertThat(found).as("UniqueConstraint uk_routine_habit no encontrado").isTrue();
    }

    // ---- id ----
    @Test
    @DisplayName("id con @Id y @GeneratedValue(IDENTITY)")
    void idMapping_ok() throws Exception {
        Field f = RoutineActivity.class.getDeclaredField("id");
        assertThat(getAnn(f, Id.class)).isNotNull();

        GeneratedValue gv = getAnn(f, GeneratedValue.class);
        assertThat(gv).isNotNull();
        assertThat(gv.strategy()).isEqualTo(GenerationType.IDENTITY);
    }

    // ---- routine ----
    @Test
    @DisplayName("routine con @ManyToOne(fetch=LAZY, optional=false) y @JoinColumn(name=\"routine_id\", nullable=false)")
    void routineMapping_ok() throws Exception {
        Field f = RoutineActivity.class.getDeclaredField("routine");

        ManyToOne m2o = getAnn(f, ManyToOne.class);
        assertThat(m2o).isNotNull();
        assertThat(m2o.fetch()).isEqualTo(FetchType.LAZY);
        assertThat(m2o.optional()).isFalse();

        JoinColumn jc = getAnn(f, JoinColumn.class);
        assertThat(jc).isNotNull();
        assertThat(jc.name()).isEqualTo("routine_id");
        assertThat(jc.nullable()).isFalse();
    }

    // ---- habit ----
    @Test
    @DisplayName("habit con @ManyToOne(fetch=LAZY, optional=false) y @JoinColumn(name=\"habit_id\", nullable=false)")
    void habitMapping_ok() throws Exception {
        Field f = RoutineActivity.class.getDeclaredField("habit");

        ManyToOne m2o = getAnn(f, ManyToOne.class);
        assertThat(m2o).isNotNull();
        assertThat(m2o.fetch()).isEqualTo(FetchType.LAZY);
        assertThat(m2o.optional()).isFalse();

        JoinColumn jc = getAnn(f, JoinColumn.class);
        assertThat(jc).isNotNull();
        assertThat(jc.name()).isEqualTo("habit_id");
        assertThat(jc.nullable()).isFalse();
    }

    // ---- duration ----
    @Test
    @DisplayName("duration con @Column(nullable=false)")
    void durationMapping_ok() throws Exception {
        Field f = RoutineActivity.class.getDeclaredField("duration");

        Column c = getAnn(f, Column.class);
        assertThat(c).isNotNull();
        assertThat(c.nullable()).isFalse();
    }

    // ---- notes ----
    @Test
    @DisplayName("notes con @Column(length=500)")
    void notesMapping_ok() throws Exception {
        Field f = RoutineActivity.class.getDeclaredField("notes");

        Column c = getAnn(f, Column.class);
        assertThat(c).isNotNull();
        assertThat(c.length()).isEqualTo(500);
        // por defecto nullable = true
        assertThat(c.nullable()).isTrue();
    }
}
