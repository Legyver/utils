# utils-cli
description = 'A library to make benefit command line interfaces'

```groovy
implementation group: 'com.legyver', name: 'utils-cli', version: '3.5.1'
```

## Features
- Example with long names
```shell
MyProgram --url https://example.com \
 --header Accept=application/json Accept-Encoding=gzip \
 --auth:basic --user myuser --pass mypassword
```

- Example with short letters (Note ':' values not automatically shortened)
```shell
MyProgram -u https://example.com \
 -h Accept=application/json Accept-Encoding=gzip \
 --auth:basic -u myuser -p mypassword
```

- Example built-in validation
```shell
MyProgram --url \
 --auth:basic -u myuser -p mypassword
Error: Menu item [--url/-u] requires a value
```

```shell
MyProgram --url https://example.com \
 --auth:basic
Error: Menu item [-auth:basic] requires additional context.  See --help for options
```

- Validation can be customized
```java
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
            (Menu.SubOption) Menu.subOption("--user")
                    .description("specifies a username")
                    .onArgument(s -> operatingContext.username = s),
            (Menu.SubOption) Menu.subOption("--pass")
                    .description("specifies a password")
                    .onArgument(s -> operatingContext.password = s)
    )
```

- All menu options by default included in help (--help or /?)
```shell
MyProgram --help
--url/-u        Specify a URL
                Usage: --url https://somesite.com/path

--header/-h     Add a request header
                Usage: --header key1=value1 key2=value2

--query/-q      Add or override a query parameter
                Usage: --query p=crossed o=dotted

--auth:basic    Include basic authorization header on request
                with --file/-f, reads credentials from file
                with --creds/-c, specifies credentials (username/password)
                with --user/-u, specifies a username
                with --pass/-p, specifies a password
                Usage: --auth:basic --file my_credentials.properties or --auth:basic --user myname --pass mypass or
                --auth:basic --creds myuser/mypass
```

- All long-name menu options have short-name equivalents automatically generated.

Collisions result in the automatically generated ones being removed.  If two identical entries are specified an InvalidOptionException is thrown.  

- Any unhandled option starting with '-' raises an Exception.
```shell
MyProgram --invalid
InvalidOptionException: No such option '--invalid'
```

- When possible, a closest-neighbor option is calculated
```shell
MyProgram --irl
InvalidOptionException: No such option '--irl'.  Did you mean '--url'?
```


Code to support above
```java
public class Demo {
    public static void main(String[] args) {
        try {
            new CommandLineArgProcessor(buildMenu()).process(args);
        } catch (InvalidOptionException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    private static buildMenu() {
        return Menu.builder()
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
                .onSelection(x -> operatingContext.auth = Auth.BASIC)
                .supportSubOptions(
                        (Menu.SubOption) Menu.subOption("--file")
                                .description("reads credentials from file")
                                .onArgument(s -> operatingContext.authFile = s),
                        (Menu.SubOption) Menu.subOption("--user")
                                .description("specifies a username")
                                .onArgument(s -> operatingContext.username = s),
                        (Menu.SubOption) Menu.subOption("--pass")
                                .description("specifies a password")
                                .onArgument(s -> operatingContext.password = s)
                )
                .build();
    }
}
```
Since 3.5.0