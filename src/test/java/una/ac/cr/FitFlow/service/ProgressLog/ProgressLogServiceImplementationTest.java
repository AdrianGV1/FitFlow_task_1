package una.ac.cr.FitFlow.service.ProgressLog;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import java.time.OffsetDateTime;
import java.time.ZoneId;
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

import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogInputDTO;
import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogOutputDTO;
import una.ac.cr.FitFlow.mapper.MapperForProgressLog;
import una.ac.cr.FitFlow.model.ProgressLog;
import una.ac.cr.FitFlow.model.Routine;
import una.ac.cr.FitFlow.model.User;
import una.ac.cr.FitFlow.repository.ProgressLogRepository;
import una.ac.cr.FitFlow.repository.RoutineRepository;
import una.ac.cr.FitFlow.repository.UserRepository;
import una.ac.cr.FitFlow.service.ProgressLog.ProgressLogServiceImplementation;

@ExtendWith(MockitoExtension.class)
class ProgressLogServiceImplementationTest {

    @Mock private ProgressLogRepository repo;
    @Mock private UserRepository userRepo;
    @Mock private RoutineRepository routineRepo;
    @Mock private MapperForProgressLog mapper;

    @InjectMocks
    private ProgressLogServiceImplementation service;

    private ProgressLogInputDTO input(Long userId, Long routineId, OffsetDateTime date) {
        ProgressLogInputDTO in = mock(ProgressLogInputDTO.class);
        when(in.getUserId()).thenReturn(userId);
        when(in.getRoutineId()).thenReturn(routineId);
        when(in.getDate()).thenReturn(date);
        return in;
    }

    /* ================== CREATE ================== */

    @Test
    @DisplayName("create: feliz")
    void create_ok() {
        var when = OffsetDateTime.now(ZoneOffset.UTC);
        var in = input(11L, 22L, when);

        User u = User.builder().id(11L).build();
        Routine r = Routine.builder().id(22L).build();
        when(userRepo.findById(11L)).thenReturn(Optional.of(u));
        when(routineRepo.findById(22L)).thenReturn(Optional.of(r));

        ProgressLog entity = ProgressLog.builder().id(99L).user(u).routine(r).logDate(when).build();
        when(mapper.toEntity(in, u, r)).thenReturn(entity);

        when(repo.save(entity)).thenReturn(entity);

        ProgressLogOutputDTO outDto = mock(ProgressLogOutputDTO.class);
        when(mapper.toDto(entity)).thenReturn(outDto);

        ProgressLogOutputDTO out = service.create(in);

        assertThat(out).isSameAs(outDto);
        verify(repo).save(entity);
    }

    @Test
    @DisplayName("create: falla si faltan campos obligatorios")
    void create_fail_required() {
        assertThatThrownBy(() -> service.create(input(null, 1L, OffsetDateTime.now())))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("userId");
        assertThatThrownBy(() -> service.create(input(1L, null, OffsetDateTime.now())))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("routineId");
        assertThatThrownBy(() -> service.create(input(1L, 2L, null)))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("date");
    }

    @Test
    @DisplayName("create: usuario/rutina no encontrados")
    void create_fail_not_found_relations() {
        var in = input(11L, 22L, OffsetDateTime.now());
        when(userRepo.findById(11L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(in))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Usuario no encontrado");

        when(userRepo.findById(11L)).thenReturn(Optional.of(User.builder().id(11L).build()));
        when(routineRepo.findById(22L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(in))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Rutina no encontrada");
    }

    /* ================== UPDATE ================== */

    @Test
    @DisplayName("update: feliz sin cambiar user/routine (no consulta repos de relaciones)")
    void update_ok_no_relation_change() {
        Long id = 7L;
        User u = User.builder().id(1L).build();
        Routine r = Routine.builder().id(2L).build();
        ProgressLog current = ProgressLog.builder().id(id).user(u).routine(r).logDate(OffsetDateTime.now()).build();

        when(repo.findById(id)).thenReturn(Optional.of(current));

        // input con mismos IDs => no debería buscar user/routine
        var in = input(1L, 2L, OffsetDateTime.now());

        doNothing().when(mapper).copyToEntity(eq(in), eq(current), isNull(), isNull());
        when(repo.save(current)).thenReturn(current);

        ProgressLogOutputDTO dto = mock(ProgressLogOutputDTO.class);
        when(mapper.toDto(current)).thenReturn(dto);

        ProgressLogOutputDTO out = service.update(id, in);

        assertThat(out).isSameAs(dto);
        verify(userRepo, never()).findById(any());
        verify(routineRepo, never()).findById(any());
        verify(repo).save(current);
    }

    @Test
    @DisplayName("update: cambia user y routine → busca/valida nuevas relaciones")
    void update_ok_with_relation_changes() {
        Long id = 7L;
        User uOld = User.builder().id(1L).build();
        Routine rOld = Routine.builder().id(2L).build();
        ProgressLog current = ProgressLog.builder().id(id).user(uOld).routine(rOld).logDate(OffsetDateTime.now()).build();

        when(repo.findById(id)).thenReturn(Optional.of(current));

        var in = input(10L, 20L, OffsetDateTime.now()); // distintos => cambia

        User uNew = User.builder().id(10L).build();
        Routine rNew = Routine.builder().id(20L).build();
        when(userRepo.findById(10L)).thenReturn(Optional.of(uNew));
        when(routineRepo.findById(20L)).thenReturn(Optional.of(rNew));

        doNothing().when(mapper).copyToEntity(eq(in), eq(current), eq(uNew), eq(rNew));
        when(repo.save(current)).thenReturn(current);

        ProgressLogOutputDTO dto = mock(ProgressLogOutputDTO.class);
        when(mapper.toDto(current)).thenReturn(dto);

        ProgressLogOutputDTO out = service.update(id, in);

        assertThat(out).isSameAs(dto);
        verify(userRepo).findById(10L);
        verify(routineRepo).findById(20L);
        verify(repo).save(current);
    }

    @Test
    @DisplayName("update: progressLog no encontrado")
    void update_not_found() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(99L, input(1L, 2L, OffsetDateTime.now())))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ProgressLog no encontrado");
    }

    @Test
    @DisplayName("update: nuevo userId/routineId no existen")
    void update_fail_new_relations_not_found() {
        Long id = 1L;
        ProgressLog current = ProgressLog.builder().id(id).build();
        when(repo.findById(id)).thenReturn(Optional.of(current));

        // caso: user no existe
        var inUser = input(10L, null, OffsetDateTime.now());
        when(userRepo.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(id, inUser))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Usuario no encontrado");

        // caso: routine no existe
        var inRoutine = input(null, 20L, OffsetDateTime.now());
        when(routineRepo.findById(20L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(id, inRoutine))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Rutina no encontrada");
    }

    /* ================== DELETE ================== */

    @Test
    @DisplayName("delete: feliz")
    void delete_ok() {
        when(repo.existsById(5L)).thenReturn(true);
        service.delete(5L);
        verify(repo).deleteById(5L);
    }

    @Test
    @DisplayName("delete: not found")
    void delete_not_found() {
        when(repo.existsById(5L)).thenReturn(false);
        assertThatThrownBy(() -> service.delete(5L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ProgressLog no encontrado");
        verify(repo, never()).deleteById(any());
    }

    /* ================== FIND / LIST ================== */

    @Test
    @DisplayName("findById: feliz")
    void findById_ok() {
        ProgressLog e = ProgressLog.builder().id(3L).build();
        when(repo.findById(3L)).thenReturn(Optional.of(e));

        ProgressLogOutputDTO dto = mock(ProgressLogOutputDTO.class);
        when(mapper.toDto(e)).thenReturn(dto);

        ProgressLogOutputDTO out = service.findById(3L);
        assertThat(out).isSameAs(dto);
    }

    @Test
    @DisplayName("findById: not found")
    void findById_not_found() {
        when(repo.findById(3L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(3L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ProgressLog no encontrado");
    }

    @Test
    @DisplayName("list: delega a repo.findAll(pageable) y mapea")
    void list_ok() {
        var pageable = PageRequest.of(0, 2);
        ProgressLog e1 = ProgressLog.builder().id(1L).build();
        ProgressLog e2 = ProgressLog.builder().id(2L).build();

        when(repo.findAll(pageable)).thenReturn(new PageImpl<>(List.of(e1, e2), pageable, 2));

        ProgressLogOutputDTO d1 = mock(ProgressLogOutputDTO.class);
        ProgressLogOutputDTO d2 = mock(ProgressLogOutputDTO.class);
        when(mapper.toDto(e1)).thenReturn(d1);
        when(mapper.toDto(e2)).thenReturn(d2);

        Page<ProgressLogOutputDTO> out = service.list(pageable);

        assertThat(out.getContent()).containsExactly(d1, d2);
    }

    @Test
    @DisplayName("listByUser: delega a repo.findByUser_Id(userId, pageable) y mapea")
    void listByUser_ok() {
        var pageable = PageRequest.of(1, 3);
        Long userId = 77L;
        ProgressLog e = ProgressLog.builder().id(5L).build();

        when(repo.findByUser_Id(userId, pageable)).thenReturn(new PageImpl<>(List.of(e), pageable, 1));

        ProgressLogOutputDTO d = mock(ProgressLogOutputDTO.class);
        when(mapper.toDto(e)).thenReturn(d);

        Page<ProgressLogOutputDTO> out = service.listByUser(userId, pageable);

        assertThat(out.getContent()).containsExactly(d);
    }

    /* ================== listByUserOnDate ================== */

    @Test
    @DisplayName("listByUserOnDate: calcula inicio/fin del día en America/Costa_Rica y consulta repo")
    void listByUserOnDate_ok() {
        Long userId = 55L;

        // 2025-09-18 06:30:00Z (UTC) -> en Costa Rica (UTC-6) es 2025-09-18 00:30-06
        OffsetDateTime inputDate = OffsetDateTime.of(2025, 9, 18, 6, 30, 0, 0, ZoneOffset.UTC);

        ProgressLog e1 = ProgressLog.builder().id(1L).build();
        when(repo.findByUser_IdAndLogDateBetween(eq(userId), any(), any()))
            .thenReturn(List.of(e1));

        ProgressLogOutputDTO d1 = mock(ProgressLogOutputDTO.class);
        when(mapper.toDto(e1)).thenReturn(d1);

        List<ProgressLogOutputDTO> out = service.listByUserOnDate(userId, inputDate);

        assertThat(out).containsExactly(d1);

        // Capturamos los límites calculados
        ArgumentCaptor<OffsetDateTime> startCap = ArgumentCaptor.forClass(OffsetDateTime.class);
        ArgumentCaptor<OffsetDateTime> endCap = ArgumentCaptor.forClass(OffsetDateTime.class);

        verify(repo).findByUser_IdAndLogDateBetween(eq(userId), startCap.capture(), endCap.capture());

        ZoneId cr = ZoneId.of("America/Costa_Rica");
        // El día local debe ser 2025-09-18
        var localDate = inputDate.atZoneSameInstant(cr).toLocalDate();
        var expectedStart = localDate.atStartOfDay(cr).toOffsetDateTime();
        var expectedEnd = localDate.plusDays(1).atStartOfDay(cr).toOffsetDateTime();

        assertThat(startCap.getValue()).isEqualTo(expectedStart);
        assertThat(endCap.getValue()).isEqualTo(expectedEnd);
        assertThat(endCap.getValue()).isAfter(startCap.getValue());
    }
}
