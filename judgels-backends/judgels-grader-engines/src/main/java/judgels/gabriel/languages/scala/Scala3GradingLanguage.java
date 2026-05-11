package judgels.gabriel.languages.scala;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import judgels.gabriel.api.GradingLanguage;
import org.apache.commons.io.FilenameUtils;

public class Scala3GradingLanguage implements GradingLanguage {

    @Override
    public String getName() {
        return "Scala 3";
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public List<String> getAllowedExtensions() {
        return ImmutableList.of("scala");
    }

    @Override
    public int getCompilationTimeLimitInMilliseconds() {
        return 40 * 1000;
    }

    @Override
    public List<String> getCompilationCommand(String sourceFilename, String... sourceFilenames) {
        String executableFilename = getExecutableFilename(sourceFilename);
        String baseName = FilenameUtils.removeExtension(sourceFilename);
        String compilationOutputDir = "__judgels_scala3_out";

        String scalacCommand = String.join(" ", new ImmutableList.Builder<String>()
                .add("/usr/bin/scala3-compiler", "-encoding", "UTF-8", "-d", quote(compilationOutputDir), quote(sourceFilename))
                .addAll(Lists.transform(Arrays.asList(sourceFilenames), this::quote))
                .build());

        StringBuilder jarCommandBuilder = new StringBuilder();
        jarCommandBuilder.append("ENTRY=\"Main\"; ");
        jarCommandBuilder.append(String.format("for CAND in Main main Solution solution %s; do ", quote(baseName)));
        jarCommandBuilder.append(String.format("CLASS_FILE=\"$(find \"%s\" -type f -name \"${CAND}.class\" | head -n 1)\"; ", compilationOutputDir));
        jarCommandBuilder.append(String.format("if [ -n \"$CLASS_FILE\" ]; then ENTRY=\"$(echo \"$CLASS_FILE\" | sed 's#^%s/##; s#\\.class$##; s#/#.#g')\"; break; fi; ", compilationOutputDir));
        jarCommandBuilder.append("done; ");
        jarCommandBuilder.append(String.format("if [ \"$ENTRY\" = \"Main\" ]; then TOP_MAIN=\"$(sed -n 's/^[[:space:]]*@main[[:space:]]\\+def[[:space:]]\\+\\([A-Za-z_][A-Za-z0-9_]*\\).*/\\1/p' %s | head -n 1)\"; ", quote(sourceFilename)));
        jarCommandBuilder.append(String.format("if [ -n \"$TOP_MAIN\" ]; then CLASS_FILE=\"$(find \"%s\" -type f -name \"${TOP_MAIN}.class\" | head -n 1)\"; ", compilationOutputDir));
        jarCommandBuilder.append(String.format("if [ -n \"$CLASS_FILE\" ]; then ENTRY=\"$(echo \"$CLASS_FILE\" | sed 's#^%s/##; s#\\.class$##; s#/#.#g')\"; fi; fi; fi; ", compilationOutputDir));
        jarCommandBuilder.append(String.format("if ! find \"%s\" -type f -name '*.class' | grep -q .; then exit 1; fi; ", compilationOutputDir));
        jarCommandBuilder.append(String.format("/usr/bin/jar cfe %s \"$ENTRY\" -C \"%s\" .", quote(executableFilename), compilationOutputDir));
        String jarCommand = jarCommandBuilder.toString();

        return ImmutableList.of(
            "/bin/bash",
            "-c",
            String.format(
                "rm -rf %s && mkdir -p %s && %s && %s",
                quote(compilationOutputDir),
                quote(compilationOutputDir),
                scalacCommand,
                jarCommand));
    }

    @Override
    public String getExecutableFilename(String sourceFilename) {
        return FilenameUtils.removeExtension(sourceFilename) + ".jar";
    }

    @Override
    public List<String> getExecutionCommand(String sourceFilename) {
        return ImmutableList.of(
                "/usr/bin/scala3",
                "-J-Xmx512M",
                "-J-Xms16M",
                "-J-Xss16M",
                "-J-XX:+UseSerialGC",
                "-J-XX:TieredStopAtLevel=1",
                getExecutableFilename(sourceFilename)
        );
    }

    private String quote(String value) {
        return "'" + value.replace("'", "'\"'\"'") + "'";
    }
}
