package una.ac.cr.FitFlow.service.RoutineActivity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import una.ac.cr.FitFlow.dto.RoutineActivity.RoutineActivityInputDTO;
import una.ac.cr.FitFlow.dto.RoutineActivity.RoutineActivityOutputDTO;
import una.ac.cr.FitFlow.mapper.MapperForRoutineActivity;
import una.ac.cr.FitFlow.model.Habit;
import una.ac.cr.FitFlow.model.Routine;
import una.ac.cr.FitFlow.model.RoutineActivity;
import una.ac.cr.FitFlow.repository.HabitRepository;
import una.ac.cr.FitFlow.repository.RoutineActivityRepository;
import una.ac.cr.FitFlow.repository.RoutineRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoutineActivityServiceImplementationTest {

    @Mock
    private RoutineActivityRepository routineActivityRepository;
    
    @Mock
    private RoutineRepository routineRepository;
    
    @Mock
    private HabitRepository habitRepository;
    
    @Mock
    private MapperForRoutineActivity mapper;

    @InjectMocks
    private RoutineActivityServiceImplementation routineActivityService;

    private RoutineActivity mockRoutineActivity;
    private RoutineActivityInputDTO mockInputDTO;
    private RoutineActivityOutputDTO mockOutputDTO;
    private Routine mockRoutine;
    private Habit mockHabit;

    @BeforeEach
    void setUp() {
        // Setup mock objects
        mockRoutine = new Routine();
        mockRoutine.setId(1L);

        mockHabit = new Habit();
        mockHabit.setId(1L);

        mockRoutineActivity = new RoutineActivity();
        mockRoutineActivity.setId(1L);
        mockRoutineActivity.setRoutine(mockRoutine);
        mockRoutineActivity.setHabit(mockHabit);
        mockRoutineActivity.setDuration(30);

        mockInputDTO = new RoutineActivityInputDTO();
        mockInputDTO.setRoutineId(1L);
        mockInputDTO.setHabitId(1L);
        mockInputDTO.setDuration(30);

        mockOutputDTO = RoutineActivityOutputDTO.builder()
                .id(1L)
                .routineId(1L)
                .habitId(1L)
                .duration(30)
                .build();
    }

    // Tests for create method
    @Test
    @DisplayName("create should create routine activity successfully with valid input")
    void create_WithValidInput_ShouldCreateSuccessfully() {
        // Given
        when(routineActivityRepository.existsByRoutine_IdAndHabit_Id(1L, 1L)).thenReturn(false);
        when(routineRepository.findById(1L)).thenReturn(Optional.of(mockRoutine));
        when(habitRepository.findById(1L)).thenReturn(Optional.of(mockHabit));
        when(mapper.toEntity(mockInputDTO, mockRoutine, mockHabit)).thenReturn(mockRoutineActivity);
        when(routineActivityRepository.save(mockRoutineActivity)).thenReturn(mockRoutineActivity);
        when(mapper.toDto(mockRoutineActivity)).thenReturn(mockOutputDTO);

        // When
        RoutineActivityOutputDTO result = routineActivityService.create(mockInputDTO);

        // Then
        assertNotNull(result);
        assertEquals(mockOutputDTO.getId(), result.getId());
        assertEquals(mockOutputDTO.getRoutineId(), result.getRoutineId());
        assertEquals(mockOutputDTO.getHabitId(), result.getHabitId());
        assertEquals(mockOutputDTO.getDuration(), result.getDuration());
        
        verify(routineActivityRepository).save(mockRoutineActivity);
        verify(mapper).toDto(mockRoutineActivity);
    }

    @Test
    @DisplayName("create should throw exception when routineId is null")
    void create_WithNullRoutineId_ShouldThrowException() {
        // Given
        mockInputDTO.setRoutineId(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> routineActivityService.create(mockInputDTO)
        );
        assertEquals("routineId es obligatorio.", exception.getMessage());
    }

    @Test
    @DisplayName("create should throw exception when habitId is null")
    void create_WithNullHabitId_ShouldThrowException() {
        // Given
        mockInputDTO.setHabitId(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> routineActivityService.create(mockInputDTO)
        );
        assertEquals("habitId es obligatorio.", exception.getMessage());
    }

    @Test
    @DisplayName("create should throw exception when duration is null")
    void create_WithNullDuration_ShouldThrowException() {
        // Given
        mockInputDTO.setDuration(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> routineActivityService.create(mockInputDTO)
        );
        assertEquals("duration debe ser >= 1.", exception.getMessage());
    }

    @Test
    @DisplayName("create should throw exception when duration is less than 1")
    void create_WithInvalidDuration_ShouldThrowException() {
        // Given
        mockInputDTO.setDuration(0);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> routineActivityService.create(mockInputDTO)
        );
        assertEquals("duration debe ser >= 1.", exception.getMessage());
    }

    @Test
    @DisplayName("create should throw exception when routine activity already exists")
    void create_WithExistingRoutineActivity_ShouldThrowException() {
        // Given
        when(routineActivityRepository.existsByRoutine_IdAndHabit_Id(1L, 1L)).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> routineActivityService.create(mockInputDTO)
        );
        assertEquals("Ya existe esa actividad para (routineId, habitId).", exception.getMessage());
    }

    @Test
    @DisplayName("create should throw exception when routine not found")
    void create_WithNonExistentRoutine_ShouldThrowException() {
        // Given
        when(routineActivityRepository.existsByRoutine_IdAndHabit_Id(1L, 1L)).thenReturn(false);
        when(routineRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> routineActivityService.create(mockInputDTO)
        );
        assertEquals("Rutina no encontrada: 1", exception.getMessage());
    }

    @Test
    @DisplayName("create should throw exception when habit not found")
    void create_WithNonExistentHabit_ShouldThrowException() {
        // Given
        when(routineActivityRepository.existsByRoutine_IdAndHabit_Id(1L, 1L)).thenReturn(false);
        when(routineRepository.findById(1L)).thenReturn(Optional.of(mockRoutine));
        when(habitRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> routineActivityService.create(mockInputDTO)
        );
        assertEquals("Hábito no encontrado: 1", exception.getMessage());
    }

    // Tests for update method
    @Test
    @DisplayName("update should update routine activity successfully")
    void update_WithValidInput_ShouldUpdateSuccessfully() {
        // Given
        Long routineActivityId = 1L;
        RoutineActivityInputDTO updateInput = new RoutineActivityInputDTO();
        updateInput.setDuration(45);
        // routineId and habitId are null, should keep current values

        when(routineActivityRepository.findById(routineActivityId)).thenReturn(Optional.of(mockRoutineActivity));
        when(routineActivityRepository.save(mockRoutineActivity)).thenReturn(mockRoutineActivity);
        when(mapper.toDto(mockRoutineActivity)).thenReturn(mockOutputDTO);

        // When
        RoutineActivityOutputDTO result = routineActivityService.update(routineActivityId, updateInput);

        // Then
        assertNotNull(result);
        verify(mapper).copyToEntity(updateInput, mockRoutineActivity, null, null);
        verify(routineActivityRepository).save(mockRoutineActivity);
    }

    @Test
    @DisplayName("update should throw exception when routine activity not found")
    void update_WithNonExistentRoutineActivity_ShouldThrowException() {
        // Given
        Long routineActivityId = 999L;
        when(routineActivityRepository.findById(routineActivityId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> routineActivityService.update(routineActivityId, mockInputDTO)
        );
        assertEquals("RoutineActivity no encontrada: 999", exception.getMessage());
    }

    @Test
    @DisplayName("update should throw exception when new combination already exists")
    void update_WithExistingCombination_ShouldThrowException() {
        // Given
        Long routineActivityId = 1L;
        RoutineActivityInputDTO updateInput = new RoutineActivityInputDTO();
        updateInput.setRoutineId(2L); // Different routine ID
        updateInput.setHabitId(1L);

        when(routineActivityRepository.findById(routineActivityId)).thenReturn(Optional.of(mockRoutineActivity));
        when(routineActivityRepository.existsByRoutine_IdAndHabit_Id(2L, 1L)).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> routineActivityService.update(routineActivityId, updateInput)
        );
        assertEquals("Ya existe esa actividad para (routineId, habitId).", exception.getMessage());
    }

    @Test
    @DisplayName("update should throw exception when new routine not found")
    void update_WithNonExistentNewRoutine_ShouldThrowException() {
        // Given
        Long routineActivityId = 1L;
        RoutineActivityInputDTO updateInput = new RoutineActivityInputDTO();
        updateInput.setRoutineId(999L);

        when(routineActivityRepository.findById(routineActivityId)).thenReturn(Optional.of(mockRoutineActivity));
        when(routineActivityRepository.existsByRoutine_IdAndHabit_Id(999L, 1L)).thenReturn(false);
        when(routineRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> routineActivityService.update(routineActivityId, updateInput)
        );
        assertEquals("Rutina no encontrada: 999", exception.getMessage());
    }

    @Test
    @DisplayName("update should throw exception when new habit not found")
    void update_WithNonExistentNewHabit_ShouldThrowException() {
        // Given
        Long routineActivityId = 1L;
        RoutineActivityInputDTO updateInput = new RoutineActivityInputDTO();
        updateInput.setHabitId(999L);

        when(routineActivityRepository.findById(routineActivityId)).thenReturn(Optional.of(mockRoutineActivity));
        when(routineActivityRepository.existsByRoutine_IdAndHabit_Id(1L, 999L)).thenReturn(false);
        when(habitRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> routineActivityService.update(routineActivityId, updateInput)
        );
        assertEquals("Hábito no encontrado: 999", exception.getMessage());
    }

    @Test
    @DisplayName("update should throw exception when duration is less than 1")
    void update_WithInvalidDuration_ShouldThrowException() {
        // Given
        Long routineActivityId = 1L;
        RoutineActivityInputDTO updateInput = new RoutineActivityInputDTO();
        updateInput.setDuration(0);

        when(routineActivityRepository.findById(routineActivityId)).thenReturn(Optional.of(mockRoutineActivity));

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> routineActivityService.update(routineActivityId, updateInput)
        );
        assertEquals("duration debe ser >= 1.", exception.getMessage());
    }

    @Test
    @DisplayName("update should update with new routine and habit successfully")
    void update_WithNewRoutineAndHabit_ShouldUpdateSuccessfully() {
        // Given
        Long routineActivityId = 1L;
        Routine newRoutine = new Routine();
        newRoutine.setId(2L);
        Habit newHabit = new Habit();
        newHabit.setId(2L);

        RoutineActivityInputDTO updateInput = new RoutineActivityInputDTO();
        updateInput.setRoutineId(2L);
        updateInput.setHabitId(2L);
        updateInput.setDuration(60);

        when(routineActivityRepository.findById(routineActivityId)).thenReturn(Optional.of(mockRoutineActivity));
        when(routineActivityRepository.existsByRoutine_IdAndHabit_Id(2L, 2L)).thenReturn(false);
        when(routineRepository.findById(2L)).thenReturn(Optional.of(newRoutine));
        when(habitRepository.findById(2L)).thenReturn(Optional.of(newHabit));
        when(routineActivityRepository.save(mockRoutineActivity)).thenReturn(mockRoutineActivity);
        when(mapper.toDto(mockRoutineActivity)).thenReturn(mockOutputDTO);

        // When
        RoutineActivityOutputDTO result = routineActivityService.update(routineActivityId, updateInput);

        // Then
        assertNotNull(result);
        verify(mapper).copyToEntity(updateInput, mockRoutineActivity, newRoutine, newHabit);
        verify(routineActivityRepository).save(mockRoutineActivity);
    }

    // Tests for delete method
    @Test
    @DisplayName("delete should delete routine activity successfully when it exists")
    void delete_WithExistingRoutineActivity_ShouldDeleteSuccessfully() {
        // Given
        Long routineActivityId = 1L;
        when(routineActivityRepository.existsById(routineActivityId)).thenReturn(true);

        // When
        routineActivityService.delete(routineActivityId);

        // Then
        verify(routineActivityRepository).deleteById(routineActivityId);
    }

    @Test
    @DisplayName("delete should throw exception when routine activity doesn't exist")
    void delete_WithNonExistentRoutineActivity_ShouldThrowException() {
        // Given
        Long routineActivityId = 999L;
        when(routineActivityRepository.existsById(routineActivityId)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> routineActivityService.delete(routineActivityId)
        );
        assertEquals("RoutineActivity no encontrada: 999", exception.getMessage());
    }

    // Tests for findById method
    @Test
    @DisplayName("findById should return routine activity when it exists")
    void findById_WithExistingRoutineActivity_ShouldReturnRoutineActivity() {
        // Given
        Long routineActivityId = 1L;
        when(routineActivityRepository.findById(routineActivityId)).thenReturn(Optional.of(mockRoutineActivity));
        when(mapper.toDto(mockRoutineActivity)).thenReturn(mockOutputDTO);

        // When
        RoutineActivityOutputDTO result = routineActivityService.findById(routineActivityId);

        // Then
        assertNotNull(result);
        assertEquals(mockOutputDTO.getId(), result.getId());
        assertEquals(mockOutputDTO.getRoutineId(), result.getRoutineId());
        assertEquals(mockOutputDTO.getHabitId(), result.getHabitId());
        assertEquals(mockOutputDTO.getDuration(), result.getDuration());
    }

    @Test
    @DisplayName("findById should throw exception when routine activity doesn't exist")
    void findById_WithNonExistentRoutineActivity_ShouldThrowException() {
        // Given
        Long routineActivityId = 999L;
        when(routineActivityRepository.findById(routineActivityId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> routineActivityService.findById(routineActivityId)
        );
        assertEquals("RoutineActivity no encontrada: 999", exception.getMessage());
    }

    // Tests for list method
    @Test
    @DisplayName("list should return all routine activities")
    void list_ShouldReturnAllRoutineActivities() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<RoutineActivity> routineActivityPage = new PageImpl<>(List.of(mockRoutineActivity));
        when(routineActivityRepository.findAll(pageable)).thenReturn(routineActivityPage);
        when(mapper.toDto(mockRoutineActivity)).thenReturn(mockOutputDTO);

        // When
        Page<RoutineActivityOutputDTO> result = routineActivityService.list(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(mockOutputDTO.getId(), result.getContent().get(0).getId());
        verify(routineActivityRepository).findAll(pageable);
    }

    @Test
    @DisplayName("list should return empty page when no routine activities exist")
    void list_WithNoRoutineActivities_ShouldReturnEmptyPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<RoutineActivity> emptyPage = new PageImpl<>(List.of());
        when(routineActivityRepository.findAll(pageable)).thenReturn(emptyPage);

        // When
        Page<RoutineActivityOutputDTO> result = routineActivityService.list(pageable);

        // Then
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    // Tests for listByRoutineId method
    @Test
    @DisplayName("listByRoutineId should return routine activities for specific routine")
    void listByRoutineId_WithValidRoutineId_ShouldReturnRoutineActivities() {
        // Given
        Long routineId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<RoutineActivity> routineActivityPage = new PageImpl<>(List.of(mockRoutineActivity));
        when(routineActivityRepository.findByRoutine_Id(routineId, pageable)).thenReturn(routineActivityPage);
        when(mapper.toDto(mockRoutineActivity)).thenReturn(mockOutputDTO);

        // When
        Page<RoutineActivityOutputDTO> result = routineActivityService.listByRoutineId(routineId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(mockOutputDTO.getId(), result.getContent().get(0).getId());
        verify(routineActivityRepository).findByRoutine_Id(routineId, pageable);
    }

    @Test
    @DisplayName("listByRoutineId should return empty page when routine has no activities")
    void listByRoutineId_WithRoutineHavingNoActivities_ShouldReturnEmptyPage() {
        // Given
        Long routineId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<RoutineActivity> emptyPage = new PageImpl<>(List.of());
        when(routineActivityRepository.findByRoutine_Id(routineId, pageable)).thenReturn(emptyPage);

        // When
        Page<RoutineActivityOutputDTO> result = routineActivityService.listByRoutineId(routineId, pageable);

        // Then
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    // Tests for listByHabitId method
    @Test
    @DisplayName("listByHabitId should return routine activities for specific habit")
    void listByHabitId_WithValidHabitId_ShouldReturnRoutineActivities() {
        // Given
        Long habitId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<RoutineActivity> routineActivityPage = new PageImpl<>(List.of(mockRoutineActivity));
        when(routineActivityRepository.findByHabit_Id(habitId, pageable)).thenReturn(routineActivityPage);
        when(mapper.toDto(mockRoutineActivity)).thenReturn(mockOutputDTO);

        // When
        Page<RoutineActivityOutputDTO> result = routineActivityService.listByHabitId(habitId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(mockOutputDTO.getId(), result.getContent().get(0).getId());
        verify(routineActivityRepository).findByHabit_Id(habitId, pageable);
    }

    @Test
    @DisplayName("listByHabitId should return empty page when habit has no activities")
    void listByHabitId_WithHabitHavingNoActivities_ShouldReturnEmptyPage() {
        // Given
        Long habitId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<RoutineActivity> emptyPage = new PageImpl<>(List.of());
        when(routineActivityRepository.findByHabit_Id(habitId, pageable)).thenReturn(emptyPage);

        // When
        Page<RoutineActivityOutputDTO> result = routineActivityService.listByHabitId(habitId, pageable);

        // Then
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    // Additional edge cases
    @Test
    @DisplayName("update should not change keys when same routine and habit are provided")
    void update_WithSameRoutineAndHabit_ShouldNotCheckForDuplicates() {
        // Given
        Long routineActivityId = 1L;
        RoutineActivityInputDTO updateInput = new RoutineActivityInputDTO();
        updateInput.setRoutineId(1L); // Same as current
        updateInput.setHabitId(1L);   // Same as current
        updateInput.setDuration(45);

        when(routineActivityRepository.findById(routineActivityId)).thenReturn(Optional.of(mockRoutineActivity));
        when(routineActivityRepository.save(mockRoutineActivity)).thenReturn(mockRoutineActivity);
        when(mapper.toDto(mockRoutineActivity)).thenReturn(mockOutputDTO);

        // When
        RoutineActivityOutputDTO result = routineActivityService.update(routineActivityId, updateInput);

        // Then
        assertNotNull(result);
        verify(routineActivityRepository, never()).existsByRoutine_IdAndHabit_Id(anyLong(), anyLong());
        verify(routineRepository, never()).findById(anyLong());
        verify(habitRepository, never()).findById(anyLong());
        verify(mapper).copyToEntity(updateInput, mockRoutineActivity, null, null);
    }
}