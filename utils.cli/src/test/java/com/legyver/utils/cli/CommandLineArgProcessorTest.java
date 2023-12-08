package com.legyver.utils.cli;

import com.legyver.utils.cli.exception.InvalidOptionException;
import com.legyver.utils.cli.exception.InvalidUsageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.junit.jupiter.api.Assertions.fail;

public class CommandLineArgProcessorTest {

    private Menu menu;
    private OperatingContext operatingContext;
    private CommandLineArgProcessor commandLineArgProcessor;
    private OutputStream helpOutputStream;

    @BeforeEach
    public void setup() throws InvalidOptionException {
        operatingContext = new OperatingContext();
        helpOutputStream = new ByteArrayOutputStream();
        menu = Menu.builder(helpOutputStream)
                .withOption("--header")
                    .description("Add a request header")
                    .usage("--header key1=value1 key2=value2")
                    .onArgument(s -> {
                        String[] parts = s.trim().split("=");
                        operatingContext.headers.put(parts[0], parts[1]);
                    })
                .newOption("--url")
                    .description("Specify a URL")
                    .usage("--url https://somesite.com/path")
                    .onArgument(s -> {
                        operatingContext.url = s;
                    })
                .newOption("--query")
                    .description("specify or override a query parameter")
                    .usage("--query p=crossed o=dotted")
                    .onArgument(s -> {
                        String[] parts = s.trim().split("=");
                        operatingContext.queryParams.put(parts[0], parts[1]);
                    })
                .newOption("--auth:basic")
                    .description("include basic authorization header on request")
                    .usage("--auth:basic --file my_credentials.properties or --auth:basic --user myname --pass mypass")
                    .onSelection((x) -> operatingContext.auth = "Basic")
                    .validation(() -> {
                        if (operatingContext.username != null && operatingContext.password == null) {
                            throw new InvalidUsageException("Password is required when specifying username");
                        }
                        if (operatingContext.username == null && operatingContext.authFile == null) {
                            throw new InvalidUsageException("Either username and password or credential file is required");
                        }
                    })
                    .supportSubOptions(
                            (Menu.SubOption) Menu.subOption("--file")
                                    .description("reads credentials from file")
                                    .onArgument(s -> operatingContext.authFile = s),
                            (Menu.SubOption) Menu.subOption("--creds")
                                    .description("specifies credentials (username/password)")
                                    .onArgument(s -> {
                                        int firstSlash = s.indexOf('/');
                                        if (firstSlash < 0) {
                                            operatingContext.username = s;
                                        } else {
                                            operatingContext.username = s.substring(0, firstSlash);
                                            operatingContext.password = s.substring(firstSlash + 1);
                                        }
                                    }),
                            (Menu.SubOption) Menu.subOption("--user")
                                    .description("specifies a username")
                                    .onArgument(s -> operatingContext.username = s),
                            (Menu.SubOption) Menu.subOption("--pass")
                                    .description("specifies a password")
                                    .onArgument(s -> operatingContext.password = s)
                    )
                .build();
        commandLineArgProcessor = new CommandLineArgProcessor(menu);
    }

    private void reset(OperatingContext operatingContext) {
        operatingContext.url = null;
        operatingContext.authFile = null;
        operatingContext.username = null;
        operatingContext.password = null;
        operatingContext.headers.clear();
        operatingContext.queryParams.clear();
    }

    private String getExpectedHelp() {
        String lineEnding = "\\".equals(File.separator) ? "\r\n" : "\n";
        return new StringJoiner(lineEnding)
                .add("--header/-h\tAdd a request header")
                .add("\t\tUsage: --header key1=value1 key2=value2")
                .add("")
                .add("--url/-u\tSpecify a URL")
                .add("\t\tUsage: --url https://somesite.com/path")
                .add("")
                .add("--query/-q\tspecify or override a query parameter")
                .add("\t\tUsage: --query p=crossed o=dotted")
                .add("")
                .add("--auth:basic\tinclude basic authorization header on request")
                .add("\t\twith --file/-f, reads credentials from file")
                .add("\t\twith --creds/-c, specifies credentials (username/password)")
                .add("\t\twith --user/-u, specifies a username")
                .add("\t\twith --pass/-p, specifies a password")
                .add("\t\tUsage: --auth:basic --file my_credentials.properties or --auth:basic --user myname --pass mypass")
                .add("")
                .add("")
                .toString();
    }

    @Test
    public void help() throws Exception {
        String expectedHelp = getExpectedHelp();

        {
            String[] helpLongParams = new String[]{
                    "--help"
            };
            commandLineArgProcessor.process(helpLongParams);
            String helpString = helpOutputStream.toString();
            assertThat(helpString).isEqualTo(expectedHelp);
        }
    }

    @Test
    public void helpShort() throws Exception {
        String expectedHelp = getExpectedHelp();
        {
            String[] helpShortParams = new String[]{
                    "/?"
            };
            commandLineArgProcessor.process(helpShortParams);
            String helpString = helpOutputStream.toString();
            assertThat(helpString).isEqualTo(expectedHelp);
        }
    }

    @Test
    public void urlWithModifiedParams() throws Exception {
        String[] noChangeParams = new String[] {
                "--url", "https://example.com/v1/texts/search?text=sometext&modified-after=2023-11-30T15:51:09.710Z&pagesize=100&page=1"
        };
        commandLineArgProcessor.process(noChangeParams);
        assertThat(operatingContext.url)
                .isEqualTo("https://example.com/v1/texts/search?text=sometext&modified-after=2023-11-30T15:51:09.710Z&pagesize=100&page=1");
        assertThat(operatingContext.queryParams).isEmpty();
        reset(operatingContext);

        String[] changedParams = new String[] {
                "--url", "https://example.com/v1/texts/search?text=sometext&modified-after=2023-11-30T15:51:09.710Z&pagesize=100&page=1",
                "--query", "modified-after=2023-11-30T15:51:09.710Z",
                "--query", "pagesize=150",
                "--query", "page=2",
                "--query", "whatever=1"
        };
        commandLineArgProcessor.process(changedParams);
        assertThat(operatingContext.queryParams).contains(
                entry("modified-after", "2023-11-30T15:51:09.710Z"),
                entry("pagesize", "150"),
                entry("page", "2"),
                entry("whatever", "1")
        );
        reset(operatingContext);

        String[] changedShortParams = new String[] {
                "-u", "https://example.com/v1/texts/search?text=sometext&modified-after=2023-11-30T15:51:09.710Z&pagesize=100&page=1",
                "-q", "modified-after=2023-11-30T15:51:09.710Z",
                "-q", "pagesize=150",
                "-q", "page=2",
                "-q", "whatever=1"
        };
        commandLineArgProcessor.process(changedShortParams);
        assertThat(operatingContext.queryParams).contains(
                entry("modified-after", "2023-11-30T15:51:09.710Z"),
                entry("pagesize", "150"),
                entry("page", "2"),
                entry("whatever", "1")
        );
    }

    @Test
    public void headers() throws Exception {
        String[] urlAndHeaders = new String[] {
                "--url", "https://example.com/v1/texts/search?text=sometext&modified-after=2023-11-30T15:51:09.710Z&pagesize=100&page=1",
                "--header", "Accept=application/xml",
                "--header", "Accept-Encoding=identity"
        };
        commandLineArgProcessor.process(urlAndHeaders);
        assertThat(operatingContext.headers).contains(
                entry("Accept", "application/xml"),
                entry("Accept-Encoding", "identity")
        );
    }

    @Test
    public void auth() throws Exception {
        String[] urlWithAuthUserPass = new String[] {
                "--url", "https://example.com/v1/texts/search?text=sometext&modified-after=2023-11-30T15:51:09.710Z&pagesize=100&page=1",
                "--auth:basic",
                    "--user", "test.user",
                    "--pass", "test.pass"
        };
        commandLineArgProcessor.process(urlWithAuthUserPass);
        assertThat(operatingContext.username).isEqualTo("test.user");
        assertThat(operatingContext.password).isEqualTo("test.pass");
        reset(operatingContext);

        String[] urlWithAuthUserPassTogether = new String[] {
                "--url", "https://example.com/v1/texts/search?text=sometext&modified-after=2023-11-30T15:51:09.710Z&pagesize=100&page=1",
                "--auth:basic",
                    "--creds", "test.user/test.pass"
        };
        commandLineArgProcessor.process(urlWithAuthUserPassTogether);
        assertThat(operatingContext.username).isEqualTo("test.user");
        assertThat(operatingContext.password).isEqualTo("test.pass");
        reset(operatingContext);

        String[] urlWithAuthFile = new String[] {
                "--url", "https://example.com/v1/texts/search?text=sometext&modified-after=2023-11-30T15:51:09.710Z&pagesize=100&page=1",
                "--auth:basic",
                    "--file", "test-user.properties"
        };
        commandLineArgProcessor.process(urlWithAuthFile);
        assertThat(operatingContext.authFile).isEqualTo("test-user.properties");
        reset(operatingContext);

        String[] urlWithAuthFileShortVersions = new String[] {
                "-u", "https://example.com/v1/texts/search?text=sometext&modified-after=2023-11-30T15:51:09.710Z&pagesize=100&page=1",
                "--auth:basic",
                "-u", "test.user",
                "-p", "test.pass"
        };
        commandLineArgProcessor.process(urlWithAuthFileShortVersions);
        assertThat(operatingContext.username).isEqualTo("test.user");
        assertThat(operatingContext.password).isEqualTo("test.pass");
    }

    private void runAuthValidation(CommandLineArgProcessor commandLineArgProcessor, boolean expectFailure) throws Exception {
        //custom validator
        try {
            String[] authWithoutCreds = new String[] {
                    "--auth:basic"
            };
            commandLineArgProcessor.process(authWithoutCreds);
            if (expectFailure) {
                fail("Expected exception be thrown for no credentials");
            }
        } catch (InvalidUsageException exception) {
            if (!expectFailure) {
                throw exception;
            }
            assertThat(exception.getMessage()).isEqualTo( "Menu item [--auth:basic] requires additional context.  See --help for available options");
        }
        try {
            String[] authWithoutPassword = new String[] {
                    "--auth:basic",
                    "--user",
                    "test.user"
            };
            commandLineArgProcessor.process(authWithoutPassword);
            if (expectFailure) {
                fail("Expected exception be thrown for no password");
            }
        } catch (InvalidUsageException exception) {
            if (!expectFailure) {
                throw exception;
            }
            assertThat(exception.getMessage()).isEqualTo( "Password is required when specifying username");
        }
    }

    private void runUrlValidation(CommandLineArgProcessor commandLineArgProcessor, boolean expectFailure, String[] args) throws Exception {
        //default --url requires url
        try {
            commandLineArgProcessor.process(args);
            if (expectFailure) {
                fail("Expected exception be thrown for no url");
            }
        } catch (InvalidUsageException exception) {
            if (!expectFailure) {
                throw exception;
            }
            assertThat(exception.getMessage()).isEqualTo( "Menu item [--url/-u] requires a value");
        }
    }

    private void runUrlValidation(CommandLineArgProcessor commandLineArgProcessor, boolean expectFailure) throws Exception {
        runUrlValidation(commandLineArgProcessor, expectFailure, new String[] {
                "--url"
        });
    }


    @Test
    public void validator() throws Exception {
        runAuthValidation(commandLineArgProcessor, true);
        runUrlValidation(commandLineArgProcessor, true);
        {
            Menu menu = Menu.builder()
                    .withOption("--url")
                    .requireInput(true)
                    .build();
            runUrlValidation(new CommandLineArgProcessor(menu), true);
        }
        {
            Menu menu = Menu.builder()
                    .withOption("--url")
                    .requireInput(false)
                    .build();
            runUrlValidation(new CommandLineArgProcessor(menu), false);
        }
        {
            Menu menu = Menu.builder()
                    .withOption("--url")
                    .onArgument(x -> {})
                    .newOption("--save")
                    .build();
            CommandLineArgProcessor commandLineArgProcessor = new CommandLineArgProcessor(menu);
            runUrlValidation(commandLineArgProcessor, true, new String[] {
                    "--url",
                    "--save"
            });
            runUrlValidation(commandLineArgProcessor, false, new String[] {
                    "--url",
                    "https://example.com",
                    "--save"
            });
        }
    }

    @Test
    public void implicitLetter() throws Exception {
        //implicit auto letter
        Menu menu = Menu.builder()
                .withOption("--header")
                .build();
        Optional<Menu.Option> explicit = menu.getSelectedOption("-h");
        assertThat(explicit.isPresent()).isTrue();
        assertThat(explicit.get().getTextArgs()).isEqualTo("--header/-h");
    }

    @Test
    public void duplicateLetterHandling() throws Exception {
        try {//both explicit
            Menu.builder()
                    .withOption("--header")
                        .optionLetter("-h")
                    .newOption("--h2")
                        .optionLetter("-h")
                    .build();
        } catch (InvalidOptionException ex) {
            assertThat(ex.getMessage()).isEqualTo("There already is an option with letter 'h'. Preexisting [--header/-h] conflicts with: --h2/-h");
        }

        {
            //first one auto letter
            Menu menu = Menu.builder()
                    .withOption("--header")
                    .newOption("--h2")
                    .optionLetter("-h")
                    .build();
            Optional<Menu.Option> explicit = menu.getSelectedOption("-h");
            assertThat(explicit.isPresent()).isTrue();
            assertThat(explicit.get().getTextArgs()).isEqualTo("--h2/-h");
        }

        {
            //second one auto letter
            Menu menu = Menu.builder()
                    .withOption("--header")
                        .optionLetter("-h")
                    .newOption("--h2")
                    .build();
            Optional<Menu.Option> explicit = menu.getSelectedOption("-h");
            assertThat(explicit.isPresent()).isTrue();
            assertThat(explicit.get().getTextArgs()).isEqualTo("--header/-h");
        }
        {
            //both auto
            Menu menu = Menu.builder()
                    .withOption("--header")
                    .newOption("--h2")
                    .build();
            Optional<Menu.Option> explicit = menu.getSelectedOption("-h");
            assertThat(explicit.isPresent()).isFalse();//both -h auto letters will be removed as they conflict
        }
    }

    @Test
    public void duplicateNameHandling() throws Exception {
        try {
            Menu.builder()
                    .withOption("--header")
                    .newOption("--header")
                    .build();
        } catch (InvalidOptionException ex) {
            assertThat(ex.getMessage()).isEqualTo("There already is an option with name 'header'. Preexisting [--header] conflicts with: --header");
        }
    }

    @Test
    public void duplicateNameHandlingSubMenu() throws Exception {
        try {
            Menu.builder()
                    .withOption("--one")
                    .supportSubOptions(Menu.subOption("--header"), Menu.subOption("--header"))
                    .build();
        } catch (InvalidOptionException ex) {
            assertThat(ex.getMessage()).isEqualTo("There already is an option with name 'header'. Preexisting [--header/-h] conflicts with: --header/-h");
        }
    }

    @Test
    public void duplicateLetterHandlingSubMenu() throws Exception {
        try {
            Menu.builder()
                    .withOption("--one")
                    .supportSubOptions(
                            (Menu.SubOption) Menu.subOption("--header1").optionLetter("-h"),
                            (Menu.SubOption) Menu.subOption("--header2").optionLetter("-h"))
                    .build();
        } catch (InvalidOptionException ex) {
            assertThat(ex.getMessage()).isEqualTo("There already is an option with letter 'h'. Preexisting [--header1/-h] conflicts with: --header2/-h");
        }
        {//second automatic
            Menu menu = Menu.builder()
                    .withOption("--one")
                    .supportSubOptions(
                            (Menu.SubOption) Menu.subOption("--header1").optionLetter("-h"),
                            (Menu.SubOption) Menu.subOption("--header2"))
                    .build();
            Menu.Option option = menu.getSelectedOption("--one").get();
            Menu.Option header1 = option.getBestMatchByDistance("-h",0).get();
            assertThat(header1.optionName).isEqualTo("header1");
        }
        {//first automatic
            Menu menu = Menu.builder()
                    .withOption("--one")
                    .supportSubOptions(
                            (Menu.SubOption) Menu.subOption("--header1"),
                            (Menu.SubOption) Menu.subOption("--header2").optionLetter("-h"))
                    .build();
            Menu.Option option = menu.getSelectedOption("--one").get();
            Menu.Option header1 = option.getBestMatchByDistance("-h",0).get();
            assertThat(header1.optionName).isEqualTo("header2");
        }
        {//both automatic
            Menu menu = Menu.builder()
                    .withOption("--one")
                    .supportSubOptions(
                            (Menu.SubOption) Menu.subOption("--header1").description("d1"),//both will be automatically assigned 'h' as letter initially and then be nulled out due to conflict
                            (Menu.SubOption) Menu.subOption("--header2").description("d2"))
                    .build();
            Optional<Menu.Option> optionOneOptional = menu.getSelectedOption("--one");
            assertThat(optionOneOptional.isPresent()).isTrue();
            Menu.Option optionOne = optionOneOptional.get();
            Menu.Option subOption1 = optionOne.getBestMatchByDistance("--header1", 0).get();
            assertThat(subOption1.description).isEqualTo("d1");
            assertThat(subOption1.optionLetter).isNull();
            Menu.Option subOption2 = optionOne.getBestMatchByDistance("--header2", 0).get();
            assertThat(subOption2.description).isEqualTo("d2");
            assertThat(subOption2.optionLetter).isNull();
        }
    }

    @Test
    public void hintsMainMenu() throws Exception {
        try {
            String[] args = new String[] {
                    "--abc", "https://example.com/v1/texts/search?text=sometext&modified-after=2023-11-30T15:51:09.710Z&pagesize=100&page=1"
            };
            commandLineArgProcessor.process(args);
        } catch (InvalidOptionException ex) {
            assertThat(ex.getMessage()).isEqualTo("Invalid Option: --abc");
        }

        try {
            String[] args = new String[] {
                    "--irl", "https://example.com/v1/texts/search?text=sometext&modified-after=2023-11-30T15:51:09.710Z&pagesize=100&page=1"
            };
            commandLineArgProcessor.process(args);
        } catch (InvalidOptionException ex) {
            assertThat(ex.getMessage()).isEqualTo("No option matches: --irl.  Did you mean --url/-u?");
        }

        try {
            String[] args = new String[] {
                    "--curl", "https://example.com/v1/texts/search?text=sometext&modified-after=2023-11-30T15:51:09.710Z&pagesize=100&page=1"
            };
            commandLineArgProcessor.process(args);
        } catch (InvalidOptionException ex) {
            assertThat(ex.getMessage()).isEqualTo("No option matches: --curl.  Did you mean --url/-u?");
        }

        try {
            String[] args = new String[] {
                    "--rl", "https://example.com/v1/texts/search?text=sometext&modified-after=2023-11-30T15:51:09.710Z&pagesize=100&page=1"
            };
            commandLineArgProcessor.process(args);
        } catch (InvalidOptionException ex) {
            assertThat(ex.getMessage()).isEqualTo("No option matches: --rl.  Did you mean --url/-u?");
        }
    }

    @Test
    public void hintsSubMenu() throws Exception {
        try {
            String[] args = new String[] {
                    "--auth:basic",
                    "--abc", "https://example.com/v1/texts/search?text=sometext&modified-after=2023-11-30T15:51:09.710Z&pagesize=100&page=1"
            };
            commandLineArgProcessor.process(args);
        } catch (InvalidOptionException ex) {
            assertThat(ex.getMessage()).isEqualTo("Invalid Option: --abc");
        }

        try {
            String[] args = new String[] {
                    "--auth:basic",
                    "--File", "creds.properties"
            };
            commandLineArgProcessor.process(args);
        } catch (InvalidOptionException ex) {
            assertThat(ex.getMessage()).isEqualTo("No option matches: --File.  Did you mean --file/-f?");
        }

        try {
            String[] args = new String[] {
                    "--auth:basic",
                    "--ile", "creds.properties"
            };
            commandLineArgProcessor.process(args);
        } catch (InvalidOptionException ex) {
            assertThat(ex.getMessage()).isEqualTo("No option matches: --ile.  Did you mean --file/-f?");
        }

        try {
            String[] args = new String[] {
                    "--auth:basic",
                    "--files", "creds.properties"
            };
            commandLineArgProcessor.process(args);
        } catch (InvalidOptionException ex) {
            assertThat(ex.getMessage()).isEqualTo("No option matches: --files.  Did you mean --file/-f?");
        }

        try {
            String[] args = new String[] {
                    "--auth:basic",
                    "--username", "test.user"
            };
            commandLineArgProcessor.process(args);
        } catch (InvalidOptionException ex) {
            assertThat(ex.getMessage()).isEqualTo("No option matches: --username.  Did you mean --user/-u?");
        }

    }

    private static class OperatingContext {
        private final Map<String, String> headers = new HashMap<>();
        private final Map<String, String> queryParams = new HashMap<>();
        public String auth;
        private String url;
        private String authFile;
        private String username;
        private String password;
        private String helpMessage;
    }
}
