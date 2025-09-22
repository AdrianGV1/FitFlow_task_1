package una.ac.cr.FitFlow.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.persistence.*;

class CompletedActivityTest {

    // ---------- Helpers ----------
    private <A extends Annotation> A getAnnotation(Field f, Class<A> ann) {
        return f.getAnnotation(ann);
    }
    private <A extends Annotation> A getTypeAnnotation(Class<?> c, Class<A> ann) {
        return c.getAnnotation(ann);
    }

    // ---------- POJO / Lombok ----------
    @Test
    @DisplayName("Builder y getters/setters funcionan")
    void builderAndAccessors_ok() {
        ProgressLog pl = new ProgressLog(); // instancia vacía basta para POJO test
        Habit habit = new Habit();

        OffsetDateTime now = OffsetDateTime.now();

        CompletedActivity ca = CompletedActivity.builder()
                .id(123L)
                .completedAt(now)
                .notes("nota de prueba")
                .progressLog(pl)
                .habit(habit)
                .build();

        // Getters (Lombok @Getter)
        assertThat(ca.getId()).isEqualTo(123L);
        assertThat(ca.getCompletedAt()).isEqualTo(now);
        assertThat(ca.getNotes()).isEqualTo("nota de prueba");
        assertThat(ca.getProgressLog()).isSameAs(pl);
        assertThat(ca.getHabit()).isSameAs(habit);

        // Setters (Lombok @Setter)
        ca.setNotes("otra nota");
        assertThat(ca.getNotes()).isEqualTo("otra nota");
    }

    // ---------- Anotaciones de Clase ----------
    @Test
    @DisplayName("@Entity y @Table(name = \"completed_activities\") presentes")
    void classAnnotations_ok() {
        Class<CompletedActivity> clazz = CompletedActivity.class;

        Entity entity = getTypeAnnotation(clazz, Entity.class);
        assertThat(entity).as("@Entity no encontrada").isNotNull();

        Table table = getTypeAnnotation(clazz, Table.class);
        assertThat(table).as("@Table no encontrada").isNotNull();
        assertThat(table.name()).isEqualTo("completed_activities");
    }

    // ---------- Campo id ----------
    @Test
    @DisplayName("id con @Id y @GeneratedValue(strategy = IDENTITY)")
    void idMapping_ok() throws Exception {
        Field id = CompletedActivity.class.getDeclaredField("id");

        Id idAnn = getAnnotation(id, Id.class);
        assertThat(idAnn).as("@Id no encontrada en id").isNotNull();

        GeneratedValue gen = getAnnotation(id, GeneratedValue.class);
        assertThat(gen).as("@GeneratedValue no encontrada en id").isNotNull();
        assertThat(gen.strategy()).isEqualTo(GenerationType.IDENTITY);
    }

    // ---------- Campo completedAt ----------
    @Test
    @DisplayName("completedAt con @Column(nullable=false)")
    void completedAtMapping_ok() throws Exception {
        Field f = CompletedActivity.class.getDeclaredField("completedAt");

        Column col = getAnnotation(f, Column.class);
        assertThat(col).as("@Column no encontrada en completedAt").isNotNull();
        assertThat(col.nullable()).isFalse();
    }

    // ---------- Campo notes ----------
    @Test
    @DisplayName("notes con @Column(length=500) y nullable por defecto")
    void notesMapping_ok() throws Exception {
        Field f = CompletedActivity.class.getDeclaredField("notes");

        Column col = getAnnotation(f, Column.class);
        assertThat(col).as("@Column no encontrada en notes").isNotNull();
        assertThat(col.length()).isEqualTo(500);
        // Por defecto nullable=true si no se especifica
        assertThat(col.nullable()).isTrue();
    }

    // ---------- Relación progressLog ----------
    @Test
    @DisplayName("progressLog con @ManyToOne(fetch=LAZY) y @JoinColumn(nullable=false, name=\"progress_log_id\")")
    void progressLogMapping_ok() throws Exception {
        Field f = CompletedActivity.class.getDeclaredField("progressLog");

        ManyToOne m2o = getAnnotation(f, ManyToOne.class);
        assertThat(m2o).as("@ManyToOne no encontrada en progressLog").isNotNull();
        assertThat(m2o.fetch()).isEqualTo(FetchType.LAZY);

        JoinColumn jc = getAnnotation(f, JoinColumn.class);
        assertThat(jc).as("@JoinColumn no encontrada en progressLog").isNotNull();
        assertThat(jc.name()).isEqualTo("progress_log_id");
        assertThat(jc.nullable()).isFalse();
    }

    // ---------- Relación habit ----------
    @Test
    @DisplayName("habit con @ManyToOne(fetch=LAZY) y @JoinColumn(nullable=false, name=\"habit_id\")")
    void habitMapping_ok() throws Exception {
        Field f = CompletedActivity.class.getDeclaredField("habit");

        ManyToOne m2o = getAnnotation(f, ManyToOne.class);
        assertThat(m2o).as("@ManyToOne no encontrada en habit").isNotNull();
        assertThat(m2o.fetch()).isEqualTo(FetchType.LAZY);

        JoinColumn jc = getAnnotation(f, JoinColumn.class);
        assertThat(jc).as("@JoinColumn no encontrada en habit").isNotNull();
        assertThat(jc.name()).isEqualTo("habit_id");
        assertThat(jc.nullable()).isFalse();
    }
}
