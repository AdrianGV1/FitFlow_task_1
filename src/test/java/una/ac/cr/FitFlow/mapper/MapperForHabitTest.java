package una.ac.cr.FitFlow.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import una.ac.cr.FitFlow.dto.Habit.HabitInputDTO;
import una.ac.cr.FitFlow.dto.Habit.HabitOutputDTO;
import una.ac.cr.FitFlow.model.Habit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MapperForHabitTest {

    @InjectMocks
    private MapperForHabit mapper;

    private Habit mockHabit;
    private HabitInputDTO mockHabitInput;

    @BeforeEach
    void setUp() {
        // Setup mock Habit entity
        mockHabit = Habit.builder()
                .id(1L)
                .name("Morning Exercise")
                .category(Habit.Category.PHYSICAL)
                .description("30 minutes of cardio every morning")
                .build();

        // Setup mock HabitInputDTO
        mockHabitInput = new HabitInputDTO();
        mockHabitInput.setName("Morning Exercise");
        mockHabitInput.setCategory("PHYSICAL");
        mockHabitInput.setDescription("30 minutes of cardio every morning");
    }

    // Tests for toDto method
    @Test
    @DisplayName("toDto should convert Habit to HabitOutputDTO successfully")
    void toDto_WithValidHabit_ShouldReturnHabitOutputDTO() {
        // When
        HabitOutputDTO result = mapper.toDto(mockHabit);

        // Then
        assertNotNull(result);
        assertEquals(mockHabit.getId(), result.getId());
        assertEquals(mockHabit.getName(), result.getName());
        assertEquals(mockHabit.getCategory().name(), result.getCategory());
        assertEquals(mockHabit.getDescription(), result.getDescription());
    }

    @Test
    @DisplayName("toDto should return null when Habit is null")
    void toDto_WithNullHabit_ShouldReturnNull() {
        // When
        HabitOutputDTO result = mapper.toDto(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("toDto should handle null category")
    void toDto_WithNullCategory_ShouldReturnNullCategory() {
        // Given
        mockHabit.setCategory(null);

        // When
        HabitOutputDTO result = mapper.toDto(mockHabit);

        // Then
        assertNotNull(result);
        assertEquals(mockHabit.getId(), result.getId());
        assertEquals(mockHabit.getName(), result.getName());
        assertNull(result.getCategory());
        assertEquals(mockHabit.getDescription(), result.getDescription());
    }

    @Test
    @DisplayName("toDto should convert all valid categories correctly")
    void toDto_WithAllCategories_ShouldConvertCorrectly() {
        // Test PHYSICAL
        mockHabit.setCategory(Habit.Category.PHYSICAL);
        HabitOutputDTO result = mapper.toDto(mockHabit);
        assertEquals("PHYSICAL", result.getCategory());

        // Test MENTAL
        mockHabit.setCategory(Habit.Category.MENTAL);
        result = mapper.toDto(mockHabit);
        assertEquals("MENTAL", result.getCategory());

        // Test SLEEP
        mockHabit.setCategory(Habit.Category.SLEEP);
        result = mapper.toDto(mockHabit);
        assertEquals("SLEEP", result.getCategory());

        // Test DIET
        mockHabit.setCategory(Habit.Category.DIET);
        result = mapper.toDto(mockHabit);
        assertEquals("DIET", result.getCategory());
    }

    // Tests for toEntity method
    @Test
    @DisplayName("toEntity should convert HabitInputDTO to Habit successfully")
    void toEntity_WithValidHabitInputDTO_ShouldReturnHabit() {
        // When
        Habit result = mapper.toEntity(mockHabitInput);

        // Then
        assertNotNull(result);
        assertEquals(mockHabitInput.getName(), result.getName());
        assertEquals(Habit.Category.PHYSICAL, result.getCategory());
        assertEquals(mockHabitInput.getDescription(), result.getDescription());
        assertNull(result.getId()); // ID should not be set in toEntity
    }

    @Test
    @DisplayName("toEntity should return null when HabitInputDTO is null")
    void toEntity_WithNullHabitInputDTO_ShouldReturnNull() {
        // When
        Habit result = mapper.toEntity(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("toEntity should convert all valid string categories correctly")
    void toEntity_WithAllStringCategories_ShouldConvertCorrectly() {
        // Test PHYSICAL
        mockHabitInput.setCategory("PHYSICAL");
        Habit result = mapper.toEntity(mockHabitInput);
        assertEquals(Habit.Category.PHYSICAL, result.getCategory());

        // Test MENTAL
        mockHabitInput.setCategory("MENTAL");
        result = mapper.toEntity(mockHabitInput);
        assertEquals(Habit.Category.MENTAL, result.getCategory());

        // Test SLEEP
        mockHabitInput.setCategory("SLEEP");
        result = mapper.toEntity(mockHabitInput);
        assertEquals(Habit.Category.SLEEP, result.getCategory());

        // Test DIET
        mockHabitInput.setCategory("DIET");
        result = mapper.toEntity(mockHabitInput);
        assertEquals(Habit.Category.DIET, result.getCategory());
    }

    @Test
    @DisplayName("toEntity should handle lowercase category strings")
    void toEntity_WithLowercaseCategory_ShouldConvertCorrectly() {
        // Given
        mockHabitInput.setCategory("physical");

        // When
        Habit result = mapper.toEntity(mockHabitInput);

        // Then
        assertEquals(Habit.Category.PHYSICAL, result.getCategory());
    }

    @Test
    @DisplayName("toEntity should handle mixed case category strings")
    void toEntity_WithMixedCaseCategory_ShouldConvertCorrectly() {
        // Given
        mockHabitInput.setCategory("PhYsIcAl");

        // When
        Habit result = mapper.toEntity(mockHabitInput);

        // Then
        assertEquals(Habit.Category.PHYSICAL, result.getCategory());
    }

    @Test
    @DisplayName("toEntity should handle category strings with whitespace")
    void toEntity_WithCategoryWithWhitespace_ShouldConvertCorrectly() {
        // Given
        mockHabitInput.setCategory("  PHYSICAL  ");

        // When
        Habit result = mapper.toEntity(mockHabitInput);

        // Then
        assertEquals(Habit.Category.PHYSICAL, result.getCategory());
    }

    @Test
    @DisplayName("toEntity should throw exception when category is null")
    void toEntity_WithNullCategory_ShouldThrowException() {
        // Given
        mockHabitInput.setCategory(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> mapper.toEntity(mockHabitInput)
        );
        assertEquals("La categoría es obligatoria.", exception.getMessage());
    }

    @Test
    @DisplayName("toEntity should throw exception when category is empty")
    void toEntity_WithEmptyCategory_ShouldThrowException() {
        // Given
        mockHabitInput.setCategory("");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> mapper.toEntity(mockHabitInput)
        );
        assertEquals("La categoría es obligatoria.", exception.getMessage());
    }

    @Test
    @DisplayName("toEntity should throw exception when category is blank")
    void toEntity_WithBlankCategory_ShouldThrowException() {
        // Given
        mockHabitInput.setCategory("   ");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> mapper.toEntity(mockHabitInput)
        );
        assertEquals("La categoría es obligatoria.", exception.getMessage());
    }

    @Test
    @DisplayName("toEntity should throw exception when category is invalid")
    void toEntity_WithInvalidCategory_ShouldThrowException() {
        // Given
        mockHabitInput.setCategory("INVALID_CATEGORY");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> mapper.toEntity(mockHabitInput)
        );
        assertEquals("Categoría inválida: INVALID_CATEGORY. Use PHYSICAL, MENTAL, SLEEP o DIET.", exception.getMessage());
    }

    // Tests for copyToEntity method
    @Test
    @DisplayName("copyToEntity should update all fields when all are provided")
    void copyToEntity_WithAllFields_ShouldUpdateAllFields() {
        // Given
        Habit target = new Habit();
        target.setId(1L);
        target.setName("Old Name");
        target.setCategory(Habit.Category.MENTAL);
        target.setDescription("Old Description");

        // When
        mapper.copyToEntity(mockHabitInput, target);

        // Then
        assertEquals(1L, target.getId()); // ID should remain unchanged
        assertEquals(mockHabitInput.getName(), target.getName());
        assertEquals(Habit.Category.PHYSICAL, target.getCategory());
        assertEquals(mockHabitInput.getDescription(), target.getDescription());
    }

    @Test
    @DisplayName("copyToEntity should only update name when only name is provided")
    void copyToEntity_WithOnlyName_ShouldUpdateOnlyName() {
        // Given
        Habit target = new Habit();
        target.setId(1L);
        target.setName("Old Name");
        target.setCategory(Habit.Category.MENTAL);
        target.setDescription("Old Description");

        HabitInputDTO input = new HabitInputDTO();
        input.setName("New Name");
        // category and description are null

        // When
        mapper.copyToEntity(input, target);

        // Then
        assertEquals(1L, target.getId());
        assertEquals("New Name", target.getName());
        assertEquals(Habit.Category.MENTAL, target.getCategory()); // Unchanged
        assertEquals("Old Description", target.getDescription()); // Unchanged
    }

    @Test
    @DisplayName("copyToEntity should only update category when only category is provided")
    void copyToEntity_WithOnlyCategory_ShouldUpdateOnlyCategory() {
        // Given
        Habit target = new Habit();
        target.setId(1L);
        target.setName("Old Name");
        target.setCategory(Habit.Category.MENTAL);
        target.setDescription("Old Description");

        HabitInputDTO input = new HabitInputDTO();
        input.setCategory("PHYSICAL");
        // name and description are null

        // When
        mapper.copyToEntity(input, target);

        // Then
        assertEquals(1L, target.getId());
        assertEquals("Old Name", target.getName()); // Unchanged
        assertEquals(Habit.Category.PHYSICAL, target.getCategory());
        assertEquals("Old Description", target.getDescription()); // Unchanged
    }

    @Test
    @DisplayName("copyToEntity should only update description when only description is provided")
    void copyToEntity_WithOnlyDescription_ShouldUpdateOnlyDescription() {
        // Given
        Habit target = new Habit();
        target.setId(1L);
        target.setName("Old Name");
        target.setCategory(Habit.Category.MENTAL);
        target.setDescription("Old Description");

        HabitInputDTO input = new HabitInputDTO();
        input.setDescription("New Description");
        // name and category are null

        // When
        mapper.copyToEntity(input, target);

        // Then
        assertEquals(1L, target.getId());
        assertEquals("Old Name", target.getName()); // Unchanged
        assertEquals(Habit.Category.MENTAL, target.getCategory()); // Unchanged
        assertEquals("New Description", target.getDescription());
    }

    @Test
    @DisplayName("copyToEntity should not update any field when all inputs are null")
    void copyToEntity_WithAllNullFields_ShouldNotUpdateAnyField() {
        // Given
        Habit target = new Habit();
        target.setId(1L);
        target.setName("Old Name");
        target.setCategory(Habit.Category.MENTAL);
        target.setDescription("Old Description");

        HabitInputDTO input = new HabitInputDTO();
        // All fields are null

        // When
        mapper.copyToEntity(input, target);

        // Then
        assertEquals(1L, target.getId());
        assertEquals("Old Name", target.getName());
        assertEquals(Habit.Category.MENTAL, target.getCategory());
        assertEquals("Old Description", target.getDescription());
    }

    @Test
    @DisplayName("copyToEntity should throw exception when category is invalid")
    void copyToEntity_WithInvalidCategory_ShouldThrowException() {
        // Given
        Habit target = new Habit();
        HabitInputDTO input = new HabitInputDTO();
        input.setCategory("INVALID");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> mapper.copyToEntity(input, target)
        );
        assertEquals("Categoría inválida: INVALID. Use PHYSICAL, MENTAL, SLEEP o DIET.", exception.getMessage());
    }

    // Tests for parseCategory method (tested indirectly through toEntity and copyToEntity)
    @Test
    @DisplayName("parseCategory should handle all edge cases through toEntity")
    void parseCategory_EdgeCases_ThroughToEntity() {
        // Test with leading/trailing spaces and mixed case
        mockHabitInput.setCategory("  mEnTaL  ");
        Habit result = mapper.toEntity(mockHabitInput);
        assertEquals(Habit.Category.MENTAL, result.getCategory());

        // Test exact match
        mockHabitInput.setCategory("SLEEP");
        result = mapper.toEntity(mockHabitInput);
        assertEquals(Habit.Category.SLEEP, result.getCategory());

        // Test lowercase
        mockHabitInput.setCategory("diet");
        result = mapper.toEntity(mockHabitInput);
        assertEquals(Habit.Category.DIET, result.getCategory());
    }
}