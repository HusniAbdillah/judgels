package judgels.gabriel.languages;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class GradingLanguageRegistryTests {
    @Test
    void get() {
        assertThat(GradingLanguageRegistry.getInstance().get("Cpp").getName()).isEqualTo("C++");
        assertThat(GradingLanguageRegistry.getInstance().get("C").getName()).isEqualTo("C");
        assertThat(GradingLanguageRegistry.getInstance().get("Scala2").getName()).isEqualTo("Scala 2");
        assertThat(GradingLanguageRegistry.getInstance().get("Scala3").getName()).isEqualTo("Scala 3");
    }

    @Test
    void getNamesMap() {
        assertThat(GradingLanguageRegistry.getInstance().getLanguages()).containsEntry("Cpp", "C++");
        assertThat(GradingLanguageRegistry.getInstance().getLanguages()).containsEntry("C", "C");
        assertThat(GradingLanguageRegistry.getInstance().getLanguages()).containsEntry("Scala2", "Scala 2");
        assertThat(GradingLanguageRegistry.getInstance().getLanguages()).containsEntry("Scala3", "Scala 3");
    }
}
