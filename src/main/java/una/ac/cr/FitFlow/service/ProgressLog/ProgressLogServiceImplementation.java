// ProgressLogServiceImplementation.java
package una.ac.cr.FitFlow.service.ProgressLog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.time.ZoneId;

import java.util.List;

import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogInputDTO;
import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogOutputDTO;
import una.ac.cr.FitFlow.mapper.MapperForProgressLog;
import una.ac.cr.FitFlow.model.ProgressLog;
import una.ac.cr.FitFlow.model.Routine;
import una.ac.cr.FitFlow.model.User;
import una.ac.cr.FitFlow.repository.ProgressLogRepository;
import una.ac.cr.FitFlow.repository.RoutineRepository;
import una.ac.cr.FitFlow.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgressLogServiceImplementation implements ProgressLogService {

  private final ProgressLogRepository repo;
  private final UserRepository userRepo;
  private final RoutineRepository routineRepo;
  private final MapperForProgressLog mapper;

  @Override
  @Transactional
  public ProgressLogOutputDTO create(ProgressLogInputDTO in) {
    if (in.getUserId() == null) throw new IllegalArgumentException("userId es obligatorio.");
    if (in.getRoutineId() == null) throw new IllegalArgumentException("routineId es obligatorio.");
    if (in.getDate() == null) throw new IllegalArgumentException("date es obligatorio.");

    User user = userRepo.findById(in.getUserId())
        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + in.getUserId()));
    Routine routine = routineRepo.findById(in.getRoutineId())
        .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada: " + in.getRoutineId()));

    ProgressLog entity = mapper.toEntity(in, user, routine);
    ProgressLog saved = repo.save(entity);
    return mapper.toDto(saved);
  }

  @Override
  @Transactional
  public ProgressLogOutputDTO update(Long id, ProgressLogInputDTO in) {
    ProgressLog current = repo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("ProgressLog no encontrado: " + id));

    User userIfChanged = null;
    if (in.getUserId() != null && (current.getUser() == null ||
        !in.getUserId().equals(current.getUser().getId()))) {
      userIfChanged = userRepo.findById(in.getUserId())
          .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + in.getUserId()));
    }

    Routine routineIfChanged = null;
    if (in.getRoutineId() != null && (current.getRoutine() == null ||
        !in.getRoutineId().equals(current.getRoutine().getId()))) {
      routineIfChanged = routineRepo.findById(in.getRoutineId())
          .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada: " + in.getRoutineId()));
    }

    mapper.copyToEntity(in, current, userIfChanged, routineIfChanged);
    ProgressLog saved = repo.save(current);
    return mapper.toDto(saved);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    if (!repo.existsById(id)) throw new IllegalArgumentException("ProgressLog no encontrado: " + id);
    repo.deleteById(id);
  }

  @Override
  public ProgressLogOutputDTO findById(Long id) {
    return repo.findById(id).map(mapper::toDto)
        .orElseThrow(() -> new IllegalArgumentException("ProgressLog no encontrado: " + id));
  }

  @Override
  public Page<ProgressLogOutputDTO> list(Pageable pageable) {
    return repo.findAll(pageable).map(mapper::toDto);
  }

  @Override
  public Page<ProgressLogOutputDTO> listByUser(Long userId, Pageable pageable) {
    return repo.findByUser_Id(userId, pageable).map(mapper::toDto);
  }

  @Override
  public List<ProgressLogOutputDTO> listByUserOnDate(Long userId, OffsetDateTime dateAtUserZone) {
    ZoneId zone = ZoneId.of("America/Costa_Rica");
    var localDate = dateAtUserZone.atZoneSameInstant(zone).toLocalDate();
    var start = localDate.atStartOfDay(zone).toOffsetDateTime();
    var end   = localDate.plusDays(1).atStartOfDay(zone).toOffsetDateTime();

    return repo.findByUser_IdAndLogDateBetween(userId, start, end)
        .stream().map(mapper::toDto).toList();
  }
}
