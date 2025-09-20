package una.ac.cr.FitFlow.mapper;

import org.springframework.stereotype.Component;
import una.ac.cr.FitFlow.dto.Habit.HabitInputDTO;
import una.ac.cr.FitFlow.dto.Habit.HabitOutputDTO;
import una.ac.cr.FitFlow.model.Habit;

@Component
public class MapperForHabit {

    public HabitOutputDTO toDto(Habit h) {
        if (h == null) return null;
        return HabitOutputDTO.builder()
                .id(h.getId())
                .name(h.getName())
                .category(h.getCategory() != null ? h.getCategory().name() : null)
                .description(h.getDescription())
                .build();
    }
    
    public Habit toEntity(HabitInputDTO in) {
        if (in == null) return null;
        return Habit.builder()
                .name(in.getName())
                .category(parseCategory(in.getCategory()))
                .description(in.getDescription())
                .build();
    }

    public void copyToEntity(HabitInputDTO in, Habit target) {
        if (in.getName() != null)       target.setName(in.getName());
        if (in.getCategory() != null)   target.setCategory(parseCategory(in.getCategory()));
        if (in.getDescription() != null) target.setDescription(in.getDescription());
    }

    private Habit.Category parseCategory(String raw) {
        if (raw == null || raw.isBlank())
            throw new IllegalArgumentException("La categoría es obligatoria.");
        try {
            return Habit.Category.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                "Categoría inválida: " + raw + ". Use PHYSICAL, MENTAL, SLEEP o DIET."
            );
        }
    }
}
