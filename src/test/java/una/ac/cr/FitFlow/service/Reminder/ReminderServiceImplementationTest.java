package una.ac.cr.FitFlow.service.Reminder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import una.ac.cr.FitFlow.dto.Reminder.ReminderInputDTO;
import una.ac.cr.FitFlow.dto.Reminder.ReminderOutputDTO;
import una.ac.cr.FitFlow.mapper.MapperForReminder;
import una.ac.cr.FitFlow.model.Habit;
import una.ac.cr.FitFlow.model.Reminder;
import una.ac.cr.FitFlow.model.User;
import una.ac.cr.FitFlow.repository.HabitRepository;
import una.ac.cr.FitFlow.repository.ReminderRepository;
import una.ac.cr.FitFlow.repository.UserRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReminderServiceImplementationTest {

    @Mock private ReminderRepository reminderRepository;
    @Mock private UserRepository userRepository;
    @Mock private HabitRepository habitRepository;
    @Mock private MapperForReminder mapper;

    @InjectMocks
    private ReminderServiceImplementation reminderService;

    private Reminder mockReminder;
    private ReminderInputDTO mockInputDTO;
    private ReminderOutputDTO mockOutputDTO;
    private User mockUser;
    private Habit mockHabit;

    // Hora fija para que no haya sorpresas de zona horaria
    private static final OffsetDateTime FIXED_9_AM_UTC = OffsetDateTime.of(2025, 1, 1, 9, 0, 0, 0, ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);

        mockHabit = new Habit();
        mockHabit.setId(1L);

        mockReminder = new Reminder();
        mockReminder.setId(1L);
        mockReminder.setUser(mockUser);
        mockReminder.setHabit(mockHabit);
        mockReminder.setMessage("Test reminder");
        mockReminder.setTime(FIXED_9_AM_UTC);
        mockReminder.setFrequency(Reminder.Frequency.DAILY);

        mockInputDTO = new ReminderInputDTO();
        mockInputDTO.setUserId(1L);
        mockInputDTO.setHabitId(1L);
        mockInputDTO.setMessage("Test reminder");
        mockInputDTO.setTime(FIXED_9_AM_UTC);
        mockInputDTO.setFrequency("DAILY");

        mockOutputDTO = ReminderOutputDTO.builder()
                .id(1L)
                .userId(1L)
                .habitId(1L)
                .message("Test reminder")
                .time(FIXED_9_AM_UTC)
                .frequency("DAILY")
                .build();
    }

    // ---------- CREATE ----------
    @Test
    @DisplayName("create: crea recordatorio con input válido (OffsetDateTime)")
    void create_WithValidInput_ShouldCreateSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(habitRepository.findById(1L)).thenReturn(Optional.of(mockHabit));
        when(mapper.toEntity(mockInputDTO, mockUser, mockHabit, Reminder.Frequency.DAILY)).thenReturn(mockReminder);
        when(reminderRepository.save(mockReminder)).thenReturn(mockReminder);
        when(mapper.toDto(mockReminder)).thenReturn(mockOutputDTO);

        ReminderOutputDTO result = reminderService.create(mockInputDTO);

        assertNotNull(result);
        assertEquals(mockOutputDTO.getId(), result.getId());
        assertEquals(FIXED_9_AM_UTC, result.getTime());
        verify(userRepository).findById(1L);
        verify(habitRepository).findById(1L);
        verify(reminderRepository).save(mockReminder);
    }

    @Test
    @DisplayName("create: userId null → IllegalArgumentException")
    void create_WithNullUserId_ShouldThrowException() {
        mockInputDTO.setUserId(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reminderService.create(mockInputDTO));
        assertEquals("userId es obligatorio.", ex.getMessage());

        verify(userRepository, never()).findById(any());
        verify(habitRepository, never()).findById(any());
        verify(reminderRepository, never()).save(any());
    }

    @Test
    @DisplayName("create: habitId null → IllegalArgumentException")
    void create_WithNullHabitId_ShouldThrowException() {
        mockInputDTO.setHabitId(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reminderService.create(mockInputDTO));
        assertEquals("habitId es obligatorio.", ex.getMessage());
    }

    @Test
    @DisplayName("create: message null/blank → IllegalArgumentException")
    void create_WithNullOrBlankMessage_ShouldThrowException() {
        mockInputDTO.setMessage(null);
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> reminderService.create(mockInputDTO));
        assertEquals("message es obligatorio.", ex1.getMessage());

        mockInputDTO.setMessage("   ");
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> reminderService.create(mockInputDTO));
        assertEquals("message es obligatorio.", ex2.getMessage());
    }

    @Test
    @DisplayName("create: time null → IllegalArgumentException (OffsetDateTime)")
    void create_WithNullTime_ShouldThrowException() {
        mockInputDTO.setTime(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reminderService.create(mockInputDTO));
        assertEquals("time es obligatorio.", ex.getMessage());
    }

    @Test
    @DisplayName("create: frequency null/blank → IllegalArgumentException")
    void create_WithNullOrBlankFrequency_ShouldThrowException() {
        mockInputDTO.setFrequency(null);
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> reminderService.create(mockInputDTO));
        assertEquals("frequency es obligatorio.", ex1.getMessage());

        mockInputDTO.setFrequency("   ");
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> reminderService.create(mockInputDTO));
        assertEquals("frequency es obligatorio.", ex2.getMessage());
    }

    @Test
    @DisplayName("create: usuario no existe")
    void create_WithNonExistentUser_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reminderService.create(mockInputDTO));
        assertEquals("Usuario no encontrado: 1", ex.getMessage());

        verify(userRepository).findById(1L);
        verify(habitRepository, never()).findById(any());
    }

    @Test
    @DisplayName("create: hábito no existe")
    void create_WithNonExistentHabit_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(habitRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reminderService.create(mockInputDTO));
        assertEquals("Hábito no encontrado: 1", ex.getMessage());

        verify(userRepository).findById(1L);
        verify(habitRepository).findById(1L);
    }

    @Test
    @DisplayName("create: parsea frequency = WEEKLY (case-insensitive) y usa OffsetDateTime")
    void create_ShouldParseFrequencyCorrectly_Weekly() {
        mockInputDTO.setFrequency("weekly");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(habitRepository.findById(1L)).thenReturn(Optional.of(mockHabit));
        when(reminderRepository.save(any(Reminder.class))).thenReturn(mockReminder);
        when(mapper.toDto(any(Reminder.class))).thenReturn(mockOutputDTO);

        ArgumentCaptor<Reminder.Frequency> freqCap = ArgumentCaptor.forClass(Reminder.Frequency.class);
        when(mapper.toEntity(eq(mockInputDTO), eq(mockUser), eq(mockHabit), freqCap.capture()))
                .thenReturn(mockReminder);

        ReminderOutputDTO result = reminderService.create(mockInputDTO);

        assertNotNull(result);
        assertEquals(Reminder.Frequency.WEEKLY, freqCap.getValue());
        assertEquals(FIXED_9_AM_UTC, mockInputDTO.getTime());
    }

    @Test
    @DisplayName("create: frequency inválida → IllegalArgumentException")
    void create_WithInvalidFrequency_ShouldThrowException() {
        mockInputDTO.setFrequency("INVALID");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(habitRepository.findById(1L)).thenReturn(Optional.of(mockHabit));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reminderService.create(mockInputDTO));
        assertEquals("Frecuencia inválida. Use DAILY o WEEKLY.", ex.getMessage());
    }

    // ---------- UPDATE ----------
    @Test
    @DisplayName("update: ok con input válido (OffsetDateTime)")
    void update_WithValidInput_ShouldUpdateSuccessfully() {
        Long id = 1L;
        when(reminderRepository.findById(id)).thenReturn(Optional.of(mockReminder));
        when(reminderRepository.save(mockReminder)).thenReturn(mockReminder);
        when(mapper.toDto(mockReminder)).thenReturn(mockOutputDTO);

        ReminderOutputDTO result = reminderService.update(id, mockInputDTO);

        assertNotNull(result);
        verify(reminderRepository).findById(id);
        verify(mapper).copyToEntity(mockInputDTO, mockReminder, null, null, Reminder.Frequency.DAILY);
        verify(reminderRepository).save(mockReminder);
    }

    @Test
    @DisplayName("update: recordatorio no existe")
    void update_WithNonExistentReminder_ShouldThrowException() {
        Long id = 999L;
        when(reminderRepository.findById(id)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reminderService.update(id, mockInputDTO));
        assertEquals("Recordatorio no encontrado: 999", ex.getMessage());

        verify(reminderRepository).findById(id);
        verify(reminderRepository, never()).save(any());
    }

    @Test
    @DisplayName("update: cambia userId → busca nuevo usuario")
    void update_WithChangedUserId_ShouldFetchNewUser() {
        Long id = 1L;
        User newUser = new User(); newUser.setId(2L);
        mockInputDTO.setUserId(2L);

        when(reminderRepository.findById(id)).thenReturn(Optional.of(mockReminder));
        when(userRepository.findById(2L)).thenReturn(Optional.of(newUser));
        when(reminderRepository.save(mockReminder)).thenReturn(mockReminder);
        when(mapper.toDto(mockReminder)).thenReturn(mockOutputDTO);

        ReminderOutputDTO result = reminderService.update(id, mockInputDTO);

        assertNotNull(result);
        verify(userRepository).findById(2L);
        verify(mapper).copyToEntity(mockInputDTO, mockReminder, newUser, null, Reminder.Frequency.DAILY);
    }

    @Test
    @DisplayName("update: cambia habitId → busca nuevo hábito")
    void update_WithChangedHabitId_ShouldFetchNewHabit() {
        Long id = 1L;
        Habit newHabit = new Habit(); newHabit.setId(2L);
        mockInputDTO.setHabitId(2L);

        when(reminderRepository.findById(id)).thenReturn(Optional.of(mockReminder));
        when(habitRepository.findById(2L)).thenReturn(Optional.of(newHabit));
        when(reminderRepository.save(mockReminder)).thenReturn(mockReminder);
        when(mapper.toDto(mockReminder)).thenReturn(mockOutputDTO);

        ReminderOutputDTO result = reminderService.update(id, mockInputDTO);

        assertNotNull(result);
        verify(habitRepository).findById(2L);
        verify(mapper).copyToEntity(mockInputDTO, mockReminder, null, newHabit, Reminder.Frequency.DAILY);
    }

    @Test
    @DisplayName("update: mismo userId → no vuelve a buscar usuario")
    void update_WithSameUserId_ShouldNotFetchUser() {
        Long id = 1L;
        when(reminderRepository.findById(id)).thenReturn(Optional.of(mockReminder));
        when(reminderRepository.save(mockReminder)).thenReturn(mockReminder);
        when(mapper.toDto(mockReminder)).thenReturn(mockOutputDTO);

        ReminderOutputDTO result = reminderService.update(id, mockInputDTO);

        assertNotNull(result);
        verify(userRepository, never()).findById(any());
        verify(mapper).copyToEntity(mockInputDTO, mockReminder, null, null, Reminder.Frequency.DAILY);
    }

    @Test
    @DisplayName("update: input con valores nulos opcionales → maneja sin error")
    void update_WithNullValues_ShouldHandleGracefully() {
        Long id = 1L;
        ReminderInputDTO updateInput = new ReminderInputDTO();
        when(reminderRepository.findById(id)).thenReturn(Optional.of(mockReminder));
        when(reminderRepository.save(mockReminder)).thenReturn(mockReminder);
        when(mapper.toDto(mockReminder)).thenReturn(mockOutputDTO);

        ReminderOutputDTO result = reminderService.update(id, updateInput);

        assertNotNull(result);
        verify(mapper).copyToEntity(updateInput, mockReminder, null, null, null);
    }

    @Test
    @DisplayName("update: nuevo usuario no existe")
    void update_WithNonExistentNewUser_ShouldThrowException() {
        Long id = 1L;
        mockInputDTO.setUserId(999L);

        when(reminderRepository.findById(id)).thenReturn(Optional.of(mockReminder));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reminderService.update(id, mockInputDTO));
        assertEquals("Usuario no encontrado: 999", ex.getMessage());

        verify(userRepository).findById(999L);
        verify(reminderRepository, never()).save(any());
    }

    // ---------- DELETE ----------
    @Test
    @DisplayName("delete: elimina cuando existe")
    void delete_WithExistingReminder_ShouldDeleteSuccessfully() {
        Long id = 1L;
        when(reminderRepository.existsById(id)).thenReturn(true);

        reminderService.delete(id);

        verify(reminderRepository).existsById(id);
        verify(reminderRepository).deleteById(id);
    }

    @Test
    @DisplayName("delete: no existe → IllegalArgumentException")
    void delete_WithNonExistentReminder_ShouldThrowException() {
        Long id = 999L;
        when(reminderRepository.existsById(id)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reminderService.delete(id));
        assertEquals("Recordatorio no encontrado: 999", ex.getMessage());

        verify(reminderRepository).existsById(id);
        verify(reminderRepository, never()).deleteById(any());
    }

    // ---------- FIND BY ID ----------
    @Test
    @DisplayName("findById: retorna cuando existe")
    void findById_WithExistingReminder_ShouldReturnReminder() {
        Long id = 1L;
        when(reminderRepository.findById(id)).thenReturn(Optional.of(mockReminder));
        when(mapper.toDto(mockReminder)).thenReturn(mockOutputDTO);

        ReminderOutputDTO result = reminderService.findById(id);

        assertNotNull(result);
        assertEquals(mockOutputDTO.getId(), result.getId());
        assertEquals(FIXED_9_AM_UTC, result.getTime());

        verify(reminderRepository).findById(id);
    }

    @Test
    @DisplayName("findById: no existe → IllegalArgumentException")
    void findById_WithNonExistentReminder_ShouldThrowException() {
        Long id = 999L;
        when(reminderRepository.findById(id)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reminderService.findById(id));
        assertEquals("Recordatorio no encontrado: 999", ex.getMessage());

        verify(reminderRepository).findById(id);
    }

    // ---------- LIST ----------
    @Test
    @DisplayName("list: devuelve página paginada")
    void list_ShouldReturnPaginatedReminders() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Reminder> page = new PageImpl<>(List.of(mockReminder));

        when(reminderRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(mapper.toDto(mockReminder)).thenReturn(mockOutputDTO);

        Page<ReminderOutputDTO> result = reminderService.list(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(mockOutputDTO.getId(), result.getContent().get(0).getId());

        verify(reminderRepository).findAll(pageable);
    }

    @Test
    @DisplayName("list: página vacía cuando no hay recordatorios")
    void list_WithNoReminders_ShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Reminder> empty = new PageImpl<>(List.of());

        when(reminderRepository.findAll(any(Pageable.class))).thenReturn(empty);

        Page<ReminderOutputDTO> result = reminderService.list(pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    @DisplayName("listByUserId: por usuario")
    void listByUserId_WithValidUserId_ShouldReturnUserReminders() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Reminder> page = new PageImpl<>(List.of(mockReminder));

        when(reminderRepository.findByUser_Id(eq(userId), any(Pageable.class))).thenReturn(page);
        when(mapper.toDto(mockReminder)).thenReturn(mockOutputDTO);

        Page<ReminderOutputDTO> result = reminderService.listByUserId(userId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(userId, result.getContent().get(0).getUserId());

        verify(reminderRepository).findByUser_Id(userId, pageable);
    }

    @Test
    @DisplayName("listByUserId: vacío cuando el usuario no tiene recordatorios")
    void listByUserId_WithUserHavingNoReminders_ShouldReturnEmptyPage() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        when(reminderRepository.findByUser_Id(eq(userId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        Page<ReminderOutputDTO> result = reminderService.listByUserId(userId, pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    @DisplayName("listByHabitId: por hábito")
    void listByHabitId_WithValidHabitId_ShouldReturnHabitReminders() {
        Long habitId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Reminder> page = new PageImpl<>(List.of(mockReminder));

        when(reminderRepository.findByHabit_Id(eq(habitId), any(Pageable.class))).thenReturn(page);
        when(mapper.toDto(mockReminder)).thenReturn(mockOutputDTO);

        Page<ReminderOutputDTO> result = reminderService.listByHabitId(habitId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(habitId, result.getContent().get(0).getHabitId());

        verify(reminderRepository).findByHabit_Id(habitId, pageable);
    }

    @Test
    @DisplayName("listByHabitId: vacío cuando el hábito no tiene recordatorios")
    void listByHabitId_WithHabitHavingNoReminders_ShouldReturnEmptyPage() {
        Long habitId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        when(reminderRepository.findByHabit_Id(eq(habitId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        Page<ReminderOutputDTO> result = reminderService.listByHabitId(habitId, pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    // ---------- Helpers ----------
    private ReminderInputDTO createReminderInputDTO(String frequency) {
        ReminderInputDTO dto = new ReminderInputDTO();
        dto.setUserId(1L);
        dto.setHabitId(1L);
        dto.setMessage("Test");
        dto.setTime(FIXED_9_AM_UTC);
        dto.setFrequency(frequency);
        return dto;
    }
}