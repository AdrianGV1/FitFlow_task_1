package una.ac.cr.FitFlow.config;

import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import graphql.schema.idl.RuntimeWiring;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import static org.assertj.core.api.Assertions.assertThat;

class GraphQLScalarConfigTest {

    @Test
    void runtimeWiringConfigurer_registersLongAndDateTimeScalars() {
        // Arrange
        GraphQLScalarConfig config = new GraphQLScalarConfig();
        RuntimeWiringConfigurer configurer = config.runtimeWiringConfigurer();

        RuntimeWiring.Builder builder = RuntimeWiring.newRuntimeWiring();

        // Act
        configurer.configure(builder);
        RuntimeWiring wiring = builder.build();

        // Assert
        GraphQLScalarType longScalar = wiring.getScalars().get("Long");
        GraphQLScalarType dateTimeScalar = wiring.getScalars().get("DateTime");

        assertThat(longScalar).isNotNull();
        assertThat(longScalar).isEqualTo(ExtendedScalars.GraphQLLong);

        assertThat(dateTimeScalar).isNotNull();
        assertThat(dateTimeScalar).isEqualTo(ExtendedScalars.DateTime);
    }
}
