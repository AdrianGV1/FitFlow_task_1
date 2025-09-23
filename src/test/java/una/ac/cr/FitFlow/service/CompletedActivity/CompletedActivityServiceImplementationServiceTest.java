package una.ac.cr.FitFlow.service.CompletedActivity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import una.ac.cr.FitFlow.dto.CompletedActivity.CompletedActivityInputDTO;
import una.ac.cr.FitFlow.dto.CompletedActivity.CompletedActivityOutputDTO;
import una.ac.cr.FitFlow.mapper.MapperForCompletedActivity;
import una.ac.cr.FitFlow.model.CompletedActivity;
import una.ac.cr.FitFlow.model.Habit;
import una.ac.cr.FitFlow.model.ProgressLog;
import una.ac.cr.FitFlow.repository.CompletedActivityRepository;
import una.ac.cr.FitFlow.repository.HabitRepository;
import una.ac.cr.FitFlow.repository.ProgressLogRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompletedActivityServiceImplementationTest {

    @Mock private CompletedActivityRepository completedActivityRepository;
    @Mock private HabitRepository habitRepository;
    @Mock private ProgressLogRepository progressLogRepository;
    @Mock private MapperForCompletedActivity mapper;

    @InjectMocks
    private CompletedActivityServiceImplementation service;

    // ---------- Helpers ----------
    private static final OffsetDateTime FIXED_TS =
            OffsetDateTime.of(2025, 9, 15, 8, 30, 0, 0, ZoneOffset.UTC);

    private CompletedActivityInputDTO input(Long habitId, Long progressLogId, OffsetDateTime completedAt, String notes) {
        CompletedActivityInputDTO dto = new CompletedActivityInputDTO();
        dto.setHabitId(habitId);
        dto.setProgressLogId(progressLogId);
        dto.setCompletedAt(completedAt);
        dto.setNotes(notes);
        return dto;
    }

    private Habit habit(Long id, Habit.Category cat) {
        Habit h = new Habit();
        h.setId(id);
        h.setName("H" + id);
        h.setCategory(cat);
        return h;
    }

    private ProgressLog pl(Long id) {
        ProgressLog p = new ProgressLog();
        p.setId(id);
        return p;
    }

    private CompletedActivity activity(Long id) {
        CompletedActivity c = new CompletedActivity();
        c.setId(id);
        return c;
    }

    // ---------- CREATE ----------
    @Test
    @DisplayName("createCompletedActivity: ok")
    void create_ok() {
        CompletedActivityInputDTO in = input(1L, 2L, FIXED_TS, "nota");
        Habit h = habit(1L, Habit.Category.PHYSICAL);
        ProgressLog p = pl(2L);
        CompletedActivity entity = activity(null);
        CompletedActivity saved  = activity(10L);
        CompletedActivityOutputDTO out = new CompletedActivityOutputDTO();

        when(habitRepository.findById(1L)).thenReturn(Optional.of(h));
        when(progressLogRepository.findById(2L)).thenReturn(Optional.of(p));
        // El servicio crea entity con builder y luego llama copyBasics(input, entity)
        doAnswer(inv -> null).when(mapper).copyBasics(eq(in), any(CompletedActivity.class));
        when(completedActivityRepository.save(any(CompletedActivity.class))).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(out);

        CompletedActivityOutputDTO res = service.createCompletedActivity(in);

        assertSame(out, res);
        verify(habitRepository).findById(1L);
        verify(progressLogRepository).findById(2L);
        verify(mapper).copyBasics(eq(in), any(CompletedActivity.class));
        verify(completedActivityRepository).save(any(CompletedActivity.class));
    }

    @Test
    @DisplayName("createCompletedActivity: completedAt obligatorio")
    void create_completedAtRequired() {
        CompletedActivityInputDTO in = input(1L, 2L, null, "x");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createCompletedActivity(in));
        assertEquals("completedAt es obligatorio.", ex.getMessage());
        verifyNoInteractions(habitRepository, progressLogRepository, completedActivityRepository, mapper);
    }

    @Test
    @DisplayName("createCompletedActivity: habitId obligatorio")
    void create_habitIdRequired() {
        CompletedActivityInputDTO in = input(null, 2L, FIXED_TS, "x");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createCompletedActivity(in));
        assertEquals("habitId es obligatorio.", ex.getMessage());
    }

    @Test
    @DisplayName("createCompletedActivity: progressLogId obligatorio")
    void create_progressLogIdRequired() {
        CompletedActivityInputDTO in = input(1L, null, FIXED_TS, "x");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createCompletedActivity(in));
        assertEquals("progressLogId es obligatorio.", ex.getMessage());
    }

    @Test
    @DisplayName("createCompletedActivity: habit no existe")
    void create_habitNotFound() {
        CompletedActivityInputDTO in = input(1L, 2L, FIXED_TS, "x");
        when(habitRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createCompletedActivity(in));
        assertEquals("Habit no encontrado: 1", ex.getMessage());

        verify(habitRepository).findById(1L);
        verifyNoInteractions(progressLogRepository, completedActivityRepository, mapper);
    }

    @Test
    @DisplayName("createCompletedActivity: progressLog no existe")
    void create_plNotFound() {
        CompletedActivityInputDTO in = input(1L, 2L, FIXED_TS, "x");
        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit(1L, Habit.Category.MENTAL)));
        when(progressLogRepository.findById(2L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createCompletedActivity(in));
        assertEquals("ProgressLog no encontrado: 2", ex.getMessage());

        verify(habitRepository).findById(1L);
        verify(progressLogRepository).findById(2L);
        verifyNoInteractions(completedActivityRepository, mapper);
    }

    // ---------- UPDATE ----------
    @Test
    @DisplayName("updateCompletedActivity: ok (sin cambios de relaciones)")
    void update_ok() {
        Long id = 9L;
        CompletedActivityInputDTO in = input(null, null, FIXED_TS, "nota editada");
        CompletedActivity current = activity(id);
        CompletedActivityOutputDTO out = new CompletedActivityOutputDTO();

        when(completedActivityRepository.findById(id)).thenReturn(Optional.of(current));
        doAnswer(inv -> null).when(mapper).copyBasics(in, current);
        when(completedActivityRepository.save(current)).thenReturn(current);
        when(mapper.toDto(current)).thenReturn(out);

        CompletedActivityOutputDTO res = service.updateCompletedActivity(id, in);

        assertSame(out, res);
        verify(habitRepository, never()).findById(any());
        verify(progressLogRepository, never()).findById(any());
        verify(mapper).copyBasics(in, current);
        verify(completedActivityRepository).save(current);
    }

    @Test
    @DisplayName("updateCompletedActivity: cambia habitId → busca habit")
    void update_changeHabit() {
        Long id = 9L;
        CompletedActivityInputDTO in = input(7L, null, FIXED_TS, "x");
        CompletedActivity current = activity(id);
        when(completedActivityRepository.findById(id)).thenReturn(Optional.of(current));
        when(habitRepository.findById(7L)).thenReturn(Optional.of(habit(7L, Habit.Category.SLEEP)));
        when(completedActivityRepository.save(current)).thenReturn(current);
        when(mapper.toDto(current)).thenReturn(new CompletedActivityOutputDTO());

        CompletedActivityOutputDTO res = service.updateCompletedActivity(id, in);

        assertNotNull(res);
        verify(habitRepository).findById(7L);
        verify(progressLogRepository, never()).findById(any());
        verify(completedActivityRepository).save(current);
    }

    @Test
    @DisplayName("updateCompletedActivity: cambia progressLogId → busca progressLog")
    void update_changeProgressLog() {
        Long id = 9L;
        CompletedActivityInputDTO in = input(null, 22L, FIXED_TS, "x");
        CompletedActivity current = activity(id);
        when(completedActivityRepository.findById(id)).thenReturn(Optional.of(current));
        when(progressLogRepository.findById(22L)).thenReturn(Optional.of(pl(22L)));
        when(completedActivityRepository.save(current)).thenReturn(current);
        when(mapper.toDto(current)).thenReturn(new CompletedActivityOutputDTO());

        CompletedActivityOutputDTO res = service.updateCompletedActivity(id, in);

        assertNotNull(res);
        verify(progressLogRepository).findById(22L);
        verify(habitRepository, never()).findById(any());
        verify(completedActivityRepository).save(current);
    }

    @Test
    @DisplayName("updateCompletedActivity: id no existe")
    void update_notFound() {
        when(completedActivityRepository.findById(111L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateCompletedActivity(111L, input(null, null, FIXED_TS, "x")));
        assertEquals("CompletedActivity no encontrado: 111", ex.getMessage());
    }

    @Test
    @DisplayName("updateCompletedActivity: nuevo habit no existe")
    void update_newHabitNotFound() {
        Long id = 9L;
        CompletedActivityInputDTO in = input(77L, null, FIXED_TS, "x");
        CompletedActivity current = activity(id);
        when(completedActivityRepository.findById(id)).thenReturn(Optional.of(current));
        when(habitRepository.findById(77L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateCompletedActivity(id, in));
        assertEquals("Habit no encontrado: 77", ex.getMessage());

        verify(habitRepository).findById(77L);
        verify(completedActivityRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateCompletedActivity: nuevo progressLog no existe")
    void update_newPlNotFound() {
        Long id = 9L;
        CompletedActivityInputDTO in = input(null, 55L, FIXED_TS, "x");
        CompletedActivity current = activity(id);
        when(completedActivityRepository.findById(id)).thenReturn(Optional.of(current));
        when(progressLogRepository.findById(55L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateCompletedActivity(id, in));
        assertEquals("ProgressLog no encontrado: 55", ex.getMessage());

        verify(progressLogRepository).findById(55L);
        verify(completedActivityRepository, never()).save(any());
    }

    // ---------- DELETE ----------
    @Test
    @DisplayName("deleteCompletedActivity: ok")
    void delete_ok() {
        Long id = 3L;
        CompletedActivity current = activity(id);
        when(completedActivityRepository.findById(id)).thenReturn(Optional.of(current));

        service.deleteCompletedActivity(id);

        verify(completedActivityRepository).delete(current);
    }

    @Test
    @DisplayName("deleteCompletedActivity: no existe")
    void delete_notFound() {
        when(completedActivityRepository.findById(3L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.deleteCompletedActivity(3L));
        assertEquals("CompletedActivity no encontrado: 3", ex.getMessage());
        verify(completedActivityRepository, never()).delete(any());
    }

    // ---------- FIND ----------
    @Test
    @DisplayName("findCompletedActivityById: ok")
    void findById_ok() {
        Long id = 4L;
        CompletedActivity current = activity(id);
        CompletedActivityOutputDTO out = new CompletedActivityOutputDTO();

        when(completedActivityRepository.findById(id)).thenReturn(Optional.of(current));
        when(mapper.toDto(current)).thenReturn(out);

        CompletedActivityOutputDTO res = service.findCompletedActivityById(id);

        assertSame(out, res);
        verify(mapper).toDto(current);
    }

    @Test
    @DisplayName("findCompletedActivityById: no existe")
    void findById_notFound() {
        when(completedActivityRepository.findById(4L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.findCompletedActivityById(4L));
        assertEquals("CompletedActivity no encontrado: 4", ex.getMessage());
    }

    // ---------- LIST ----------
    @Test
    @DisplayName("listCompletedActivities: q vacío → findAll")
    void list_blank() {
        Pageable pageable = PageRequest.of(0, 10);
        CompletedActivity c1 = activity(1L);
        CompletedActivity c2 = activity(2L);
        when(completedActivityRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(c1, c2)));

        CompletedActivityOutputDTO o1 = new CompletedActivityOutputDTO();
        CompletedActivityOutputDTO o2 = new CompletedActivityOutputDTO();
        when(mapper.toDto(c1)).thenReturn(o1);
        when(mapper.toDto(c2)).thenReturn(o2);

        Page<CompletedActivityOutputDTO> page = service.listCompletedActivities("  ", pageable);

        assertEquals(2, page.getTotalElements());
        assertTrue(page.getContent().containsAll(List.of(o1, o2)));
        verify(completedActivityRepository).findAll(pageable);
    }

    @Test
    @DisplayName("listCompletedActivities: q con texto → findByNotesContainingIgnoreCase")
    void list_byNotes() {
        Pageable pageable = PageRequest.of(0, 10);
        String q = "meta";
        CompletedActivity c1 = activity(1L);
        when(completedActivityRepository.findByNotesContainingIgnoreCase(q.trim(), pageable))
                .thenReturn(new PageImpl<>(List.of(c1)));

        CompletedActivityOutputDTO o1 = new CompletedActivityOutputDTO();
        when(mapper.toDto(c1)).thenReturn(o1);

        Page<CompletedActivityOutputDTO> page = service.listCompletedActivities(q, pageable);

        assertEquals(1, page.getTotalElements());
        assertSame(o1, page.getContent().get(0));
        verify(completedActivityRepository).findByNotesContainingIgnoreCase(q, pageable);
    }

    // ---------- BY USER / PROGRESS LOG / HABIT ----------
    @Test
    @DisplayName("findCompletedActivitiesByUserId: pagina por usuario")
    void findByUserId_ok() {
        Pageable pageable = PageRequest.of(0, 10);
        CompletedActivity c = activity(1L);
        when(completedActivityRepository.findByProgressLog_User_Id(9L, pageable))
                .thenReturn(new PageImpl<>(List.of(c)));
        CompletedActivityOutputDTO o = new CompletedActivityOutputDTO();
        when(mapper.toDto(c)).thenReturn(o);

        Page<CompletedActivityOutputDTO> page = service.findCompletedActivitiesByUserId(9L, pageable);

        assertEquals(1, page.getTotalElements());
        assertSame(o, page.getContent().get(0));
        verify(completedActivityRepository).findByProgressLog_User_Id(9L, pageable);
    }

    @Test
    @DisplayName("findByProgressLogId: pagina por progress log")
    void findByProgressLogId_ok() {
        Pageable pageable = PageRequest.of(0, 10);
        CompletedActivity c = activity(1L);
        when(completedActivityRepository.findByProgressLog_Id(22L, pageable))
                .thenReturn(new PageImpl<>(List.of(c)));
        CompletedActivityOutputDTO o = new CompletedActivityOutputDTO();
        when(mapper.toDto(c)).thenReturn(o);

        Page<CompletedActivityOutputDTO> page = service.findByProgressLogId(22L, pageable);

        assertEquals(1, page.getTotalElements());
        assertSame(o, page.getContent().get(0));
        verify(completedActivityRepository).findByProgressLog_Id(22L, pageable);
    }

    @Test
    @DisplayName("findByHabitId: lista simple")
    void findByHabitId_ok() {
        CompletedActivity c = activity(1L);
        when(completedActivityRepository.findByHabit_Id(7L)).thenReturn(List.of(c));
        CompletedActivityOutputDTO o = new CompletedActivityOutputDTO();
        when(mapper.toDto(c)).thenReturn(o);

        List<CompletedActivityOutputDTO> list = service.findByHabitId(7L);

        assertEquals(1, list.size());
        assertSame(o, list.get(0));
        verify(completedActivityRepository).findByHabit_Id(7L);
    }

    // ---------- Monthly by Category ----------
    @Test
    @DisplayName("monthlyCompletedActivitiesByCategoryAndDate: calcula [start, end) del mes y usa enum Category")
    void monthlyByCategory_ok() {
        // Fecha base: 2025-09-15T08:30Z → start debe ser 2025-09-01T00:00Z y end 2025-10-01T00:00Z
        OffsetDateTime base = FIXED_TS;
        ArgumentCaptor<OffsetDateTime> startCap = ArgumentCaptor.forClass(OffsetDateTime.class);
        ArgumentCaptor<OffsetDateTime> endCap   = ArgumentCaptor.forClass(OffsetDateTime.class);

        CompletedActivity c = activity(1L);
        when(completedActivityRepository.findCompletedByCategoryAndMonth(
                eq(Habit.Category.DIET), startCap.capture(), endCap.capture()))
                .thenReturn(List.of(c));
        CompletedActivityOutputDTO o = new CompletedActivityOutputDTO();
        when(mapper.toDto(c)).thenReturn(o);

        List<CompletedActivityOutputDTO> res = service.monthlyCompletedActivitiesByCategoryAndDate("diet", base);

        assertEquals(1, res.size());
        assertSame(o, res.get(0));

        OffsetDateTime start = startCap.getValue();
        OffsetDateTime end   = endCap.getValue();

        assertEquals(2025, start.getYear());
        assertEquals(9, start.getMonthValue());
        assertEquals(1, start.getDayOfMonth());
        assertEquals(0, start.getHour());
        assertEquals(0, start.getMinute());
        assertEquals(0, start.getSecond());
        assertEquals(0, start.getNano());

        assertEquals(2025, end.getYear());
        assertEquals(10, end.getMonthValue());
        assertEquals(1, end.getDayOfMonth());
        assertEquals(0, end.getHour());
        assertEquals(0, end.getMinute());
        assertEquals(0, end.getSecond());
        assertEquals(0, end.getNano());
    }

    @Test
    @DisplayName("monthlyCompletedActivitiesByCategoryAndDate: categoría inválida → IllegalArgumentException")
    void monthlyByCategory_invalid() {
        OffsetDateTime base = FIXED_TS;
        assertThrows(IllegalArgumentException.class,
                () -> service.monthlyCompletedActivitiesByCategoryAndDate("invalid-cat", base));
        verifyNoInteractions(completedActivityRepository, mapper);
    }
}