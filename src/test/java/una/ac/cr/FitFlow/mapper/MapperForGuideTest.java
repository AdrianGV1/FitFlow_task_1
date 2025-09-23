package una.ac.cr.FitFlow.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import una.ac.cr.FitFlow.dto.Guide.GuideInputDTO;
import una.ac.cr.FitFlow.dto.Guide.GuideOutputDTO;
import una.ac.cr.FitFlow.model.Guide;
import una.ac.cr.FitFlow.model.Habit;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MapperForGuideTest {

    private MapperForGuide mapper;
    
    @Mock
    private Habit mockHabit1;
    
    @Mock
    private Habit mockHabit2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = new MapperForGuide();
    }

    // Tests for toDto method
    @Test
    @DisplayName("toDto should return null when input is null")
    void toDto_WhenInputIsNull_ShouldReturnNull() {
        // When
        GuideOutputDTO result = mapper.toDto(null);
        
        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("toDto should map all fields correctly with full Guide")
    void toDto_WithFullGuide_ShouldMapAllFields() {
        // Given
        Long guideId = 1L;
        String title = "Test Guide";
        String content = "Test content";
        Guide.Category category = Guide.Category.PHYSICAL;
        Long habit1Id = 2L;
        Long habit2Id = 3L;

        when(mockHabit1.getId()).thenReturn(habit1Id);
        when(mockHabit2.getId()).thenReturn(habit2Id);

        Guide guide = Guide.builder()
                .id(guideId)
                .title(title)
                .content(content)
                .category(category)
                .recommendedHabits(Set.of(mockHabit1, mockHabit2))
                .build();

        // When
        GuideOutputDTO result = mapper.toDto(guide);

        // Then
        assertNotNull(result);
        assertEquals(guideId, result.getId());
        assertEquals(title, result.getTitle());
        assertEquals(content, result.getContent());
        assertEquals("PHYSICAL", result.getCategory());
        assertEquals(2, result.getRecommendedHabitIds().size());
        assertTrue(result.getRecommendedHabitIds().contains(habit1Id));
        assertTrue(result.getRecommendedHabitIds().contains(habit2Id));
    }

    @Test
    @DisplayName("toDto should handle null category")
    void toDto_WithNullCategory_ShouldSetCategoryToNull() {
        // Given
        Guide guide = Guide.builder()
                .id(1L)
                .title("Test Guide")
                .content("Test content")
                .category(null)
                .recommendedHabits(new HashSet<>())
                .build();

        // When
        GuideOutputDTO result = mapper.toDto(guide);

        // Then
        assertNotNull(result);
        assertNull(result.getCategory());
    }

    @Test
    @DisplayName("toDto should handle empty recommended habits")
    void toDto_WithEmptyRecommendedHabits_ShouldReturnEmptySet() {
        // Given
        Guide guide = Guide.builder()
                .id(1L)
                .title("Test Guide")
                .content("Test content")
                .category(Guide.Category.MENTAL)
                .recommendedHabits(new HashSet<>())
                .build();

        // When
        GuideOutputDTO result = mapper.toDto(guide);

        // Then
        assertNotNull(result);
        assertEquals("MENTAL", result.getCategory());
        assertTrue(result.getRecommendedHabitIds().isEmpty());
    }

    // Tests for toEntity method
    @Test
    @DisplayName("toEntity should return null when input is null")
    void toEntity_WhenInputIsNull_ShouldReturnNull() {
        // When
        Guide result = mapper.toEntity(null);
        
        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("toEntity should create Guide with all fields from DTO")
    void toEntity_WithValidInput_ShouldCreateGuideWithAllFields() {
        // Given
        GuideInputDTO input = new GuideInputDTO();
        input.setTitle("Test Guide");
        input.setContent("Test content");
        input.setCategory("PHYSICAL");

        // When
        Guide result = mapper.toEntity(input);

        // Then
        assertNotNull(result);
        assertEquals("Test Guide", result.getTitle());
        assertEquals("Test content", result.getContent());
        assertEquals(Guide.Category.PHYSICAL, result.getCategory());
    }

    @Test
    @DisplayName("toEntity should handle lowercase category")
    void toEntity_WithLowercaseCategory_ShouldConvertToUppercase() {
        // Given
        GuideInputDTO input = new GuideInputDTO();
        input.setTitle("Test Guide");
        input.setContent("Test content");
        input.setCategory("mental");

        // When
        Guide result = mapper.toEntity(input);

        // Then
        assertNotNull(result);
        assertEquals(Guide.Category.MENTAL, result.getCategory());
    }

    @Test
    @DisplayName("toEntity should handle category with extra spaces")
    void toEntity_WithCategoryWithSpaces_ShouldTrimAndConvert() {
        // Given
        GuideInputDTO input = new GuideInputDTO();
        input.setTitle("Test Guide");
        input.setContent("Test content");
        input.setCategory("  sleep  ");

        // When
        Guide result = mapper.toEntity(input);

        // Then
        assertNotNull(result);
        assertEquals(Guide.Category.SLEEP, result.getCategory());
    }

    @Test
    @DisplayName("toEntity should throw exception for null category")
    void toEntity_WithNullCategory_ShouldThrowException() {
        // Given
        GuideInputDTO input = new GuideInputDTO();
        input.setTitle("Test Guide");
        input.setContent("Test content");
        input.setCategory(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> mapper.toEntity(input)
        );
        assertEquals("La categoría es obligatoria.", exception.getMessage());
    }

    @Test
    @DisplayName("toEntity should throw exception for blank category")
    void toEntity_WithBlankCategory_ShouldThrowException() {
        // Given
        GuideInputDTO input = new GuideInputDTO();
        input.setTitle("Test Guide");
        input.setContent("Test content");
        input.setCategory("   ");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> mapper.toEntity(input)
        );
        assertEquals("La categoría es obligatoria.", exception.getMessage());
    }

    @Test
    @DisplayName("toEntity should throw exception for invalid category")
    void toEntity_WithInvalidCategory_ShouldThrowException() {
        // Given
        GuideInputDTO input = new GuideInputDTO();
        input.setTitle("Test Guide");
        input.setContent("Test content");
        input.setCategory("INVALID_CATEGORY");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> mapper.toEntity(input)
        );
        assertTrue(exception.getMessage().contains("Categoría inválida: INVALID_CATEGORY"));
        assertTrue(exception.getMessage().contains("Use PHYSICAL, MENTAL, SLEEP o DIET"));
    }

    // Tests for copyToEntity method
    @Test
    @DisplayName("copyToEntity should copy all non-null fields from input to target")
    void copyToEntity_WithAllNonNullFields_ShouldCopyAllFields() {
        // Given
        GuideInputDTO input = new GuideInputDTO();
        input.setTitle("Updated Title");
        input.setContent("Updated Content");
        input.setCategory("DIET");

        Guide target = Guide.builder()
                .title("Original Title")
                .content("Original Content")
                .category(Guide.Category.PHYSICAL)
                .build();

        // When
        mapper.copyToEntity(input, target);

        // Then
        assertEquals("Updated Title", target.getTitle());
        assertEquals("Updated Content", target.getContent());
        assertEquals(Guide.Category.DIET, target.getCategory());
    }

    @Test
    @DisplayName("copyToEntity should not modify target when title is null")
    void copyToEntity_WithNullTitle_ShouldNotModifyTitle() {
        // Given
        String originalTitle = "Original Title";
        GuideInputDTO input = new GuideInputDTO();
        input.setTitle(null);
        input.setContent("Updated Content");
        input.setCategory("MENTAL");

        Guide target = Guide.builder()
                .title(originalTitle)
                .content("Original Content")
                .category(Guide.Category.PHYSICAL)
                .build();

        // When
        mapper.copyToEntity(input, target);

        // Then
        assertEquals(originalTitle, target.getTitle()); // Should remain unchanged
        assertEquals("Updated Content", target.getContent());
        assertEquals(Guide.Category.MENTAL, target.getCategory());
    }

    @Test
    @DisplayName("copyToEntity should not modify target when content is null")
    void copyToEntity_WithNullContent_ShouldNotModifyContent() {
        // Given
        String originalContent = "Original Content";
        GuideInputDTO input = new GuideInputDTO();
        input.setTitle("Updated Title");
        input.setContent(null);
        input.setCategory("SLEEP");

        Guide target = Guide.builder()
                .title("Original Title")
                .content(originalContent)
                .category(Guide.Category.PHYSICAL)
                .build();

        // When
        mapper.copyToEntity(input, target);

        // Then
        assertEquals("Updated Title", target.getTitle());
        assertEquals(originalContent, target.getContent()); // Should remain unchanged
        assertEquals(Guide.Category.SLEEP, target.getCategory());
    }

    @Test
    @DisplayName("copyToEntity should not modify target when category is null")
    void copyToEntity_WithNullCategory_ShouldNotModifyCategory() {
        // Given
        Guide.Category originalCategory = Guide.Category.PHYSICAL;
        GuideInputDTO input = new GuideInputDTO();
        input.setTitle("Updated Title");
        input.setContent("Updated Content");
        input.setCategory(null);

        Guide target = Guide.builder()
                .title("Original Title")
                .content("Original Content")
                .category(originalCategory)
                .build();

        // When
        mapper.copyToEntity(input, target);

        // Then
        assertEquals("Updated Title", target.getTitle());
        assertEquals("Updated Content", target.getContent());
        assertEquals(originalCategory, target.getCategory()); // Should remain unchanged
    }

    @Test
    @DisplayName("copyToEntity should throw exception when copying invalid category")
    void copyToEntity_WithInvalidCategory_ShouldThrowException() {
        // Given
        GuideInputDTO input = new GuideInputDTO();
        input.setTitle("Updated Title");
        input.setContent("Updated Content");
        input.setCategory("INVALID");

        Guide target = Guide.builder()
                .title("Original Title")
                .content("Original Content")
                .category(Guide.Category.PHYSICAL)
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> mapper.copyToEntity(input, target)
        );
        assertTrue(exception.getMessage().contains("Categoría inválida: INVALID"));
    }

    @Test
    @DisplayName("copyToEntity should handle empty strings correctly")
    void copyToEntity_WithEmptyStrings_ShouldSetEmptyStrings() {
        // Given
        GuideInputDTO input = new GuideInputDTO();
        input.setTitle("");
        input.setContent("");
        input.setCategory("PHYSICAL");

        Guide target = Guide.builder()
                .title("Original Title")
                .content("Original Content")
                .category(Guide.Category.MENTAL)
                .build();

        // When
        mapper.copyToEntity(input, target);

        // Then
        assertEquals("", target.getTitle());
        assertEquals("", target.getContent());
        assertEquals(Guide.Category.PHYSICAL, target.getCategory());
    }

    // Tests for all valid categories
    @Test
    @DisplayName("toEntity should handle all valid categories")
    void toEntity_WithAllValidCategories_ShouldParseCorrectly() {
        // Test all valid categories
        String[] validCategories = {"PHYSICAL", "MENTAL", "SLEEP", "DIET"};
        Guide.Category[] expectedCategories = {
            Guide.Category.PHYSICAL, 
            Guide.Category.MENTAL, 
            Guide.Category.SLEEP, 
            Guide.Category.DIET
        };

        for (int i = 0; i < validCategories.length; i++) {
            // Given
            GuideInputDTO input = new GuideInputDTO();
            input.setTitle("Test Guide");
            input.setContent("Test content");
            input.setCategory(validCategories[i]);

            // When
            Guide result = mapper.toEntity(input);

            // Then
            assertEquals(expectedCategories[i], result.getCategory(), 
                "Failed for category: " + validCategories[i]);
        }
    }
}