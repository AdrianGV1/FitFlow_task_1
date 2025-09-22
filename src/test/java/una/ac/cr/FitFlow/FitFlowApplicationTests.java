package una.ac.cr.FitFlow;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

class FitFlowApplicationTests {

    @Test
    void contextLoads() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(eq(FitFlowApplication.class), any(String[].class)))
                  .thenReturn(null);

            FitFlowApplication.main(new String[]{});

            mocked.verify(() -> SpringApplication.run(eq(FitFlowApplication.class), any(String[].class)));
        }
    }

    @Test
    void mainDoesNotThrow() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(eq(FitFlowApplication.class), any(String[].class)))
                  .thenReturn(null);

            assertDoesNotThrow(() -> FitFlowApplication.main(new String[]{"--test"}));
        }
    }
}
