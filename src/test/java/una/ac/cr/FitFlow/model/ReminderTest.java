package una.ac.cr.FitFlow.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.persistence.*;

class ReminderTest {

    // ---- Helpers ----
    private <A extends Annotation> A getAnn(Field f, Class<A> ann) { return f.getAnnotation(ann); }
    private <A extends Annotation> A getTypeAnn(Class<?> c, Class<A> ann) { return c.getAnnotation(ann); }

    // ---- POJO / Lombok ----
    @Test
    @DisplayName("Builder, getters/setters funcionan")
    void builderAndAccessors_ok() {
        User user = new User();
        Habit habit = new Habit();
        OffsetDateTime now = OffsetDateTime.now();

        Reminder r = Reminder.builder()
                .id(3L)
                .user(user)
                .habit(habit)
                .message("Levántate temprano")
                .time(now)
                .frequency(Reminder.Frequency.DAILY)
                .build();

        assertThat(r.getId()).isEqualTo(3L);
        assertThat(r.getUser()).isSameAs(user);
        assertThat(r.getHabit()).isSameAs(habit);
        assertThat(r.getMessage()).isEqualTo("Levántate temprano");
        assertThat(r.getTime()).isEqualTo(now);
        assertThat(r.getFrequency()).isEqualTo(Reminder.Frequency.DAILY);

        // setters
        r.setMessage("Bebe agua");
        assertThat(r.getMessage()).isEqualTo("Bebe agua");
    }

    // ---- Clase ----
    @Test
    @DisplayName("@Entity y @Table(name=\"reminder\") presentes")
    void classAnnotations_ok() {
        assertThat(getTypeAnn(Reminder.class, Entity.class)).isNotNull();

        Table t = getTypeAnn(Reminder.class, Table.class);
        assertThat(t).isNotNull();
        assertThat(t.name()).isEqualTo("reminder");
    }

    // ---- id ----
    @Test
    @DisplayName("id con @Id y @GeneratedValue(IDENTITY)")
    void idMapping_ok() throws Exception {
        Field f = Reminder.class.getDeclaredField("id");
        assertThat(getAnn(f, Id.class)).isNotNull();

        GeneratedValue gv = getAnn(f, GeneratedValue.class);
        assertThat(gv).isNotNull();
        assertThat(gv.strategy()).isEqualTo(GenerationType.IDENTITY);
    }

    // ---- user ----
    @Test
    @DisplayName("user con @ManyToOne(fetch=LAZY, optional=false) y @JoinColumn(name=\"user_id\", nullable=false)")
    void userMapping_ok() throws Exception {
        Field f = Reminder.class.getDeclaredField("user");

        ManyToOne m2o = getAnn(f, ManyToOne.class);
        assertThat(m2o).isNotNull();
        assertThat(m2o.fetch()).isEqualTo(FetchType.LAZY);
        assertThat(m2o.optional()).isFalse();

        JoinColumn jc = getAnn(f, JoinColumn.class);
        assertThat(jc).isNotNull();
        assertThat(jc.name()).isEqualTo("user_id");
        assertThat(jc.nullable()).isFalse();
    }

    // ---- habit ----
    @Test
    @DisplayName("habit con @ManyToOne(fetch=LAZY, optional=false) y @JoinColumn(name=\"habit_id\", nullable=false)")
    void habitMapping_ok() throws Exception {
        Field f = Reminder.class.getDeclaredField("habit");

        ManyToOne m2o = getAnn(f, ManyToOne.class);
        assertThat(m2o).isNotNull();
        assertThat(m2o.fetch()).isEqualTo(FetchType.LAZY);
        assertThat(m2o.optional()).isFalse();

        JoinColumn jc = getAnn(f, JoinColumn.class);
        assertThat(jc).isNotNull();
        assertThat(jc.name()).isEqualTo("habit_id");
        assertThat(jc.nullable()).isFalse();
    }

    // ---- message ----
    @Test
    @DisplayName("message con @Column(nullable=false, length=255)")
    void messageMapping_ok() throws Exception {
        Field f = Reminder.class.getDeclaredField("message");

        Column c = getAnn(f, Column.class);
        assertThat(c).isNotNull();
        assertThat(c.nullable()).isFalse();
        assertThat(c.length()).isEqualTo(255);
    }

    // ---- time ----
    @Test
    @DisplayName("time con @Column(nullable=false)")
    void timeMapping_ok() throws Exception {
        Field f = Reminder.class.getDeclaredField("time");

        Column c = getAnn(f, Column.class);
        assertThat(c).isNotNull();
        assertThat(c.nullable()).isFalse();
    }

    // ---- frequency ----
    @Test
    @DisplayName("frequency con @Enumerated(STRING) y @Column(nullable=false, length=10)")
    void frequencyMapping_ok() throws Exception {
        Field f = Reminder.class.getDeclaredField("frequency");

        Enumerated en = getAnn(f, Enumerated.class);
        assertThat(en).isNotNull();
        assertThat(en.value()).isEqualTo(EnumType.STRING);

        Column c = getAnn(f, Column.class);
        assertThat(c).isNotNull();
        assertThat(c.nullable()).isFalse();
        assertThat(c.length()).isEqualTo(10);
    }
}
