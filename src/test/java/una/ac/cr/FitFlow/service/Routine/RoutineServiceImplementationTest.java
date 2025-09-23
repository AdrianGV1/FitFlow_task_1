package una.ac.cr.FitFlow.service.Routine;

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

import una.ac.cr.FitFlow.dto.Routine.RoutineInputDTO;
import una.ac.cr.FitFlow.dto.Routine.RoutineOutputDTO;
import una.ac.cr.FitFlow.mapper.MapperForRoutine;
import una.ac.cr.FitFlow.model.Routine;
import una.ac.cr.FitFlow.model.RoutineActivity;
import una.ac.cr.FitFlow.model.User;
import una.ac.cr.FitFlow.repository.RoutineActivityRepository;
import una.ac.cr.FitFlow.repository.RoutineRepository;
import una.ac.cr.FitFlow.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoutineServiceImplementationTest {

    @Mock
    private RoutineRepository routineRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RoutineActivityRepository routineActivityRepository;
    
    @Mock
    private MapperForRoutine mapper;

    @InjectMocks
    private RoutineServiceImplementation routineService;

    private Routine mockRoutine;
    private RoutineInputDTO mockInputDTO;
    private RoutineOutputDTO mockOutputDTO;
    private User mockUser;
    private RoutineActivity mockActivity1;
    private RoutineActivity mockActivity2;

    @BeforeEach
    void setUp() {
        // Setup mock objects
        mockUser = new User();
        mockUser.setId(1L);

        mockActivity1 = new RoutineActivity();
        mockActivity1.setId(1L);

        mockActivity2 = new RoutineActivity();
        mockActivity2.setId(2L);

        mockRoutine = new Routine();
        mockRoutine.setId(1L);
        mockRoutine.setUser(mockUser);
        mockRoutine.setActivities(new ArrayList<>());

        mockInputDTO = new RoutineInputDTO();
        mockInputDTO.setTitle("Morning Workout");
        mockInputDTO.setUserId(1L);
        mockInputDTO.setDaysOfWeek(List.of(Routine.DaysOfWeek.MON, Routine.DaysOfWeek.WED, Routine.DaysOfWeek.FRI));
        mockInputDTO.setActivityIds(List.of(1L, 2L));

        mockOutputDTO = RoutineOutputDTO.builder()
                .id(1L)
                .title("Morning Workout")
                .userId(1L)
                .daysOfWeek(List.of(Routine.DaysOfWeek.MON, Routine.DaysOfWeek.WED, Routine.DaysOfWeek.FRI))
                .build();
    }

    // Tests para el método create
    @Test
    @DisplayName("create should create routine successfully with valid input")
    void create_WithValidInput_ShouldCreateSuccessfully() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(mapper.toEntity(mockInputDTO, mockUser)).thenReturn(mockRoutine);
        when(routineRepository.save(mockRoutine)).thenReturn(mockRoutine);
        when(routineActivityRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(mockActivity1, mockActivity2));
        when(mapper.toDto(mockRoutine)).thenReturn(mockOutputDTO);

        // When
        RoutineOutputDTO result = routineService.create(mockInputDTO);

        // Then
        assertNotNull(result);
        assertEquals(mockOutputDTO.getId(), result.getId());
        assertEquals(mockOutputDTO.getTitle(), result.getTitle());
        assertEquals(mockOutputDTO.getUserId(), result.getUserId());
        assertEquals(mockOutputDTO.getDaysOfWeek(), result.getDaysOfWeek());
        
        verify(routineRepository).save(mockRoutine);
        verify(routineActivityRepository).findAllById(List.of(1L, 2L));
        verify(mapper).toDto(mockRoutine);
    }

    @Test
    @DisplayName("create should throw exception when title is null")
    void create_WithNullTitle_ShouldThrowException() {
        // Given
        mockInputDTO.setTitle(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> routineService.create(mockInputDTO)
        );
        assertEquals("El título es obligatorio.", exception.getMessage());
    }

    @Test
    @DisplayName("create should throw exception when user not found")
    void create_WithNonExistentUser_ShouldThrowException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> routineService.create(mockInputDTO)
        );
        assertEquals("Usuario no encontrado: 1", exception.getMessage());
    }

    // Tests para el método update
    @Test
    @DisplayName("update should update routine successfully")
    void update_WithValidInput_ShouldUpdateSuccessfully() {
        // Given
        Long routineId = 1L;
        RoutineInputDTO updateInput = new RoutineInputDTO();
        updateInput.setTitle("Updated Workout");
        updateInput.setActivityIds(List.of(1L));

        when(routineRepository.findById(routineId)).thenReturn(Optional.of(mockRoutine));
        when(routineActivityRepository.findAllById(List.of(1L))).thenReturn(List.of(mockActivity1));
        when(routineRepository.save(mockRoutine)).thenReturn(mockRoutine);
        when(mapper.toDto(mockRoutine)).thenReturn(mockOutputDTO);

        // When
        RoutineOutputDTO result = routineService.update(routineId, updateInput);

        // Then
        assertNotNull(result);
        verify(routineRepository).save(mockRoutine);
    }

    @Test
    @DisplayName("update should throw exception when routine not found")
    void update_WithNonExistentRoutine_ShouldThrowException() {
        // Given
        Long routineId = 999L;
        when(routineRepository.findById(routineId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> routineService.update(routineId, mockInputDTO)
        );
        assertEquals("Routine no encontrada: 999", exception.getMessage());
    }

    // Tests para el método delete
    @Test
    @DisplayName("delete should delete routine successfully when it exists")
    void delete_WithExistingRoutine_ShouldDeleteSuccessfully() {
        // Given
        Long routineId = 1L;
        when(routineRepository.findById(routineId)).thenReturn(Optional.of(mockRoutine));

        // When
        routineService.delete(routineId);

        // Then
        verify(routineRepository).delete(mockRoutine);
    }

    // Tests para el método findById
    @Test
    @DisplayName("findById should return routine when it exists")
    void findById_WithExistingRoutine_ShouldReturnRoutine() {
        // Given
        Long routineId = 1L;
        when(routineRepository.findById(routineId)).thenReturn(Optional.of(mockRoutine));
        when(mapper.toDto(mockRoutine)).thenReturn(mockOutputDTO);

        // When
        RoutineOutputDTO result = routineService.findById(routineId);

        // Then
        assertNotNull(result);
        assertEquals(mockOutputDTO.getId(), result.getId());
    }

    // Tests para el método list - CORREGIDOS
    @Test
    @DisplayName("list should return all routines when query is null")
    void list_WithNullQuery_ShouldReturnAllRoutines() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Routine> routinePage = new PageImpl<>(List.of(mockRoutine));
        
        // Usar any(Pageable.class) para evitar la ambigüedad
        when(routineRepository.findAll(any(Pageable.class))).thenReturn(routinePage);
        when(mapper.toDto(mockRoutine)).thenReturn(mockOutputDTO);

        // When
        Page<RoutineOutputDTO> result = routineService.list(null, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(routineRepository).findAll(pageable);
    }

    @Test
    @DisplayName("list should search routines when query is provided")
    void list_WithQuery_ShouldSearchRoutines() {
        // Given
        String query = "morning";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Routine> routinePage = new PageImpl<>(List.of(mockRoutine));
        
        // Especificar explícitamente los tipos de parámetros
        when(routineRepository.findByTitleContainingIgnoreCase(eq("morning"), any(Pageable.class)))
            .thenReturn(routinePage);
        when(mapper.toDto(mockRoutine)).thenReturn(mockOutputDTO);

        // When
        Page<RoutineOutputDTO> result = routineService.list(query, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(routineRepository).findByTitleContainingIgnoreCase("morning", pageable);
    }

    // Tests para listByUserId - CORREGIDOS
    @Test
    @DisplayName("listByUserId should return routines for specific user")
    void listByUserId_WithValidUserId_ShouldReturnUserRoutines() {
        // Given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Routine> routinePage = new PageImpl<>(List.of(mockRoutine));
        
        // Usar any(Pageable.class) para evitar ambigüedad
        when(routineRepository.findByUserId(eq(userId), any(Pageable.class))).thenReturn(routinePage);
        when(mapper.toDto(mockRoutine)).thenReturn(mockOutputDTO);

        // When
        Page<RoutineOutputDTO> result = routineService.listByUserId(userId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(routineRepository).findByUserId(userId, pageable);
    }

    // Tests adicionales corregidos
    @Test
    @DisplayName("list should return empty page when no routines found")
    void list_WithNoResults_ShouldReturnEmptyPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Routine> emptyPage = new PageImpl<>(List.of());
        
        // Usar any(Pageable.class) para evitar ambigüedad
        when(routineRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // When
        Page<RoutineOutputDTO> result = routineService.list(null, pageable);

        // Then
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    @DisplayName("listByUserId should return empty page when user has no routines")
    void listByUserId_WithUserHavingNoRoutines_ShouldReturnEmptyPage() {
        // Given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Routine> emptyPage = new PageImpl<>(List.of());
        
        // Usar any(Pageable.class) para evitar ambigüedad
        when(routineRepository.findByUserId(eq(userId), any(Pageable.class))).thenReturn(emptyPage);

        // When
        Page<RoutineOutputDTO> result = routineService.listByUserId(userId, pageable);

        // Then
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    // Mantén los otros tests que no tienen problemas de ambigüedad...
}