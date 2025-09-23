package una.ac.cr.FitFlow.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import una.ac.cr.FitFlow.dto.CompletedActivity.CompletedActivityInputDTO;
import una.ac.cr.FitFlow.dto.CompletedActivity.CompletedActivityOutputDTO;
import una.ac.cr.FitFlow.model.CompletedActivity;
import una.ac.cr.FitFlow.model.ProgressLog;
import una.ac.cr.FitFlow.model.Habit;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MapperForCompletedActivityTest {

    private MapperForCompletedActivity mapper;
    
    @Mock
    private ProgressLog mockProgressLog;
    
    @Mock
    private Habit mockHabit;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = new MapperForCompletedActivity();
    }

    @Test
    @DisplayName("toDto should return null when input is null")
    void toDto_WhenInputIsNull_ShouldReturnNull() {
        // When
        CompletedActivityOutputDTO result = mapper.toDto(null);
        
        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("toDto should map all fields correctly with full CompletedActivity")
    void toDto_WithFullCompletedActivity_ShouldMapAllFields() {
        // Given
        Long activityId = 1L;
        OffsetDateTime completedAt = OffsetDateTime.now();
        String notes = "Test notes";
        Long progressLogId = 2L;
        Long habitId = 3L;

        when(mockProgressLog.getId()).thenReturn(progressLogId);
        when(mockHabit.getId()).thenReturn(habitId);

        CompletedActivity completedActivity = new CompletedActivity();
        completedActivity.setId(activityId);
        completedActivity.setCompletedAt(completedAt);
        completedActivity.setNotes(notes);
        completedActivity.setProgressLog(mockProgressLog);
        completedActivity.setHabit(mockHabit);

        // When
        CompletedActivityOutputDTO result = mapper.toDto(completedActivity);

        // Then
        assertNotNull(result);
        assertEquals(activityId, result.getId());
        assertEquals(completedAt, result.getCompletedAt());
        assertEquals(notes, result.getNotes());
        assertEquals(progressLogId, result.getProgressLogId());
        assertEquals(habitId, result.getHabitId());
    }

    @Test
    @DisplayName("toDto should handle null ProgressLog")
    void toDto_WithNullProgressLog_ShouldSetProgressLogIdToNull() {
        // Given
        CompletedActivity completedActivity = new CompletedActivity();
        completedActivity.setId(1L);
        completedActivity.setCompletedAt(OffsetDateTime.now());
        completedActivity.setNotes("Test notes");
        completedActivity.setProgressLog(null);
        completedActivity.setHabit(mockHabit);
        
        when(mockHabit.getId()).thenReturn(3L);

        // When
        CompletedActivityOutputDTO result = mapper.toDto(completedActivity);

        // Then
        assertNotNull(result);
        assertNull(result.getProgressLogId());
        assertEquals(3L, result.getHabitId());
    }

    @Test
    @DisplayName("toDto should handle null Habit")
    void toDto_WithNullHabit_ShouldSetHabitIdToNull() {
        // Given
        CompletedActivity completedActivity = new CompletedActivity();
        completedActivity.setId(1L);
        completedActivity.setCompletedAt(OffsetDateTime.now());
        completedActivity.setNotes("Test notes");
        completedActivity.setProgressLog(mockProgressLog);
        completedActivity.setHabit(null);
        
        when(mockProgressLog.getId()).thenReturn(2L);

        // When
        CompletedActivityOutputDTO result = mapper.toDto(completedActivity);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getProgressLogId());
        assertNull(result.getHabitId());
    }

    @Test
    @DisplayName("toDto should handle both ProgressLog and Habit as null")
    void toDto_WithBothProgressLogAndHabitNull_ShouldSetBothIdsToNull() {
        // Given
        CompletedActivity completedActivity = new CompletedActivity();
        completedActivity.setId(1L);
        completedActivity.setCompletedAt(OffsetDateTime.now());
        completedActivity.setNotes("Test notes");
        completedActivity.setProgressLog(null);
        completedActivity.setHabit(null);

        // When
        CompletedActivityOutputDTO result = mapper.toDto(completedActivity);

        // Then
        assertNotNull(result);
        assertNull(result.getProgressLogId());
        assertNull(result.getHabitId());
    }

    @Test
    @DisplayName("copyBasics should copy all non-null fields from input to target")
    void copyBasics_WithAllNonNullFields_ShouldCopyAllFields() {
        // Given
        OffsetDateTime completedAt = OffsetDateTime.now();
        String notes = "Updated notes";
        
        CompletedActivityInputDTO input = new CompletedActivityInputDTO();
        input.setCompletedAt(completedAt);
        input.setNotes(notes);

        CompletedActivity target = new CompletedActivity();

        // When
        mapper.copyBasics(input, target);

        // Then
        assertEquals(completedAt, target.getCompletedAt());
        assertEquals(notes, target.getNotes());
    }

    @Test
    @DisplayName("copyBasics should not modify target when completedAt is null")
    void copyBasics_WithNullCompletedAt_ShouldNotModifyCompletedAt() {
        // Given
        OffsetDateTime originalCompletedAt = OffsetDateTime.now().minusHours(1);
        String notes = "Updated notes";
        
        CompletedActivityInputDTO input = new CompletedActivityInputDTO();
        input.setCompletedAt(null);
        input.setNotes(notes);

        CompletedActivity target = new CompletedActivity();
        target.setCompletedAt(originalCompletedAt);

        // When
        mapper.copyBasics(input, target);

        // Then
        assertEquals(originalCompletedAt, target.getCompletedAt()); // Should remain unchanged
        assertEquals(notes, target.getNotes());
    }

    @Test
    @DisplayName("copyBasics should not modify target when notes is null")
    void copyBasics_WithNullNotes_ShouldNotModifyNotes() {
        // Given
        OffsetDateTime completedAt = OffsetDateTime.now();
        String originalNotes = "Original notes";
        
        CompletedActivityInputDTO input = new CompletedActivityInputDTO();
        input.setCompletedAt(completedAt);
        input.setNotes(null);

        CompletedActivity target = new CompletedActivity();
        target.setNotes(originalNotes);

        // When
        mapper.copyBasics(input, target);

        // Then
        assertEquals(completedAt, target.getCompletedAt());
        assertEquals(originalNotes, target.getNotes()); // Should remain unchanged
    }

    @Test
    @DisplayName("copyBasics should not modify target when both fields are null")
    void copyBasics_WithAllNullFields_ShouldNotModifyTarget() {
        // Given
        OffsetDateTime originalCompletedAt = OffsetDateTime.now().minusHours(1);
        String originalNotes = "Original notes";
        
        CompletedActivityInputDTO input = new CompletedActivityInputDTO();
        input.setCompletedAt(null);
        input.setNotes(null);

        CompletedActivity target = new CompletedActivity();
        target.setCompletedAt(originalCompletedAt);
        target.setNotes(originalNotes);

        // When
        mapper.copyBasics(input, target);

        // Then
        assertEquals(originalCompletedAt, target.getCompletedAt()); // Should remain unchanged
        assertEquals(originalNotes, target.getNotes()); // Should remain unchanged
    }

    @Test
    @DisplayName("copyBasics should handle empty string notes correctly")
    void copyBasics_WithEmptyStringNotes_ShouldSetEmptyString() {
        // Given
        OffsetDateTime completedAt = OffsetDateTime.now();
        String emptyNotes = "";
        
        CompletedActivityInputDTO input = new CompletedActivityInputDTO();
        input.setCompletedAt(completedAt);
        input.setNotes(emptyNotes);

        CompletedActivity target = new CompletedActivity();
        target.setNotes("Original notes");

        // When
        mapper.copyBasics(input, target);

        // Then
        assertEquals(completedAt, target.getCompletedAt());
        assertEquals(emptyNotes, target.getNotes());
    }
}