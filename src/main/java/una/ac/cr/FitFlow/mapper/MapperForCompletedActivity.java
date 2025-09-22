package una.ac.cr.FitFlow.mapper;

import org.springframework.stereotype.Component;
import una.ac.cr.FitFlow.dto.CompletedActivity.CompletedActivityInputDTO;
import una.ac.cr.FitFlow.dto.CompletedActivity.CompletedActivityOutputDTO;
import una.ac.cr.FitFlow.model.CompletedActivity;

@Component
public class MapperForCompletedActivity {

    public CompletedActivityOutputDTO toDto(CompletedActivity c) {
        if (c == null) return null;
        return CompletedActivityOutputDTO.builder()
                .id(c.getId())
                .completedAt(c.getCompletedAt())
                .notes(c.getNotes())
                .progressLogId(c.getProgressLog() != null ? c.getProgressLog().getId() : null)
                .habitId(c.getHabit() != null ? c.getHabit().getId() : null)
                .build();
    }

    public void copyBasics(CompletedActivityInputDTO in, CompletedActivity target) {
        if (in.getCompletedAt() != null) target.setCompletedAt(in.getCompletedAt()); 
        if (in.getNotes() != null)       target.setNotes(in.getNotes());
    }
}
