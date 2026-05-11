package judgels.gabriel.api;

import java.util.List;

public interface GradingLanguage {
    String getName();
    boolean isVisible();
    List<String> getAllowedExtensions();
    default int getCompilationTimeLimitInMilliseconds() {
        return 20 * 1000;
    }
    List<String> getCompilationCommand(String sourceFilename, String... sourceFilenames);
    String getExecutableFilename(String sourceFilename);
    List<String> getExecutionCommand(String sourceFilename);
}
