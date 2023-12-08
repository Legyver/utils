package com.legyver.utils.cli.internal;

import com.legyver.utils.cli.Menu;
import com.legyver.utils.cli.exception.InvalidOptionException;
import com.legyver.utils.cli.exception.InvalidUsageException;

import java.util.Optional;

/**
 * State Machine to handle repeatable and varying depth parameter chains
 */
public class StateMachine {
    private static Double DEFAULT_SUGGESTION_APPROXIMATION = 7d;
    private final Menu commandLineMenu;
    private Menu.Option currentMenuOption;

    /**
     * Construct a StateMachine around a given menu
     * @param commandLineMenu the menu of valid options
     */
    public StateMachine(Menu commandLineMenu) {
        this.commandLineMenu = commandLineMenu;
    }

    /**
     * Transition to a state based on a valid menu option
     * @param arg the arg specifying the menu option
     * @throws InvalidOptionException if the menu option is not, in fact, valid
     * @throws InvalidUsageException if a menu item has not received all necessary information
     */
    public void transition(String arg) throws InvalidOptionException, InvalidUsageException {
        if (currentMenuOption == null) {
            Optional<? extends Menu.Option> selectedOption = commandLineMenu.getSelectedOption(arg);
            if (selectedOption.isPresent()) {
                currentMenuOption = selectedOption.get();
                currentMenuOption.select();
            } else {
                getMenuHint(arg, () -> {
                    return commandLineMenu.getBestMatchByDistance(arg, DEFAULT_SUGGESTION_APPROXIMATION);
                });
            }
        } else {
            Optional<? extends Menu.Option> selectedOption = currentMenuOption.getBestMatchByDistance(arg, 0);
            if (selectedOption.isPresent()) {
                currentMenuOption.validate(selectedOption.get());
                currentMenuOption = selectedOption.get();
                currentMenuOption.select();
            } else {
                getMenuHint(arg,  () -> {
                    return currentMenuOption.getBestMatchByDistance(arg, DEFAULT_SUGGESTION_APPROXIMATION);
                });
            }
        }
    }

    private static void getMenuHint(String arg, HintSupplier hintSupplier) throws InvalidOptionException {
        Optional<? extends Menu.Option> bestMatch = hintSupplier.getHint();
        if (bestMatch.isPresent()) {
            throw new InvalidOptionException("No option matches: " + arg + ".  Did you mean " + bestMatch.get().getTextArgs() + "?");
        } else {
            throw new InvalidOptionException("Invalid Option: " + arg);
        }
    }

    /**
     * Accept an argument that is not a menu option but rather an argument to augment the previous menu option
     * @param value the value that should be accepted by the menu option
     */
    public void accept(String value) {
        currentMenuOption.accept(value);
    }

    /**
     * Validate that the received arguments fulfilled the menus requirements.
     * Note: This specifically checks if the final menu option received necessary inputs
     * @throws InvalidUsageException if a menu item has not received all necessary inputs
     */
    public void validate() throws InvalidUsageException {
        if (currentMenuOption != null) {
            currentMenuOption.validate(null);
        }
    }

    @FunctionalInterface
    private interface HintSupplier {
        Optional<? extends Menu.Option> getHint() throws InvalidOptionException;
    }
}
