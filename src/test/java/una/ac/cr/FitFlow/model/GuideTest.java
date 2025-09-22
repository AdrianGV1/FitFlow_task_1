package una.ac.cr.FitFlow.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.persistence.*;

class GuideTest {

    // -------- Helpers ----------
    private <A extends Annotation> A getAnn(Field f, Class<A> ann) { return f.getAnnotation(ann); }
    private <A extends Annotation> A getTypeAnn(Class<?> c, Class<A> ann) { return c.getAnnotation(ann); }

    // -------- POJO / Lombok ----------
    @Test
    @DisplayName("Builder, getters/setters y @Builder.Default funcionan")
    void builderAndAccessors_ok() {
        Guide.Category cat = Guide.Category.PHYSICAL;
        Set<Habit> initial = new HashSet<>();
        Habit h = new Habit();
        initial.add(h);

        Guide g = Guide.builder()
                .id(10L)
                .title("Rutina HIIT")
                .content("Contenido extenso...")
                .category(cat)
                .recommendedHabits(initial)
                .build();

        assertThat(g.getId()).isEqualTo(10L);
        assertThat(g.getTitle()).isEqualTo("Rutina HIIT");
        assertThat(g.getContent()).isEqualTo("Contenido extenso...");
        assertThat(g.getCategory()).isEqualTo(cat);
        assertThat(g.getRecommendedHabits()).containsExactly(h);

        // Setters
        g.setTitle("Nueva guía");
        assertThat(g.getTitle()).isEqualTo("Nueva guía");

        // Verifica @Builder.Default cuando no se pasa el set
        Guide g2 = Guide.builder()
                .title("Solo título")
                .content("x")
                .category(Guide.Category.DIET)
                .build();
        assertThat(g2.getRecommendedHabits()).isNotNull();
        assertThat(g2.getRecommendedHabits()).isEmpty();

        // Mutabilidad del set
        Habit h2 = new Habit();
        g2.getRecommendedHabits().add(h2);
        assertThat(g2.getRecommendedHabits()).hasSize(1).contains(h2);
    }

    @Test
    @DisplayName("@ToString(onlyExplicitlyIncluded=true) no incluye campos por defecto")
    void toString_noFieldsIncludedByDefault() {
        Guide g = Guide.builder()
                .title("Secreto")
                .content("No debe salir")
                .category(Guide.Category.MENTAL)
                .build();

        String s = g.toString();
        assertThat(s).contains("Guide");       // clase
        assertThat(s).doesNotContain("Secreto"); // no filtra title sin @ToString.Include
        assertThat(s).doesNotContain("No debe salir");
    }

    // -------- Anotaciones de clase ----------
    @Test
    @DisplayName("@Entity y @Table(name=\"guides\") presentes")
    void classAnnotations_ok() {
        Entity e = getTypeAnn(Guide.class, Entity.class);
        assertThat(e).isNotNull();

        Table t = getTypeAnn(Guide.class, Table.class);
        assertThat(t).isNotNull();
        assertThat(t.name()).isEqualTo("guides");
    }

    // -------- id ----------
    @Test
    @DisplayName("id con @Id y @GeneratedValue(IDENTITY)")
    void idMapping_ok() throws Exception {
        Field f = Guide.class.getDeclaredField("id");
        assertThat(getAnn(f, Id.class)).isNotNull();
        GeneratedValue gv = getAnn(f, GeneratedValue.class);
        assertThat(gv).isNotNull();
        assertThat(gv.strategy()).isEqualTo(GenerationType.IDENTITY);
    }

    // -------- title ----------
    @Test
    @DisplayName("title con @Column(nullable=false, length=150)")
    void titleMapping_ok() throws Exception {
        Field f = Guide.class.getDeclaredField("title");
        Column c = getAnn(f, Column.class);
        assertThat(c).isNotNull();
        assertThat(c.nullable()).isFalse();
        assertThat(c.length()).isEqualTo(150);
    }

    // -------- content ----------
    @Test
    @DisplayName("content con @Column(nullable=false, columnDefinition=\"TEXT\")")
    void contentMapping_ok() throws Exception {
        Field f = Guide.class.getDeclaredField("content");
        Column c = getAnn(f, Column.class);
        assertThat(c).isNotNull();
        assertThat(c.nullable()).isFalse();
        // Si cambias el dialecto y columnDefinition, ajusta este assert:
        assertThat(c.columnDefinition()).isEqualTo("TEXT");
    }

    // -------- category ----------
    @Test
    @DisplayName("category con @Enumerated(STRING) y @Column(nullable=false, length=10)")
    void categoryMapping_ok() throws Exception {
        Field f = Guide.class.getDeclaredField("category");
        Enumerated en = getAnn(f, Enumerated.class);
        assertThat(en).isNotNull();
        assertThat(en.value()).isEqualTo(EnumType.STRING);

        Column c = getAnn(f, Column.class);
        assertThat(c).isNotNull();
        assertThat(c.nullable()).isFalse();
        assertThat(c.length()).isEqualTo(10);
    }

    // -------- recommendedHabits ----------
    @Test
    @DisplayName("@ManyToMany LAZY con @JoinTable guide_recommended_habit (guide_id, habit_id)")
    void recommendedHabitsMapping_ok() throws Exception {
        Field f = Guide.class.getDeclaredField("recommendedHabits");

        ManyToMany m2m = getAnn(f, ManyToMany.class);
        assertThat(m2m).isNotNull();
        assertThat(m2m.fetch()).isEqualTo(FetchType.LAZY);

        JoinTable jt = getAnn(f, JoinTable.class);
        assertThat(jt).isNotNull();
        assertThat(jt.name()).isEqualTo("guide_recommended_habit");

        // joinColumns
        assertThat(jt.joinColumns()).hasSize(1);
        assertThat(jt.joinColumns()[0].name()).isEqualTo("guide_id");

        // inverseJoinColumns
        assertThat(jt.inverseJoinColumns()).hasSize(1);
        assertThat(jt.inverseJoinColumns()[0].name()).isEqualTo("habit_id");
    }
}
