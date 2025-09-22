package una.ac.cr.FitFlow.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.persistence.*;

class ProgressLogTest {

    // -------- Helpers ----------
    private <A extends Annotation> A getAnn(Field f, Class<A> ann) { return f.getAnnotation(ann); }
    private <A extends Annotation> A getTypeAnn(Class<?> c, Class<A> ann) { return c.getAnnotation(ann); }

    // -------- POJO / Lombok ----------
    @Test
    @DisplayName("Builder, getters/setters funcionan")
    void builderAndAccessors_ok() {
        User user = new User();
        Routine routine = new Routine();
        OffsetDateTime now = OffsetDateTime.now();

        ProgressLog pl = ProgressLog.builder()
                .id(7L)
                .user(user)
                .routine(routine)
                .logDate(now)
                .build();

        assertThat(pl.getId()).isEqualTo(7L);
        assertThat(pl.getUser()).isSameAs(user);
        assertThat(pl.getRoutine()).isSameAs(routine);
        assertThat(pl.getLogDate()).isEqualTo(now);

        // setters
        Routine r2 = new Routine();
        pl.setRoutine(r2);
        assertThat(pl.getRoutine()).isSameAs(r2);
    }

    // -------- Anotaciones de clase ----------
    @Test
    @DisplayName("@Entity y @Table(name=\"progress_log\") presentes")
    void classAnnotations_ok() {
        assertThat(getTypeAnn(ProgressLog.class, Entity.class)).isNotNull();

        Table t = getTypeAnn(ProgressLog.class, Table.class);
        assertThat(t).isNotNull();
        assertThat(t.name()).isEqualTo("progress_log");
    }

    // -------- id ----------
    @Test
    @DisplayName("id con @Id y @GeneratedValue(IDENTITY)")
    void idMapping_ok() throws Exception {
        Field f = ProgressLog.class.getDeclaredField("id");
        assertThat(getAnn(f, Id.class)).isNotNull();

        GeneratedValue gv = getAnn(f, GeneratedValue.class);
        assertThat(gv).isNotNull();
        assertThat(gv.strategy()).isEqualTo(GenerationType.IDENTITY);
    }

    // -------- user ----------
    @Test
    @DisplayName("user con @ManyToOne(fetch=LAZY, optional=false) y @JoinColumn(name=\"user_id\", nullable=false)")
    void userMapping_ok() throws Exception {
        Field f = ProgressLog.class.getDeclaredField("user");

        ManyToOne m2o = getAnn(f, ManyToOne.class);
        assertThat(m2o).isNotNull();
        assertThat(m2o.fetch()).isEqualTo(FetchType.LAZY);
        assertThat(m2o.optional()).isFalse();

        JoinColumn jc = getAnn(f, JoinColumn.class);
        assertThat(jc).isNotNull();
        assertThat(jc.name()).isEqualTo("user_id");
        assertThat(jc.nullable()).isFalse();
    }

    // -------- routine ----------
    @Test
    @DisplayName("routine con @ManyToOne(fetch=LAZY, optional=false) y @JoinColumn(name=\"routine_id\", nullable=false)")
    void routineMapping_ok() throws Exception {
        Field f = ProgressLog.class.getDeclaredField("routine");

        ManyToOne m2o = getAnn(f, ManyToOne.class);
        assertThat(m2o).isNotNull();
        assertThat(m2o.fetch()).isEqualTo(FetchType.LAZY);
        assertThat(m2o.optional()).isFalse();

        JoinColumn jc = getAnn(f, JoinColumn.class);
        assertThat(jc).isNotNull();
        assertThat(jc.name()).isEqualTo("routine_id");
        assertThat(jc.nullable()).isFalse();
    }

    // -------- logDate ----------
    @Test
    @DisplayName("logDate con @Column(name=\"log_date\", nullable=false)")
    void logDateMapping_ok() throws Exception {
        Field f = ProgressLog.class.getDeclaredField("logDate");

        Column c = getAnn(f, Column.class);
        assertThat(c).isNotNull();
        assertThat(c.name()).isEqualTo("log_date");
        assertThat(c.nullable()).isFalse();
    }

    // -------- completedActivities ----------
    @Test
    @DisplayName("completedActivities con @OneToMany(mappedBy=\"progressLog\", cascade=ALL, orphanRemoval=true)")
    void completedActivitiesMapping_ok() throws Exception {
        Field f = ProgressLog.class.getDeclaredField("completedActivities");

        OneToMany o2m = getAnn(f, OneToMany.class);
        assertThat(o2m).isNotNull();
        assertThat(o2m.mappedBy()).isEqualTo("progressLog");
        assertThat(o2m.cascade()).contains(CascadeType.ALL);
        assertThat(o2m.orphanRemoval()).isTrue();

        assertThat(List.class.isAssignableFrom(f.getType())).isTrue();
    }
}
