package una.ac.cr.FitFlow.model;

    import static org.assertj.core.api.Assertions.assertThat;

    import java.lang.annotation.Annotation;
    import java.lang.reflect.Field;
    import java.lang.reflect.ParameterizedType;
    import java.util.List;

    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;

    import jakarta.persistence.*;

    class RoutineTest {

        // ---------- Helpers ----------
        private <A extends Annotation> A getAnn(Field f, Class<A> ann) {
            return f.getAnnotation(ann);
        }

        private <A extends Annotation> A getTypeAnn(Class<?> c, Class<A> ann) {
            return c.getAnnotation(ann);
        }

        // ---------- POJO / Lombok ----------
        @Test
        @DisplayName("Builder, getters/setters y @Builder.Default funcionan")
        void builderAndAccessors_ok() {
            User user = new User();

            Routine r = Routine.builder()
                    .id(42L)
                    .title("Rutina semanal")
                    .user(user)
                    .daysOfWeek(List.of(Routine.DaysOfWeek.MON, Routine.DaysOfWeek.WED, Routine.DaysOfWeek.FRI))
                    // no pasamos activities/progressLogs: deben inicializarse vacíos por @Builder.Default
                    .build();

            assertThat(r.getId()).isEqualTo(42L);
            assertThat(r.getTitle()).isEqualTo("Rutina semanal");
            assertThat(r.getUser()).isSameAs(user);
            assertThat(r.getDaysOfWeek()).containsExactly(
                    Routine.DaysOfWeek.MON, Routine.DaysOfWeek.WED, Routine.DaysOfWeek.FRI);

            assertThat(r.getActivities()).isNotNull().isEmpty();
            assertThat(r.getProgressLogs()).isNotNull().isEmpty();

            // setters básicos
            r.setTitle("Rutina actualizada");
            assertThat(r.getTitle()).isEqualTo("Rutina actualizada");
        }

        // ---------- Anotaciones de clase ----------
        @Test
        @DisplayName("@Entity y @Table(name=\"routines\") presentes")
        void classAnnotations_ok() {
            assertThat(getTypeAnn(Routine.class, Entity.class)).isNotNull();

            Table t = getTypeAnn(Routine.class, Table.class);
            assertThat(t).isNotNull();
            assertThat(t.name()).isEqualTo("routines");
        }

        // ---------- id ----------
        @Test
        @DisplayName("id con @Id y @GeneratedValue(IDENTITY)")
        void idMapping_ok() throws Exception {
            Field f = Routine.class.getDeclaredField("id");
            assertThat(getAnn(f, Id.class)).isNotNull();

            GeneratedValue gv = getAnn(f, GeneratedValue.class);
            assertThat(gv).isNotNull();
            assertThat(gv.strategy()).isEqualTo(GenerationType.IDENTITY);
        }

        // ---------- title ----------
        @Test
        @DisplayName("title con @Column(nullable=false, length=150)")
        void titleMapping_ok() throws Exception {
            Field f = Routine.class.getDeclaredField("title");
            Column c = getAnn(f, Column.class);
            assertThat(c).isNotNull();
            assertThat(c.nullable()).isFalse();
            assertThat(c.length()).isEqualTo(150);
        }

        // ---------- user ----------
        @Test
        @DisplayName("user con @ManyToOne(fetch=LAZY, optional=false) y @JoinColumn(name=\"user_id\", nullable=false)")
        void userMapping_ok() throws Exception {
            Field f = Routine.class.getDeclaredField("user");

            ManyToOne m2o = getAnn(f, ManyToOne.class);
            assertThat(m2o).isNotNull();
            assertThat(m2o.fetch()).isEqualTo(FetchType.LAZY);
            assertThat(m2o.optional()).isFalse();

            JoinColumn jc = getAnn(f, JoinColumn.class);
            assertThat(jc).isNotNull();
            assertThat(jc.name()).isEqualTo("user_id");
            assertThat(jc.nullable()).isFalse();
        }

        // ---------- daysOfWeek ----------
        @Test
        @DisplayName("daysOfWeek mapeado con @Column(name=\"days_of_week\", nullable=false) y tipo List<DaysOfWeek>")
        void daysOfWeekMapping_ok() throws Exception {
            Field f = Routine.class.getDeclaredField("daysOfWeek");

            // Tipo del campo: List<?>
            assertThat(List.class.isAssignableFrom(f.getType())).isTrue();

            // Verifica que el parámetro genérico sea Routine.DaysOfWeek
            var gt = f.getGenericType();
            assertThat(gt).isInstanceOf(ParameterizedType.class);
            var p = (ParameterizedType) gt;
            var arg = p.getActualTypeArguments()[0];

            String expectedInnerName = Routine.DaysOfWeek.class.getName(); // "una.ac.cr.FitFlow.model.Routine$DaysOfWeek"
            assertThat(arg.getTypeName()).isEqualTo(expectedInnerName);

            // Verifica @Column según el diseño actual
            Column col = f.getAnnotation(Column.class);
            assertThat(col).isNotNull();
            assertThat(col.name()).isEqualTo("days_of_week");
            assertThat(col.nullable()).isFalse();

            // Asegura que NO se esté usando ElementCollection/CollectionTable en este diseño
            assertThat(f.getAnnotation(ElementCollection.class)).isNull();
            assertThat(f.getAnnotation(CollectionTable.class)).isNull();
        }

        // ---------- activities ----------
        @Test
        @DisplayName("activities con @OneToMany(mappedBy=\"routine\", cascade=ALL, orphanRemoval=true)")
        void activitiesMapping_ok() throws Exception {
            Field f = Routine.class.getDeclaredField("activities");
            OneToMany o2m = getAnn(f, OneToMany.class);
            assertThat(o2m).isNotNull();
            assertThat(o2m.mappedBy()).isEqualTo("routine");
            assertThat(o2m.cascade()).contains(CascadeType.ALL);
            assertThat(o2m.orphanRemoval()).isTrue();

            assertThat(List.class.isAssignableFrom(f.getType())).isTrue();
        }

        // ---------- progressLogs ----------
        @Test
        @DisplayName("progressLogs con @OneToMany(mappedBy=\"routine\", cascade=ALL, orphanRemoval=true)")
        void progressLogsMapping_ok() throws Exception {
            Field f = Routine.class.getDeclaredField("progressLogs");
            OneToMany o2m = getAnn(f, OneToMany.class);
            assertThat(o2m).isNotNull();
            assertThat(o2m.mappedBy()).isEqualTo("routine");
            assertThat(o2m.cascade()).contains(CascadeType.ALL);
            assertThat(o2m.orphanRemoval()).isTrue();

            assertThat(List.class.isAssignableFrom(f.getType())).isTrue();
        }
    }