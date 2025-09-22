package una.ac.cr.FitFlow.service.CompletedActivity;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import una.ac.cr.FitFlow.dto.CompletedActivity.CompletedActivityInputDTO;
import una.ac.cr.FitFlow.dto.CompletedActivity.CompletedActivityOutputDTO;
import una.ac.cr.FitFlow.mapper.MapperForCompletedActivity;
import una.ac.cr.FitFlow.model.CompletedActivity;
import una.ac.cr.FitFlow.model.Habit;
import una.ac.cr.FitFlow.model.ProgressLog;
import una.ac.cr.FitFlow.repository.CompletedActivityRepository;
import una.ac.cr.FitFlow.repository.HabitRepository;
import una.ac.cr.FitFlow.repository.ProgressLogRepository;

@ExtendWith(MockitoExtension.class)
class CompletedActivityServiceImplementationTest {

    @Mock private CompletedActivityRepository completedActivityRepository;
    @Mock private HabitRepository habitRepository;
    @Mock private ProgressLogRepository progressLogRepository;
    @Mock private MapperForCompletedActivity mapper;

    @InjectMocks
    private CompletedActivityServiceImplementation service;

    private CompletedActivityInputDTO input(Long habitId, Long progressLogId, OffsetDateTime completedAt) {
        var dto = mock(CompletedActivityInputDTO.class);
        when(dto.getHabitId()).thenReturn(habitId);
        when(dto.getProgressLogId()).thenReturn(progressLogId);
        when(dto.getCompletedAt()).thenReturn(completedAt);
        return dto;
    }

    @Nested
    class Create {

        @Test
        @DisplayName("createCompletedActivity: feliz")
        void create_ok() {
            OffsetDateTime when = OffsetDateTime.now(ZoneOffset.UTC);
            var in = input(10L, 20L, when);

            Habit habit = Habit.builder().id(10L).build();
            ProgressLog pl = ProgressLog.builder().id(20L).build();

            when(habitRepository.findById(10L)).thenReturn(Optional.of(habit));
            when(progressLogRepository.findById(20L)).thenReturn(Optional.of(pl));

            // copyBasics mapea campos primitivos del input al entity
            doAnswer(inv -> {
                CompletedActivityInputDTO src = inv.getArgument(0);
                CompletedActivity target = inv.getArgument(1);
                target.setCompletedAt(src.getCompletedAt());
                target.setNotes("n"); // lo que sea, no afecta el assert principal
                return null;
            }).when(mapper).copyBasics(eq(in), any(CompletedActivity.class));

            // save devuelve el mismo entity
            doAnswer(inv -> inv.getArgument(0))
                .when(completedActivityRepository).save(any(CompletedActivity.class));

            CompletedActivityOutputDTO outDto = mock(CompletedActivityOutputDTO.class);
            when(mapper.toDto(any(CompletedActivity.class))).thenReturn(outDto);

            CompletedActivityOutputDTO out = service.createCompletedActivity(in);

            assertThat(out).isSameAs(outDto);

            ArgumentCaptor<CompletedActivity> cap = ArgumentCaptor.forClass(CompletedActivity.class);
            verify(completedActivityRepository).save(cap.capture());
            CompletedActivity saved = cap.getValue();
            assertThat(saved.getHabit()).isSameAs(habit);
            assertThat(saved.getProgressLog()).isSameAs(pl);
            assertThat(saved.getCompletedAt()).isEqualTo(when);
        }

        @Test
        @DisplayName("createCompletedActivity: falla si completedAt es null")
        void create_fails_completedAt_null() {
            var in = input(1L, 2L, null);
            assertThatThrownBy(() -> service.createCompletedActivity(in))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("completedAt");
        }

        @Test
        @DisplayName("createCompletedActivity: falla si habitId es null")
        void create_fails_habitId_null() {
            var in = input(null, 2L, OffsetDateTime.now());
            assertThatThrownBy(() -> service.createCompletedActivity(in))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("habitId");
        }

        @Test
        @DisplayName("createCompletedActivity: falla si progressLogId es null")
        void create_fails_progressLogId_null() {
            var in = input(1L, null, OffsetDateTime.now());
            assertThatThrownBy(() -> service.createCompletedActivity(in))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("progressLogId");
        }

        @Test
        @DisplayName("createCompletedActivity: falla si habit no existe")
        void create_fails_habit_not_found() {
            var in = input(10L, 20L, OffsetDateTime.now());
            when(habitRepository.findById(10L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.createCompletedActivity(in))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Habit no encontrado");
        }

        @Test
        @DisplayName("createCompletedActivity: falla si progressLog no existe")
        void create_fails_progresslog_not_found() {
            var in = input(10L, 20L, OffsetDateTime.now());
            when(habitRepository.findById(10L)).thenReturn(Optional.of(Habit.builder().id(10L).build()));
            when(progressLogRepository.findById(20L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.createCompletedActivity(in))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ProgressLog no encontrado");
        }
    }

    @Nested
    class Update {

        @Test
        @DisplayName("updateCompletedActivity: feliz (mapea basics y relaciones opcionales)")
        void update_ok() {
            Long id = 77L;
            var in = input(10L, 20L, OffsetDateTime.now());

            CompletedActivity existing = CompletedActivity.builder()
                    .id(id)
                    .completedAt(OffsetDateTime.now().minusDays(1))
                    .habit(Habit.builder().id(1L).build())
                    .progressLog(ProgressLog.builder().id(2L).build())
                    .build();

            when(completedActivityRepository.findById(id)).thenReturn(Optional.of(existing));
            when(habitRepository.findById(10L)).thenReturn(Optional.of(Habit.builder().id(10L).build()));
            when(progressLogRepository.findById(20L)).thenReturn(Optional.of(ProgressLog.builder().id(20L).build()));

            doAnswer(inv -> {
                CompletedActivityInputDTO src = inv.getArgument(0);
                CompletedActivity target = inv.getArgument(1);
                target.setCompletedAt(src.getCompletedAt());
                target.setNotes("upd");
                return null;
            }).when(mapper).copyBasics(eq(in), any(CompletedActivity.class));

            doAnswer(inv -> inv.getArgument(0))
                .when(completedActivityRepository).save(any(CompletedActivity.class));

            CompletedActivityOutputDTO outDto = mock(CompletedActivityOutputDTO.class);
            when(mapper.toDto(any(CompletedActivity.class))).thenReturn(outDto);

            CompletedActivityOutputDTO out = service.updateCompletedActivity(id, in);

            assertThat(out).isSameAs(outDto);
            assertThat(existing.getHabit().getId()).isEqualTo(10L);
            assertThat(existing.getProgressLog().getId()).isEqualTo(20L);
            assertThat(existing.getNotes()).isEqualTo("upd");
        }

        @Test
        @DisplayName("updateCompletedActivity: falla si no existe la entidad")
        void update_not_found() {
            when(completedActivityRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.updateCompletedActivity(99L, input(1L, 2L, OffsetDateTime.now())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CompletedActivity no encontrado");
        }

        @Test
        @DisplayName("updateCompletedActivity: falla si nuevo habitId no existe")
        void update_habit_not_found() {
            Long id = 1L;
            CompletedActivity existing = CompletedActivity.builder().id(id).build();
            when(completedActivityRepository.findById(id)).thenReturn(Optional.of(existing));

            var in = input(10L, null, OffsetDateTime.now());
            when(habitRepository.findById(10L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.updateCompletedActivity(id, in))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Habit no encontrado");
        }

        @Test
        @DisplayName("updateCompletedActivity: falla si nuevo progressLogId no existe")
        void update_progresslog_not_found() {
            Long id = 1L;
            CompletedActivity existing = CompletedActivity.builder().id(id).build();
            when(completedActivityRepository.findById(id)).thenReturn(Optional.of(existing));

            var in = input(null, 20L, OffsetDateTime.now());
            when(progressLogRepository.findById(20L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.updateCompletedActivity(id, in))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ProgressLog no encontrado");
        }
    }

    @Nested
    class DeleteAndFind {

        @Test
        @DisplayName("deleteCompletedActivity: feliz")
        void delete_ok() {
            Long id = 5L;
            CompletedActivity entity = CompletedActivity.builder().id(id).build();
            when(completedActivityRepository.findById(id)).thenReturn(Optional.of(entity));

            service.deleteCompletedActivity(id);

            verify(completedActivityRepository).delete(entity);
        }

        @Test
        @DisplayName("deleteCompletedActivity: not found")
        void delete_not_found() {
            when(completedActivityRepository.findById(5L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.deleteCompletedActivity(5L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CompletedActivity no encontrado");
        }

        @Test
        @DisplayName("findCompletedActivityById: feliz")
        void findById_ok() {
            Long id = 7L;
            CompletedActivity entity = CompletedActivity.builder().id(id).build();
            when(completedActivityRepository.findById(id)).thenReturn(Optional.of(entity));

            CompletedActivityOutputDTO dto = mock(CompletedActivityOutputDTO.class);
            when(mapper.toDto(entity)).thenReturn(dto);

            CompletedActivityOutputDTO out = service.findCompletedActivityById(id);
            assertThat(out).isSameAs(dto);
        }

        @Test
        @DisplayName("findCompletedActivityById: not found")
        void findById_not_found() {
            when(completedActivityRepository.findById(7L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.findCompletedActivityById(7L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CompletedActivity no encontrado");
        }
    }

    @Nested
    class ListingAndQueries {

        @Test
        @DisplayName("listCompletedActivities: sin keyword → findAll(pageable)")
        void list_noKeyword() {
            var pageable = PageRequest.of(0, 2);
            CompletedActivity e1 = CompletedActivity.builder().id(1L).build();
            CompletedActivity e2 = CompletedActivity.builder().id(2L).build();

            Page<CompletedActivity> page = new PageImpl<>(List.of(e1, e2), pageable, 2);
            when(completedActivityRepository.findAll(pageable)).thenReturn(page);

            CompletedActivityOutputDTO d1 = mock(CompletedActivityOutputDTO.class);
            CompletedActivityOutputDTO d2 = mock(CompletedActivityOutputDTO.class);
            when(mapper.toDto(e1)).thenReturn(d1);
            when(mapper.toDto(e2)).thenReturn(d2);

            Page<CompletedActivityOutputDTO> out = service.listCompletedActivities("   ", pageable);

            assertThat(out.getContent()).containsExactly(d1, d2);
            assertThat(out.getTotalElements()).isEqualTo(2);
        }

        @Test
        @DisplayName("listCompletedActivities: con keyword → findByNotesContainingIgnoreCase")
        void list_withKeyword() {
            var pageable = PageRequest.of(1, 3);
            String q = "agua";

            CompletedActivity e = CompletedActivity.builder().id(1L).build();
            Page<CompletedActivity> page = new PageImpl<>(List.of(e), pageable, 1);
            when(completedActivityRepository.findByNotesContainingIgnoreCase(q, pageable)).thenReturn(page);

            CompletedActivityOutputDTO d = mock(CompletedActivityOutputDTO.class);
            when(mapper.toDto(e)).thenReturn(d);

            Page<CompletedActivityOutputDTO> out = service.listCompletedActivities(q, pageable);

            assertThat(out.getContent()).containsExactly(d);
            assertThat(out.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("findCompletedActivitiesByUserId: delega a repo y mapea")
        void findByUserId_ok() {
            var pageable = PageRequest.of(0, 5);
            Long userId = 9L;

            CompletedActivity e = CompletedActivity.builder().id(1L).build();
            Page<CompletedActivity> page = new PageImpl<>(List.of(e), pageable, 1);
            when(completedActivityRepository.findByProgressLog_User_Id(userId, pageable)).thenReturn(page);

            CompletedActivityOutputDTO d = mock(CompletedActivityOutputDTO.class);
            when(mapper.toDto(e)).thenReturn(d);

            Page<CompletedActivityOutputDTO> out = service.findCompletedActivitiesByUserId(userId, pageable);

            assertThat(out.getContent()).containsExactly(d);
        }

        @Test
        @DisplayName("findByProgressLogId: delega a repo y mapea")
        void findByProgressLogId_ok() {
            var pageable = PageRequest.of(0, 5);
            Long logId = 15L;

            CompletedActivity e = CompletedActivity.builder().id(2L).build();
            Page<CompletedActivity> page = new PageImpl<>(List.of(e), pageable, 1);
            when(completedActivityRepository.findByProgressLog_Id(logId, pageable)).thenReturn(page);

            CompletedActivityOutputDTO d = mock(CompletedActivityOutputDTO.class);
            when(mapper.toDto(e)).thenReturn(d);

            Page<CompletedActivityOutputDTO> out = service.findByProgressLogId(logId, pageable);

            assertThat(out.getContent()).containsExactly(d);
        }

        @Test
        @DisplayName("findByHabitId: delega a repo, stream y mapea a lista")
        void findByHabitId_ok() {
            Long habitId = 33L;
            CompletedActivity e1 = CompletedActivity.builder().id(1L).build();
            CompletedActivity e2 = CompletedActivity.builder().id(2L).build();

            when(completedActivityRepository.findByHabit_Id(habitId)).thenReturn(List.of(e1, e2));

            CompletedActivityOutputDTO d1 = mock(CompletedActivityOutputDTO.class);
            CompletedActivityOutputDTO d2 = mock(CompletedActivityOutputDTO.class);
            when(mapper.toDto(e1)).thenReturn(d1);
            when(mapper.toDto(e2)).thenReturn(d2);

            List<CompletedActivityOutputDTO> out = service.findByHabitId(habitId);

            assertThat(out).containsExactly(d1, d2);
        }
    }

    @Nested
    class MonthlyByCategory {

        @Test
        @DisplayName("monthlyCompletedActivitiesByCategoryAndDate: calcula inicio/fin del mes y consulta repo con enum")
        void monthly_ok() {
            OffsetDateTime anyDate = OffsetDateTime.of(2025, 9, 18, 10, 0, 0, 0, ZoneOffset.UTC);

            CompletedActivity e1 = CompletedActivity.builder().id(1L).build();
            CompletedActivity e2 = CompletedActivity.builder().id(2L).build();

            when(completedActivityRepository.findCompletedByCategoryAndMonth(
                eq(Habit.Category.DIET), any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(List.of(e1, e2));

            CompletedActivityOutputDTO d1 = mock(CompletedActivityOutputDTO.class);
            CompletedActivityOutputDTO d2 = mock(CompletedActivityOutputDTO.class);
            when(mapper.toDto(e1)).thenReturn(d1);
            when(mapper.toDto(e2)).thenReturn(d2);

            List<CompletedActivityOutputDTO> out = service.monthlyCompletedActivitiesByCategoryAndDate("diet", anyDate);

            // Verifica salida
            assertThat(out).containsExactly(d1, d2);

            // Captura y verifica rango de fecha
            ArgumentCaptor<OffsetDateTime> startCap = ArgumentCaptor.forClass(OffsetDateTime.class);
            ArgumentCaptor<OffsetDateTime> endCap = ArgumentCaptor.forClass(OffsetDateTime.class);
            verify(completedActivityRepository).findCompletedByCategoryAndMonth(
                eq(Habit.Category.DIET), startCap.capture(), endCap.capture());

            OffsetDateTime start = startCap.getValue();
            OffsetDateTime end = endCap.getValue();

            assertThat(start.getDayOfMonth()).isEqualTo(1);
            assertThat(start.getHour()).isEqualTo(0);
            assertThat(start.getMinute()).isEqualTo(0);
            assertThat(start.getSecond()).isEqualTo(0);
            assertThat(start.getNano()).isEqualTo(0);

            // end debe ser inicio del siguiente mes (exclusivo)
            assertThat(end.getMonthValue()).isEqualTo(10);
            assertThat(end.getDayOfMonth()).isEqualTo(1);
            assertThat(end.isAfter(start)).isTrue();
        }

        @Test
        @DisplayName("monthlyCompletedActivitiesByCategoryAndDate: lanza si categoría inválida")
        void monthly_invalid_category() {
            OffsetDateTime anyDate = OffsetDateTime.now();
            assertThatThrownBy(() -> service.monthlyCompletedActivitiesByCategoryAndDate("INVALIDA", anyDate))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
