package com.legyver.utils.cli;

import com.legyver.utils.cli.exception.InvalidOptionException;
import com.legyver.utils.cli.exception.InvalidUsageException;
import com.legyver.utils.cli.internal.StateMachine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Command line argument processor
 * Constructs a state machine to parse the arguments as passed in from command line
 */
public class CommandLineArgProcessor {
    private static final Logger logger = LogManager.getLogger(CommandLineArgProcessor.class);
    private final Menu commandLineMenu;

    /**
     * Construct a CommandLineArgProcessor around a given Menu
     * @param commandLineMenu the menu of supported options
     */
    public CommandLineArgProcessor(Menu commandLineMenu) {
        this.commandLineMenu = commandLineMenu;
    }

    /**
     * Process menu options as past in from the command line
     * @param args the command line arguments
     * @throws InvalidOptionException if the menu does not contain the specified argument as an option
     * @throws InvalidUsageException if the usage of a menu item is not correct
     */
    public void process(String[] args) throws InvalidOptionException, InvalidUsageException {
        if (args == null || args.length == 0) {
            logger.debug("No args provided");
        } else {
            StateMachine stateMachine = new StateMachine(commandLineMenu);
            for (String arg : args) {
                if (arg.startsWith("-") || arg.startsWith("/?")) {
                    stateMachine.transition(arg);
                } else {
                    stateMachine.accept(arg);
                }
            }
            stateMachine.validate();
        }
    }
}
