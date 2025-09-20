package una.ac.cr.FitFlow.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import una.ac.cr.FitFlow.model.Guide;

public interface GuideRepository extends JpaRepository<Guide, Long> {
    Page<Guide> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Guide> findByCategory(Guide.Category category, Pageable pageable);

    Page<Guide> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
            String title, String content, Pageable pageable);
}
