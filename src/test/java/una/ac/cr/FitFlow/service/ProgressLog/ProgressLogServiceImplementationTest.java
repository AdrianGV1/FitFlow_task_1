package una.ac.cr.FitFlow.service.ProgressLog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogInputDTO;
import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogOutputDTO;
import una.ac.cr.FitFlow.mapper.MapperForProgressLog;
import una.ac.cr.FitFlow.model.ProgressLog;
import una.ac.cr.FitFlow.model.Routine;
import una.ac.cr.FitFlow.model.User;
import una.ac.cr.FitFlow.repository.ProgressLogRepository;
import una.ac.cr.FitFlow.repository.RoutineRepository;
import una.ac.cr.FitFlow.repository.UserRepository;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProgressLogServiceImplementationTest {

    @Mock private ProgressLogRepository repo;
    @Mock private UserRepository userRepo;
    @Mock private RoutineRepository routineRepo;
    @Mock private MapperForProgressLog mapper;

    @InjectMocks
    private ProgressLogServiceImplementation service;

    private User user;
    private Routine routine;
    private ProgressLog entity;
    private ProgressLogOutputDTO out;
    private ProgressLogInputDTO in;

    // fechas fijas deterministas
    private static final ZoneId CR = ZoneId.of("America/Costa_Rica");
    private static final OffsetDateTime DATE_UTC =
            OffsetDateTime.of(2025, 9, 22, 12, 34, 56, 0, ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        routine = new Routine();
        routine.setId(10L);

        entity = new ProgressLog();

        out = new ProgressLogOutputDTO(); // asumiendo ctor vacío o builder en tu DTO

        in = new ProgressLogInputDTO();
        in.setUserId(1L);
        in.setRoutineId(10L);
        in.setDate(DATE_UTC);
    }

    // -------- CREATE --------
    @Test
    @DisplayName("create: ok")
    void create_ok() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(routineRepo.findById(10L)).thenReturn(Optional.of(routine));
        when(mapper.toEntity(in, user, routine)).thenReturn(entity);
        when(repo.save(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(out);

        ProgressLogOutputDTO result = service.create(in);

        assertNotNull(result);
        assertSame(out, result);
        verify(userRepo).findById(1L);
        verify(routineRepo).findById(10L);
        verify(repo).save(entity);
    }

    @Test
    @DisplayName("create: userId null → IllegalArgumentException")
    void create_userIdNull() {
        in.setUserId(null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.create(in));
        assertEquals("userId es obligatorio.", ex.getMessage());
        verifyNoInteractions(userRepo, routineRepo, repo, mapper);
    }

    @Test
    @DisplayName("create: routineId null → IllegalArgumentException")
    void create_routineIdNull() {
        in.setRoutineId(null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.create(in));
        assertEquals("routineId es obligatorio.", ex.getMessage());
    }

    @Test
    @DisplayName("create: date null → IllegalArgumentException")
    void create_dateNull() {
        in.setDate(null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.create(in));
        assertEquals("date es obligatorio.", ex.getMessage());
    }

    @Test
    @DisplayName("create: usuario no existe")
    void create_userNotFound() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.create(in));
        assertEquals("Usuario no encontrado: 1", ex.getMessage());
        verify(userRepo).findById(1L);
        verifyNoMoreInteractions(userRepo);
        verifyNoInteractions(routineRepo, repo, mapper);
    }

    @Test
    @DisplayName("create: rutina no existe")
    void create_routineNotFound() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(routineRepo.findById(10L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.create(in));
        assertEquals("Rutina no encontrada: 10", ex.getMessage());
        verify(userRepo).findById(1L);
        verify(routineRepo).findById(10L);
        verifyNoMoreInteractions(routineRepo);
        verifyNoInteractions(repo, mapper);
    }

    // -------- UPDATE --------
    @Test
    @DisplayName("update: ok (sin cambio de user/routine, NO busca nada)")
    void update_ok_noChanges() {
        // current con MISMO user/routine que 'in' (1L / 10L)
        User currentUser = new User(); currentUser.setId(1L);
        Routine currentRoutine = new Routine(); currentRoutine.setId(10L);

        ProgressLog current = new ProgressLog();
        current.setUser(currentUser);
        current.setRoutine(currentRoutine);

        when(repo.findById(99L)).thenReturn(Optional.of(current));
        when(repo.save(current)).thenReturn(current);
        when(mapper.toDto(current)).thenReturn(out);

        ProgressLogOutputDTO res = service.update(99L, in);

        assertSame(out, res);
        // no dispara búsquedas
        verify(userRepo, never()).findById(anyLong());
        verify(routineRepo, never()).findById(anyLong());
        verify(mapper).copyToEntity(in, current, null, null);
        verify(repo).save(current);
    }

    @Test
    @DisplayName("update: id no existe")
    void update_notFound() {
        when(repo.findById(999L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.update(999L, in));
        assertEquals("ProgressLog no encontrado: 999", ex.getMessage());
    }

    @Test
    @DisplayName("update: cambia userId → busca usuario (no rutina)")
    void update_changeUser_onlyUser() {
        // current con misma rutina = 10L (NO busca rutina)
        Routine sameRoutine = new Routine(); sameRoutine.setId(10L);
        // usuario actual distinto para forzar búsqueda de nuevo user
        User currentUser = new User(); currentUser.setId(3L);

        ProgressLog current = new ProgressLog();
        current.setRoutine(sameRoutine);
        current.setUser(currentUser);

        // in ya tiene userId=1L, routineId=10L
        when(repo.findById(50L)).thenReturn(Optional.of(current));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(repo.save(current)).thenReturn(current);
        when(mapper.toDto(current)).thenReturn(out);

        ProgressLogOutputDTO res = service.update(50L, in);

        assertSame(out, res);
        verify(userRepo).findById(1L);
        verify(routineRepo, never()).findById(anyLong());
        verify(mapper).copyToEntity(in, current, user, null);
    }

    @Test
    @DisplayName("update: cambia routineId → busca rutina (no usuario)")
    void update_changeRoutine_onlyRoutine() {
        // current con mismo usuario = 1L (NO busca usuario)
        User sameUser = new User(); sameUser.setId(1L);
        // rutina actual distinta para forzar búsqueda de nueva rutina
        Routine currentRoutine = new Routine(); currentRoutine.setId(20L);

        ProgressLog current = new ProgressLog();
        current.setUser(sameUser);
        current.setRoutine(currentRoutine);

        when(repo.findById(51L)).thenReturn(Optional.of(current));
        when(routineRepo.findById(10L)).thenReturn(Optional.of(routine)); // in.getRoutineId() = 10L
        when(repo.save(current)).thenReturn(current);
        when(mapper.toDto(current)).thenReturn(out);

        ProgressLogOutputDTO res = service.update(51L, in);

        assertSame(out, res);
        verify(routineRepo).findById(10L);
        verify(userRepo, never()).findById(anyLong());
        verify(mapper).copyToEntity(in, current, null, routine);
    }

    @Test
    @DisplayName("update: nuevo usuario no existe")
    void update_newUserNotFound() {
        // current con rutina = 10L para que sólo dispare user
        Routine sameRoutine = new Routine(); sameRoutine.setId(10L);
        User currentUser = new User(); currentUser.setId(3L);

        ProgressLog current = new ProgressLog();
        current.setRoutine(sameRoutine);
        current.setUser(currentUser);

        when(repo.findById(52L)).thenReturn(Optional.of(current));
        when(userRepo.findById(1L)).thenReturn(Optional.empty()); // in.getUserId() = 1L

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.update(52L, in));
        assertEquals("Usuario no encontrado: 1", ex.getMessage());

        verify(userRepo).findById(1L);
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("update: nueva rutina no existe")
    void update_newRoutineNotFound() {
        // current con user = 1L para que sólo dispare rutina
        User sameUser = new User(); sameUser.setId(1L);
        Routine currentRoutine = new Routine(); currentRoutine.setId(20L);

        ProgressLog current = new ProgressLog();
        current.setUser(sameUser);
        current.setRoutine(currentRoutine);

        when(repo.findById(53L)).thenReturn(Optional.of(current));
        when(routineRepo.findById(10L)).thenReturn(Optional.empty()); // in.getRoutineId() = 10L

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.update(53L, in));
        assertEquals("Rutina no encontrada: 10", ex.getMessage());

        verify(routineRepo).findById(10L);
        verify(repo, never()).save(any());
    }

    // -------- DELETE --------
    @Test
    @DisplayName("delete: ok")
    void delete_ok() {
        when(repo.existsById(77L)).thenReturn(true);
        service.delete(77L);
        verify(repo).deleteById(77L);
    }

    @Test
    @DisplayName("delete: no existe → IllegalArgumentException")
    void delete_notFound() {
        when(repo.existsById(77L)).thenReturn(false);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.delete(77L));
        assertEquals("ProgressLog no encontrado: 77", ex.getMessage());
        verify(repo, never()).deleteById(any());
    }

    // -------- FIND --------
    @Test
    @DisplayName("findById: ok")
    void findById_ok() {
        when(repo.findById(5L)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(out);

        ProgressLogOutputDTO res = service.findById(5L);

        assertSame(out, res);
        verify(mapper).toDto(entity);
    }

    @Test
    @DisplayName("findById: no existe")
    void findById_notFound() {
        when(repo.findById(5L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.findById(5L));
        assertEquals("ProgressLog no encontrado: 5", ex.getMessage());
    }

    // -------- LIST --------
    @Test
    @DisplayName("list: pagina elementos")
    void list_ok() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repo.findAll(pageable)).thenReturn(new PageImpl<>(List.of(entity)));
        when(mapper.toDto(entity)).thenReturn(out);

        Page<ProgressLogOutputDTO> page = service.list(pageable);

        assertEquals(1, page.getTotalElements());
        assertSame(out, page.getContent().get(0));
    }

    @Test
    @DisplayName("listByUser: pagina por usuario")
    void listByUser_ok() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repo.findByUser_Id(1L, pageable)).thenReturn(new PageImpl<>(List.of(entity)));
        when(mapper.toDto(entity)).thenReturn(out);

        Page<ProgressLogOutputDTO> page = service.listByUser(1L, pageable);

        assertEquals(1, page.getTotalElements());
        assertSame(out, page.getContent().get(0));
    }

    // -------- listByUserOnDate (zona horaria CR) --------
    @Test
    @DisplayName("listByUserOnDate: convierte a día local CR y consulta entre [start, end)")
    void listByUserOnDate_ok() {
        ArgumentCaptor<OffsetDateTime> startCap = ArgumentCaptor.forClass(OffsetDateTime.class);
        ArgumentCaptor<OffsetDateTime> endCap   = ArgumentCaptor.forClass(OffsetDateTime.class);

        when(repo.findByUser_IdAndLogDateBetween(eq(1L), startCap.capture(), endCap.capture()))
                .thenReturn(List.of(entity));
        when(mapper.toDto(entity)).thenReturn(out);

        List<ProgressLogOutputDTO> result = service.listByUserOnDate(1L, DATE_UTC);

        assertEquals(1, result.size());
        assertSame(out, result.get(0));

        var localCR = DATE_UTC.atZoneSameInstant(CR).toLocalDate();
        var expectedStart = localCR.atStartOfDay(CR).toOffsetDateTime();
        var expectedEnd   = localCR.plusDays(1).atStartOfDay(CR).toOffsetDateTime();

        assertEquals(expectedStart, startCap.getValue(), "start (CR 00:00) incorrecto");
        assertEquals(expectedEnd,   endCap.getValue(),   "end (CR 00:00 del día siguiente) incorrecto");
    }
}