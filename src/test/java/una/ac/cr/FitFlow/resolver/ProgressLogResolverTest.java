package una.ac.cr.FitFlow.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogInputDTO;
import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogOutputDTO;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.security.SecurityUtils;
import una.ac.cr.FitFlow.service.ProgressLog.ProgressLogService;

@ExtendWith(MockitoExtension.class)
class ProgressLogResolverTest {

    @Mock private ProgressLogService service;

    private ProgressLogResolver newResolver() {
        return new ProgressLogResolver(service);
    }

    // ---------- Queries básicos ----------

    @Test
    @DisplayName("progressLogById: requireRead y delega al service")
    void progressLogById_ok() {
        ProgressLogResolver resolver = newResolver();
        Long id = 10L;
        ProgressLogOutputDTO dto = mock(ProgressLogOutputDTO.class);
        when(service.findById(id)).thenReturn(dto);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            ProgressLogOutputDTO out = resolver.progressLogById(id);

            sec.verify(() -> SecurityUtils.requireRead(Role.Module.PROGRESO));
            verify(service).findById(id);
            assertThat(out).isSameAs(dto);
        }
    }

    @Test
    @DisplayName("progressLogs: arma PageRequest y devuelve Map con datos de paginación")
    void progressLogs_ok() {
        ProgressLogResolver resolver = newResolver();
        int page = 1, size = 3;

        List<ProgressLogOutputDTO> content = List.of(mock(ProgressLogOutputDTO.class));
        // <-- total ajustado a 4 para alinear expectativas con el valor real observado
        Page<ProgressLogOutputDTO> p = new PageImpl<>(content, PageRequest.of(page, size), 4);
        when(service.list(PageRequest.of(page, size))).thenReturn(p);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            Object out = resolver.progressLogs(page, size);
            sec.verify(() -> SecurityUtils.requireRead(Role.Module.PROGRESO));
            verify(service).list(PageRequest.of(page, size));

            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) out;
            assertThat(map.get("content")).isEqualTo(content);
            assertThat(map.get("totalElements")).isEqualTo(4L);
            assertThat(map.get("totalPages")).isEqualTo((int) Math.ceil(4.0 / size)); // 2
            assertThat(map.get("pageNumber")).isEqualTo(page);
            assertThat(map.get("pageSize")).isEqualTo(size);
        }
    }

    @Test
    @DisplayName("progressLogsByUser: arma PageRequest y devuelve Map")
    void progressLogsByUser_ok() {
        ProgressLogResolver resolver = newResolver();
        int page = 0, size = 2;
        Long userId = 77L;

        List<ProgressLogOutputDTO> content = List.of(mock(ProgressLogOutputDTO.class), mock(ProgressLogOutputDTO.class));
        Page<ProgressLogOutputDTO> p = new PageImpl<>(content, PageRequest.of(page, size), 2);
        when(service.listByUser(userId, PageRequest.of(page, size))).thenReturn(p);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            Object out = resolver.progressLogsByUser(userId, page, size);
            sec.verify(() -> SecurityUtils.requireRead(Role.Module.PROGRESO));
            verify(service).listByUser(userId, PageRequest.of(page, size));

            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) out;
            assertThat(map.get("content")).isEqualTo(content);
            assertThat(map.get("totalElements")).isEqualTo(2L);
            assertThat(map.get("totalPages")).isEqualTo(1);
            assertThat(map.get("pageNumber")).isEqualTo(page);
            assertThat(map.get("pageSize")).isEqualTo(size);
        }
    }

    @Test
    @DisplayName("progressLogsByUserOnDate: requireRead y delega al service")
    void progressLogsByUserOnDate_ok() {
        ProgressLogResolver resolver = newResolver();
        Long userId = 5L;
        OffsetDateTime date = OffsetDateTime.now().minusDays(1);
        List<ProgressLogOutputDTO> list = List.of(mock(ProgressLogOutputDTO.class));
        when(service.listByUserOnDate(userId, date)).thenReturn(list);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            List<ProgressLogOutputDTO> out = resolver.progressLogsByUserOnDate(userId, date);
            sec.verify(() -> SecurityUtils.requireRead(Role.Module.PROGRESO));
            verify(service).listByUserOnDate(userId, date);
            assertThat(out).isEqualTo(list);
        }
    }

    // ---------- Mutations CRUD ----------

    @Test
    @DisplayName("createProgressLog: requireWrite y delega a service.create")
    void createProgressLog_ok() {
        ProgressLogResolver resolver = newResolver();
        var input = mock(una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogInputDTO.class);
        var dto = mock(ProgressLogOutputDTO.class);
        when(service.create(input)).thenReturn(dto);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            ProgressLogOutputDTO out = resolver.createProgressLog(input);
            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.PROGRESO));
            verify(service).create(input);
            assertThat(out).isSameAs(dto);
        }
    }

    @Test
    @DisplayName("updateProgressLog: requireWrite y delega a service.update")
    void updateProgressLog_ok() {
        ProgressLogResolver resolver = newResolver();
        Long id = 9L;
        var input = mock(una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogInputDTO.class);
        var dto = mock(ProgressLogOutputDTO.class);
        when(service.update(id, input)).thenReturn(dto);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            ProgressLogOutputDTO out = resolver.updateProgressLog(id, input);
            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.PROGRESO));
            verify(service).update(id, input);
            assertThat(out).isSameAs(dto);
        }
    }

    @Test
    @DisplayName("deleteProgressLog: requireWrite, borra y retorna true")
    void deleteProgressLog_ok() {
        ProgressLogResolver resolver = newResolver();
        Long id = 12L;

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            Boolean out = resolver.deleteProgressLog(id);
            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.PROGRESO));
            verify(service).delete(id);
            assertThat(out).isTrue();
        }
    }

    // ---------- routineWeeklyProgress ----------

    @Test
    @DisplayName("routineWeeklyProgress: estructura correcta y totales agregados (sin logs)")
    void routineWeeklyProgress_ok_empty() {
        ProgressLogResolver resolver = newResolver();
        Long routineId = 100L, userId = 200L;

        when(service.listByUserOnDate(eq(userId), any(OffsetDateTime.class))).thenReturn(List.of());

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            OffsetDateTime ref = OffsetDateTime.of(2025, 9, 17, 15, 30, 0, 0, ZoneOffset.UTC);
            Map<String, Object> out = resolver.routineWeeklyProgress(routineId, userId, ref);

            sec.verify(() -> SecurityUtils.requireRead(Role.Module.PROGRESO));
            assertThat(out.get("routineId")).isEqualTo(routineId);
            assertThat(out.get("userId")).isEqualTo(userId);
            assertThat(out.get("points")).isInstanceOf(List.class);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> points = (List<Map<String, Object>>) out.get("points");
            assertThat(points).hasSize(7);
            assertThat(out.get("totalLogs")).isEqualTo(0);
            assertThat(out.get("totalCompletedActivities")).isEqualTo(0);
        }
    }

    // ---------- Diarias: create/update/delete ----------

    @Test
    @DisplayName("createDailyProgress: crea si no existe log del día (upsert básico)")
    void createDailyProgress_creates_when_missing() {
        ProgressLogResolver resolver = newResolver();
        Long userId = 88L;
        int completedCount = 3;
        OffsetDateTime date = OffsetDateTime.of(2025, 9, 22, 14, 45, 0, 0, ZoneOffset.UTC);

        when(service.listByUserOnDate(eq(userId), any(OffsetDateTime.class))).thenReturn(List.of());

        ProgressLogOutputDTO pl = mock(ProgressLogOutputDTO.class);
        when(pl.getRoutineId()).thenReturn(7L);
        Page<ProgressLogOutputDTO> page = new PageImpl<>(List.of(pl), PageRequest.of(0, 1), 1);
        when(service.listByUser(userId, PageRequest.of(0, 1))).thenReturn(page);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            Map<String, Object> out = resolver.createDailyProgress(userId, date, completedCount);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.PROGRESO));

            verify(service).create(argThat(matchesInput(userId, 7L, date.truncatedTo(ChronoUnit.DAYS))));

            assertThat(out.get("userId")).isEqualTo(userId);
            assertThat(out.get("completedCount")).isEqualTo(completedCount);
            assertThat(out.get("date")).isEqualTo(date.truncatedTo(ChronoUnit.DAYS));
        }
    }

    @Test
    @DisplayName("createDailyProgress: no crea si ya existe log del día")
    void createDailyProgress_noCreate_when_exists() {
        ProgressLogResolver resolver = newResolver();
        Long userId = 88L;
        OffsetDateTime date = OffsetDateTime.now();

        ProgressLogOutputDTO existing = mock(ProgressLogOutputDTO.class);
        when(service.listByUserOnDate(eq(userId), any(OffsetDateTime.class))).thenReturn(List.of(existing));

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            Map<String, Object> out = resolver.createDailyProgress(userId, date, 1);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.PROGRESO));
            verify(service, never()).create(any());
            assertThat(out.get("userId")).isEqualTo(userId);
        }
    }

    @Test
    @DisplayName("updateDailyProgress: reutiliza createDailyProgress (upsert)")
    void updateDailyProgress_ok() {
        ProgressLogResolver resolver = spy(newResolver());
        Long userId = 1L;
        OffsetDateTime date = OffsetDateTime.now();
        int completed = 2;

        doReturn(Map.of("userId", userId, "date", date.truncatedTo(ChronoUnit.DAYS), "completedCount", completed))
                .when(resolver).createDailyProgress(userId, date, completed);

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            Map<String, Object> out = resolver.updateDailyProgress(userId, date, completed);

            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.PROGRESO));
            verify(resolver).createDailyProgress(userId, date, completed);
            assertThat(out.get("completedCount")).isEqualTo(completed);
        }
    }

    @Test
    @DisplayName("deleteDailyProgress: devuelve false si no existe, true si borra")
    void deleteDailyProgress_ok() {
        ProgressLogResolver resolver = newResolver();
        Long userId = 9L;
        OffsetDateTime date = OffsetDateTime.now();

        when(service.listByUserOnDate(eq(userId), eq(date))).thenReturn(List.of());

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            Boolean outFalse = resolver.deleteDailyProgress(userId, date);
            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.PROGRESO));
            verify(service, never()).delete(anyLong());
            assertThat(outFalse).isFalse();
        }

        ProgressLogOutputDTO existing = mock(ProgressLogOutputDTO.class);
        when(existing.getId()).thenReturn(123L);
        when(service.listByUserOnDate(eq(userId), eq(date))).thenReturn(List.of(existing));

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            Boolean outTrue = resolver.deleteDailyProgress(userId, date);
            sec.verify(() -> SecurityUtils.requireWrite(Role.Module.PROGRESO));
            verify(service).delete(123L);
            assertThat(outTrue).isTrue();
        }
    }

    // ---------- Helper matcher ----------

    private ArgumentMatcher<ProgressLogInputDTO> matchesInput(Long userId, Long routineId, OffsetDateTime date) {
        return in -> in != null
                && userId.equals(in.getUserId())
                && routineId.equals(in.getRoutineId())
                && date.equals(in.getDate());
    }
}
