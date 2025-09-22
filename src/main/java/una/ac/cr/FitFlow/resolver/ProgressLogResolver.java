// ProgressLogResolver.java
package una.ac.cr.FitFlow.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import jakarta.transaction.Transactional;

import org.springframework.graphql.data.method.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogInputDTO;
import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogOutputDTO;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.security.SecurityUtils;
import una.ac.cr.FitFlow.service.ProgressLog.ProgressLogService;

@Controller
@RequiredArgsConstructor
public class ProgressLogResolver {

  private static final Role.Module MODULE = Role.Module.PROGRESO;

  private final ProgressLogService service;

  @QueryMapping
  public ProgressLogOutputDTO progressLogById(@Argument Long id) {
    SecurityUtils.requireRead(MODULE);
    return service.findById(id);
  }

  @QueryMapping
  public Object progressLogs(@Argument int page, @Argument int size) {
    SecurityUtils.requireRead(MODULE);
    Pageable pageable = PageRequest.of(page, size);
    Page<ProgressLogOutputDTO> p = service.list(pageable);
    return pageDTO(p);
  }

  @QueryMapping
  public Object progressLogsByUser(@Argument Long userId, @Argument int page, @Argument int size) {
    SecurityUtils.requireRead(MODULE);
    Pageable pageable = PageRequest.of(page, size);
    Page<ProgressLogOutputDTO> p = service.listByUser(userId, pageable);
    return pageDTO(p);
  }

  @QueryMapping
  public List<ProgressLogOutputDTO> progressLogsByUserOnDate(@Argument Long userId,
                                                             @Argument OffsetDateTime date) {
    SecurityUtils.requireRead(MODULE);
    return service.listByUserOnDate(userId, date);
  }

  @MutationMapping
  public ProgressLogOutputDTO createProgressLog(@Argument("input") ProgressLogInputDTO input) {
    SecurityUtils.requireWrite(MODULE);
    return service.create(input);
  }

  @MutationMapping
  public ProgressLogOutputDTO updateProgressLog(@Argument Long id, @Argument("input") ProgressLogInputDTO input) {
    SecurityUtils.requireWrite(MODULE);
    return service.update(id, input);
  }

  @MutationMapping
  public Boolean deleteProgressLog(@Argument Long id) {
    SecurityUtils.requireWrite(MODULE);
    service.delete(id);
    return true;
  }

  private Object pageDTO(Page<ProgressLogOutputDTO> p) {
    return new java.util.HashMap<>() {{
      put("content", p.getContent());
      put("totalElements", p.getTotalElements());
      put("totalPages", p.getTotalPages());
      put("pageNumber", p.getNumber());
      put("pageSize", p.getSize());
    }};
  }
  @QueryMapping(name = "routineWeeklyProgress")
  public Map<String, Object> routineWeeklyProgress(@Argument Long routineId,
                                                 @Argument Long userId,
                                                 @Argument OffsetDateTime refDate) {
  SecurityUtils.requireRead(MODULE);
  java.time.ZoneId zone = java.time.ZoneId.of("America/Costa_Rica");

  java.time.OffsetDateTime base = (refDate != null) ? refDate : java.time.OffsetDateTime.now(zone);
  java.time.LocalDate refLocal = base.atZoneSameInstant(zone).toLocalDate();

  java.time.LocalDate weekStart = refLocal.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
  java.time.LocalDate weekEnd   = weekStart.plusDays(6);

  java.util.List<java.util.Map<String, Object>> points = new java.util.ArrayList<>();
  int totalLogs = 0, totalCompleted = 0;

  for (int i = 0; i < 7; i++) {
    java.time.LocalDate d = weekStart.plusDays(i);
    java.time.OffsetDateTime dayStart = d.atStartOfDay(zone).toOffsetDateTime();

    java.util.List<ProgressLogOutputDTO> logs = service.listByUserOnDate(userId, dayStart)
        .stream().filter(pl -> java.util.Objects.equals(pl.getRoutineId(), routineId))
        .toList();

    int logsCount = logs.size();
    int completedCount = logs.stream()
        .map(pl -> pl.getCompletedActivityIds() == null ? 0 : pl.getCompletedActivityIds().size())
        .mapToInt(Integer::intValue).sum();

    java.util.Map<String, Object> pt = new java.util.HashMap<>();
    pt.put("date", d.toString());
    pt.put("logs", logsCount);
    pt.put("completedActivities", completedCount);
    points.add(pt);

    totalLogs += logsCount;
    totalCompleted += completedCount;
  }

  java.util.Map<String, Object> resp = new java.util.HashMap<>();
  resp.put("routineId", routineId);
  resp.put("userId", userId);
  resp.put("weekStart", weekStart.toString());
  resp.put("weekEnd", weekEnd.toString());
  resp.put("points", points);
  resp.put("totalLogs", totalLogs);
  resp.put("totalCompletedActivities", totalCompleted);
  return resp;
  } 

  @MutationMapping(name = "createDailyProgress")
@Transactional
public Map<String, Object> createDailyProgress(@Argument Long userId,
                                               @Argument OffsetDateTime date,
                                               @Argument int completedCount) {
    SecurityUtils.requireWrite(MODULE);

    // Normaliza fecha al inicio del día
    OffsetDateTime startOfDay = date.truncatedTo(java.time.temporal.ChronoUnit.DAYS);

    // Si no existe log para ese día, creamos uno vacío
    var existing = service.listByUserOnDate(userId, startOfDay).stream().findFirst().orElse(null);
    if (existing == null) {
        ProgressLogInputDTO input = ProgressLogInputDTO.builder()
                .userId(userId)
                .routineId(
                        service.listByUser(userId, PageRequest.of(0, 1))
                               .getContent().stream().findFirst()
                               .map(ProgressLogOutputDTO::getRoutineId)
                               .orElseThrow(() -> new IllegalStateException("Usuario sin rutina"))
                )
                .date(startOfDay)
                .build();
        service.create(input);
    }

    Map<String, Object> result = new HashMap<>();
    result.put("userId", userId);
    result.put("date", startOfDay);
    result.put("completedCount", completedCount);
    return result;
}

@MutationMapping(name = "updateDailyProgress")
@Transactional
public Map<String, Object> updateDailyProgress(@Argument Long userId,
                                               @Argument OffsetDateTime date,
                                               @Argument int completedCount) {
    SecurityUtils.requireWrite(MODULE);

    // Usamos la misma lógica de create (upsert)
    return createDailyProgress(userId, date, completedCount);
}

@MutationMapping(name = "deleteDailyProgress")
@Transactional
public Boolean deleteDailyProgress(@Argument Long userId,
                                   @Argument OffsetDateTime date) {
    SecurityUtils.requireWrite(MODULE);

    // Busca el log del día y lo borra
    var existing = service.listByUserOnDate(userId, date).stream().findFirst().orElse(null);
    if (existing == null) return false;

    service.delete(existing.getId());
    return true;
}


}
