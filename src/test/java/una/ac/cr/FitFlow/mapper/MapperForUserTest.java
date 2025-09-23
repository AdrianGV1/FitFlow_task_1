package una.ac.cr.FitFlow.mapper;



import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;

import una.ac.cr.FitFlow.dto.User.UserOutputDTO;

import una.ac.cr.FitFlow.model.Habit;

import una.ac.cr.FitFlow.model.Role;

import una.ac.cr.FitFlow.model.User;



import java.util.Set;



import static org.junit.jupiter.api.Assertions.*;



class MapperForUserTest {



    private final MapperForUser mapper = new MapperForUser();



    private Role role(Long id, String name) {

        Role r = new Role();

        r.setId(id);

        // IMPORTANTE: si equals/hashCode usan 'name', esto evita colisiones en el Set

        try {

            Role.class.getMethod("setName", String.class).invoke(r, name);

        } catch (Exception ignored) { /* si Role no tiene name, no pasa nada */ }

        return r;

    }



    private Habit habit(Long id, String name) {

        Habit h = new Habit();

        h.setId(id);

        h.setName(name); // Habit sí tiene name

        return h;

    }


/* 
    @Test

    @DisplayName("toDto: mapea id/username/email y colecciones a sets de IDs")

    void toDto_mapsAllFields() {

        // given

        User u = new User();

        u.setId(42L);

        u.setUsername("neo");

        u.setEmail("neo@matrix.io");



        // Aseguramos objetos distintos según equals/hashCode de tus entidades

        u.getRoles().add(role(1L, "ROLE_USER"));

        u.getRoles().add(role(2L, "ROLE_ADMIN"));



        u.getHabits().add(habit(10L, "Caminar"));

        u.getHabits().add(habit(20L, "Dormir"));



        // when

        UserOutputDTO dto = mapper.toDto(u);



        // then

        assertNotNull(dto);

        assertEquals(42L, dto.getId());

        assertEquals("neo", dto.getUsername());

        assertEquals("neo@matrix.io", dto.getEmail());



        // sets de IDs (orden no importa)

        assertEquals(Set.of(1L, 2L), dto.getRoleIds());

        assertEquals(Set.of(10L, 20L), dto.getHabitIds());

    }

*/

    @Test

    @DisplayName("toDto: colecciones vacías → sets vacíos en el DTO")

    void toDto_emptyCollections() {

        User u = new User();

        u.setId(7L);

        u.setUsername("trinity");

        u.setEmail("trinity@matrix.io");



        UserOutputDTO dto = mapper.toDto(u);



        assertNotNull(dto);

        assertEquals(7L, dto.getId());

        assertEquals("trinity", dto.getUsername());

        assertEquals("trinity@matrix.io", dto.getEmail());



        assertNotNull(dto.getRoleIds());

        assertTrue(dto.getRoleIds().isEmpty());



        assertNotNull(dto.getHabitIds());

        assertTrue(dto.getHabitIds().isEmpty());

    }

}