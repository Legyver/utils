package com.legyver.utils.cli;

import com.legyver.utils.cli.exception.InvalidOptionException;
import com.legyver.utils.cli.exception.InvalidUsageException;
import com.legyver.utils.cli.internal.WindowWrapFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Menu of command line options
 */
public class Menu {
    private static final Logger logger = LogManager.getLogger(Menu.class);
    private final List<Option> optionList = new ArrayList<>();
    private Option helpOption;
    private boolean helpRequested;

    private Menu() {
        //use static method
    }

    /**
     * Check if help has been requested.
     * Generally, if help has been requested, you want to abort normal program flow.
     * @return true if help has been requested
     */
    public boolean isHelpRequested() {
        return helpRequested;
    }

    /**
     * Acquire a builder where the Help menu is output to the console
     * @return the builder
     */
    public static Menu builder() {
        Menu menu = new Menu();
        menu.setupHelp(System.out);
        return menu;
    }

    /**
     * Acquire a builder where the help menu is written to the given output stream
     * @param outputStream the output stream to write the output to
     * @return the builder
     */
    public static Menu builder(OutputStream outputStream) {
        Menu menu = new Menu();
        menu.setupHelp(new PrintStream(outputStream, true));
        return menu;
    }

    /**
     * Get the menu option matching an argument
     * @param arg the argument identifying the menu option
     * @return the appropriate menu option
     * @throws InvalidOptionException if the argument is null
     */
    public Optional<Option> getSelectedOption(String arg) throws InvalidOptionException {
        if (arg == null) {
            throw new InvalidOptionException("Argument provided is null");
        }
        if ("/?".equals(arg) || helpOption.matches(arg)) {
            return Optional.of(helpOption);
        } else {
            return optionList.stream()
                    .filter(option -> option.matches(arg))
                    .findAny();
        }
    }

    private void setupHelp(PrintStream helpOutputStream)  {
        try {
            helpOption = new Option(this)
                    .optionLetter("/?")
                    .optionName("--help")
                    .onSelection(s -> {
                        for (Option option : optionList) {
                            helpOutputStream.println(option.toString());
                        }
                        this.helpRequested = true;
                    });
        } catch (InvalidOptionException e) {
            logger.error(e);
        }
    }

    private Option option(String option) throws InvalidOptionException {
        Option result = new Option(this);
        optionList.add(result);
        if (option.startsWith("--")) {
            result.optionName(option);
        } else if (option.startsWith("-")) {
            result.optionLetter(option);
        } else {
            result.description(option);
        }
        return result;
    }

    /**
     * Specify a menu option --name, -letter or description
     * @param option the option to be constructed.
     *  - If the option argument begins with a '--' the value is set as the name of the option
     *  - Otherwise, if the option argument begins with a '-' the value is set as the letter of the option
     *  - In all other cases the option argument is accepted as the description of the option
     * @return the option builder
     * @throws InvalidOptionException  if the option argument is not at least letter in length after leading hyphen removal
     */
    public Option withOption(String option) throws InvalidOptionException {
        return option(option);
    }

    /**
     * Specify a sub-menu option.
     * @param option the sub-menu option to be constructed
     *  - If the option argument begins with a '--' the value is set as the name of the sub-menu option
     *  - Otherwise, if the option argument begins with a '-' the value is set as the letter of the sub-menu option
     *  - In all other cases the option argument is accepted as the description of the sub-menu option
     * @return the submenu option builder
     * @throws InvalidOptionException if the sub-menu option value is null or there is a collision in sub-menu option names or letters.
     * Collisions will only throw exceptions if they are within the same scope (ie: top-level menu options, or sub-menu options under same menu option)
     */
    public static SubOption subOption(String option) throws InvalidOptionException {
        SubOption result = new SubOption(null);
        if (option.startsWith("--")) {
            result.optionName(option);
        } else if (option.startsWith("-")) {
            result.optionLetter(option);
        } else {
            result.description(option);
        }
        return result;
    }

    /**
     * Get the best match for a specified option name/letter.  When epsilon &gt; 0, this will attempt to derive a nearest-neighbor to the option within a letter or two deviation.
     * Delegates to {@link #getBestWeightedMatch(String)} for weight calculation
     * @param arg the argument to find the option for
     * @param epsilon the amount of deviation acceptable for the match
     * @return the best match if within specified deviation or an empty Optional
     * @throws InvalidOptionException if the arg identifying the menu item is not at least one letter in length, not counting the leading hyphens
     */
    public Optional<? extends Option> getBestMatchByDistance(String arg, double epsilon) throws InvalidOptionException {
        WeightedOption bestWeightedOption = getBestWeightedMatch(arg);
        return bestWeightedOption == null ? Optional.empty()
                : bestWeightedOption.weight <= epsilon ? Optional.of(bestWeightedOption.option)
                : Optional.empty();
    }

    

    /**
     * Get the best-weighted match for a specified letter/name
     * @param arg the argument to find the option for
     * @return null if there are no calculated distances; otherwise, the lowest distance option
     * @throws InvalidOptionException if the arg identifying the menu item is not at least one letter in length, not counting the leading hyphens
     */
    private WeightedOption getBestWeightedMatch(String arg) throws InvalidOptionException {
        Map<Double, WeightedOption> calculatedDistances = new HashMap<>();
        String noDash = stripLeadingHyphens(arg);
        for (Option option : optionList) {
            WeightedOption weightedOption = option.calculateBestMatch(noDash, false, false);
            if (weightedOption != null) {
                if (weightedOption.weight == 0) {
                    return weightedOption;
                } else if (weightedOption.weight > -1) {
                    calculatedDistances.putIfAbsent(weightedOption.weight, weightedOption);
                }
            }
        }
       return bestInMap(calculatedDistances);
    }

    /**
     * Remove one or two leading hyphens
     * @param option the option to remove leading hyphens from
     * @return the option sans leading hyphen.
     * @throws InvalidOptionException if the option argument is not at least letter in length after leading hyphen removal
     */
    private static String stripLeadingHyphens(String option) throws InvalidOptionException {
        String result = option.startsWith("--") ? option.substring(2)
                : option.startsWith("-") ? option.substring(1)
                : option;
        if (result.length() < 1) {
            throw new InvalidOptionException("Invalid option: " + option);
        }
        return result;
    }

    private static WeightedOption bestInMap(Map<Double, WeightedOption> calculatedDistances) {
        if (calculatedDistances.isEmpty()) {
            return null;
        }
        List<Double> distances = new ArrayList<>(calculatedDistances.keySet());
        Collections.sort(distances);

        return calculatedDistances.get(distances.get(0));
    }

    private static int sumSquaresDistance(String noDash, int offsetNoDash, String current, int offsetCurrent) {
        int sum = 0;
        for (int i = 0; i + offsetNoDash < noDash.length() && i + offsetCurrent < current.length(); i++) {
            String noDashI = String.valueOf(noDash.charAt(offsetNoDash + i));
            String currentI = String.valueOf(current.charAt(offsetCurrent + i));
            if (noDashI.equals(currentI)) {
                //skip incrementing by zero
            } else if (noDashI.equalsIgnoreCase(currentI)) {
                sum += 9;//3^2
            } else {
                sum += 25;//5^2
            }
        }
        int diff = noDash.length() - current.length();
        if (diff != 0) {
            if (current.startsWith(noDash) || noDash.startsWith(current)
                    || current.endsWith(noDash) || noDash.endsWith(current)
            ) {
                sum += Math.abs(diff);
            } else{
                sum += Math.pow(diff, 2) * 49; //7^2
            }
        }
        return sum;
    }

    private static double calculateDistance(String noDash, String current) {
        int noOffsetNameScore = sumSquaresDistance(noDash, 0, current, 0);
        int offsetNameScore = -1;
        int diff = noDash.length() - current.length();
        for (int i = 1; i <= Math.abs(diff); i++) {
            if (diff > 0) {
                int calcSum = sumSquaresDistance(noDash, i, current, 0);
                if (offsetNameScore < 0 || offsetNameScore > calcSum) {
                    offsetNameScore = calcSum;
                }
            } else if (diff < 0) {
                int calcSum = sumSquaresDistance(noDash, 0, current, i);
                if (offsetNameScore < 0 || offsetNameScore > calcSum) {
                    offsetNameScore = calcSum;
                }
            }
        }

        int chosenSum;
        if (offsetNameScore == -1 || noOffsetNameScore < offsetNameScore) {
            chosenSum = noOffsetNameScore;
        } else {
            chosenSum = offsetNameScore;
        }
        return Math.sqrt(chosenSum);
    }

    private static String getLineSeparator() {
        return "\\".equals(File.separator) ? "\r\n" : "\n";
    }


    /**
     * A menu option for a command line menu
     */
    public static class Option {
        String optionLetter;
        String optionName;
        String description;
        String usage;
        private Consumer<String> onArgument;
        private Consumer<String> onSelection;
        private Validator validator;
        //below not an option, but used for ceding automatically created option letters
        private boolean automatic;
        Menu menu;
        private final List<SubOption> subOptionList = new ArrayList<>();
        WindowWrapFactory windowWrapFactory;
        private Boolean requireInput;
        private boolean paramSupplied;
        private Boolean requireContext;
        private boolean contextSupplied;

        private Option(Menu menu) {
            this.menu = menu;
        }

        void validateLetter(ValidationContext validationContext) throws InvalidOptionException {
            List<Option> letterCollisions = validationContext.letterOptionMap.computeIfAbsent(this.optionLetter, x-> new ArrayList<>());
            if (letterCollisions.size() > 1) {
                //remove all automatic entries
                for (Iterator<Option> collisionIt = letterCollisions.listIterator(); collisionIt.hasNext();) {
                    Option option = collisionIt.next();
                    if (option.automatic) {
                        option.optionLetter = null;
                        collisionIt.remove();
                    }
                }
                if (letterCollisions.size() > 1) {
                    Option first = letterCollisions.get(0);
                    Option second = letterCollisions.get(1);
                    throw new InvalidOptionException("There already is an option with letter '" + first.optionLetter + "'. Preexisting [" + first.getTextArgs() + "] conflicts with: " + second.getTextArgs());
                }
            }
        }

        void validateName(ValidationContext validationContext) throws InvalidOptionException {
            List<Option> nameCollisions = validationContext.nameOptionMap.computeIfAbsent(this.optionName, x -> new ArrayList<>());
            if (nameCollisions.size() > 1) {
                Option first = nameCollisions.get(0);
                Option second = nameCollisions.get(1);
                throw new InvalidOptionException("There already is an option with name '" + optionName + "'. Preexisting [" + first.getTextArgs() + "] conflicts with: " + second.getTextArgs());
            }
        }

        private void optionLetterInternal(String optionLetter, boolean automatic) throws InvalidOptionException {
            if (!automatic || this.optionLetter == null) {
                this.optionLetter = stripLeadingHyphens(optionLetter);
                this.automatic = automatic;
            }
        }

        /**
         * Specify that the menu item requires a subsequent input parameter
         * @param requireInput flag to override if input should be required
         * @return this builder
         */
        public Option requireInput(boolean requireInput) {
            this.requireInput = requireInput;
            return this;
        }

        private boolean isRequireInput() {
            return requireInput != null ? requireInput : this.onArgument != null;
        }

        /**
         * Set a flag to require the context (sub options) to be provided when specifying a menu item
         *
         * @param requireContext true if an error should be thrown when transitioning prematurely
         * @return
         */
        public Option requireContext(boolean requireContext) {
            this.requireContext = requireContext;
            return this;
        }

        private boolean isRequireContext() {
            return requireContext != null ? requireContext : !subOptionList.isEmpty();
        }

        /**
         * Specify the letter for an option
         * @param optionLetter the letter
         * @return this option builder
         * @throws InvalidOptionException  if the option argument is not at least letter in length after leading hyphen removal
         */
        public Option optionLetter(String optionLetter) throws InvalidOptionException {
            optionLetterInternal(optionLetter, false);
            return this;
        }



        /**
         * Specify the name for an option
         * @param optionName the name for the option
         * @return this option builder
         * @throws InvalidOptionException  if the option argument is not at least letter in length after leading hyphen removal
         */
        public Option optionName(String optionName) throws InvalidOptionException {
            this.optionName = stripLeadingHyphens(optionName);

            if (optionLetter == null && !optionName.contains(":")) {
                String letter = stripLeadingHyphens(optionName).substring(0, 1);
                optionLetterInternal(letter, true);
            }

            return this;
        }

        /**
         * Specify the description for an option
         * @param description the description of the option
         * @return this option builder
         */
        public Option description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Specify the usage for an option
         * @param usage the usage for the option
         * @return this option builder
         */
        public Option usage(String usage) {
            this.usage = usage;
            return this;
        }

        /**
         * Specify the action to be executed when the menu item is selected
         * @param onSelection the action to be executed when the menu item is selected
         * @return this option builder
         */
        public Option onSelection(Consumer<String> onSelection) {
            this.onSelection = onSelection;
            return this;
        }

        /**
         * Specify the action to handle any following arguments.
         * @param onArgument the action to handle the subsequent argument(s)
         * @return this option builder
         */
        public Option onArgument(Consumer<String> onArgument) {
            this.onArgument = onArgument;
            return this;
        }

        /**
         * Specify a validation action to be executed on menu item.
         * This is actioned when transitioning to another state that is not a child state or when all arguments have been processed
         * and this is the final menu item.
         * @param validator the validator to be executed
         * @return this builder
         */
        public Option validation(Validator validator) {
            this.validator = validator;
            return this;
        }

        /**
         * Specify sub-menu options that should be supported by this menu option
         * @param subOptions Sub-menu options to be used with this option
         * @return this option builder
         * @throws InvalidOptionException if there is a conflict name or letter conflict in sub-menu options
         */
        public Option supportSubOptions(SubOption ...subOptions) throws InvalidOptionException {
            if (subOptions != null) {
                ValidationContext subMenuValidationContext = new ValidationContext();
                for (SubOption subOption : subOptions) {
                    if (subOption.optionName != null) {
                        List<Option> nameOptions = subMenuValidationContext.nameOptionMap.computeIfAbsent(subOption.optionName, x -> new ArrayList<>());
                        nameOptions.add(subOption);
                    }
                    if (subOption.optionLetter != null) {
                        List<Option> letterOptions = subMenuValidationContext.letterOptionMap.computeIfAbsent(subOption.optionLetter, x -> new ArrayList<>());
                        letterOptions.add(subOption);
                    }
                }
                for (SubOption subOption : subOptions) {
                    subOption.parent = this;
                    subOption.validateName(subMenuValidationContext);
                    subOption.validateLetter(subMenuValidationContext);
                    this.subOptionList.add(subOption);
                }
            }
            return this;
        }

        /**
         * Construct a new menu option with the given name, letter or description.
         * @param option the new menu option
         * @return a new option builder
         * @throws InvalidOptionException if the option is not sufficient length or there is a conflict with another option in same scope
         */
        public Option newOption(String option) throws InvalidOptionException {
            return menu.option(option);
        }

        /**
         * Accept an argument as a parameter
         * @param value the parameter value
         */
        public void accept(String value) {
            if (onArgument != null) {
                this.paramSupplied = value != null;
                onArgument.accept(value);
            }
        }

        /**
         * Select the menu option
         */
        public void select() {
            if (onSelection != null) {
                onSelection.accept(null);
            }
        }

        /**
         * Get the menu option name/letter for listing in help menu and as mistyped option hints
         * @return the menu options name, letter combination (--name/-letter)
         */
        public String getTextArgs() {
           StringJoiner stringJoiner = new StringJoiner  ("/");
           if (optionName != null) {
               stringJoiner.add("--" + optionName);
           }
           if (optionLetter != null) {
               stringJoiner.add("-" + optionLetter);
           }
           return stringJoiner.toString();
        }

        private double getLowestDistanceNameOrLetter(String noDash) {
            double distanceName = -1;
            if (optionName != null) {
                distanceName = calculateDistance(noDash, optionName);
            }
            double distanceLetter = -1;
            if (optionLetter != null) {
                if (noDash.length() == 1) {
                    distanceLetter = calculateDistance(noDash, optionLetter);
                } else {
                    distanceLetter = Double.MAX_VALUE;
                }
            }

            double score;
            if (distanceLetter == -1 || distanceName < distanceLetter) {
                score = distanceName;
            } else {
                score = distanceLetter;
            }
            return score;
        }

        /**
         * Calculate the best match for an option
         * @param noDash the option name/letter with leading hyphen(s) removed
         * @param evalChildren flag controlling if children should be included in this iteration of analysis
         * @param evalMenu flag controlling if the parent menu should be included in this iteration of analysis
         * @return the best match based on lowest distance
         * @throws InvalidOptionException if the arg identifying the menu item is not at least one letter in length, not counting the leading hyphens
         */
        protected WeightedOption calculateBestMatch(String noDash, boolean evalChildren, boolean evalMenu) throws InvalidOptionException {
            Map<Double, WeightedOption> matchMap = new HashMap<>();
            double scoreThis = getLowestDistanceNameOrLetter(noDash);
            if (scoreThis > -1) {
                matchMap.put(scoreThis, new WeightedOption(this, scoreThis));
            }
            if (scoreThis == 0) {
                return new WeightedOption(this, scoreThis);
            }

            if (evalChildren) {
                for (Option option : subOptionList) {
                    WeightedOption subOption = option.calculateBestMatch(noDash, false, false);//only eval immediate children
                    if (subOption != null && subOption.weight > -1 && !matchMap.containsKey(subOption.weight)) {
                        matchMap.put(subOption.weight, subOption);
                    }
                }
            }
            if (evalMenu) {
                //do this after, so we choose a submenu item vs a top-level item as tie-breaker
                WeightedOption menuOption = menu.getBestWeightedMatch(noDash);
                if (menuOption != null && menuOption.weight > -1 && !matchMap.containsKey(menuOption.weight)) {
                    matchMap.put(menuOption.weight, menuOption);
                }
            }
            return bestInMap(matchMap);
        }

        /**
         * Get the closest match to the specified menu option.
         * When epsilon is 0, this will be an exact match
         * @param arg the name/letter of the menu option
         * @param epsilon the degree of lenience accepted before saying there are no matches
         * @return the closest match to a requested menu option
         * @throws InvalidOptionException if arg is not at least one letter long after the leading hyphens
         */
        public Optional<? extends Option> getBestMatchByDistance(String arg, double epsilon) throws InvalidOptionException {
            String noDash = stripLeadingHyphens(arg);
            WeightedOption bestMatch = calculateBestMatch(noDash, true, true);
            return bestMatch == null ? Optional.empty()
                    : bestMatch.weight <= epsilon ? Optional.of(bestMatch.option)
                    : Optional.empty();
        }

        @Override
        public String toString() {
            String lineSeparator = getLineSeparator();
            if (windowWrapFactory == null) {
                windowWrapFactory = new WindowWrapFactory(110, lineSeparator);
            }
            return getTextArgs() + "\t" +
                            description + subOptionList.stream()
                            .map(option ->  {
                                String result = lineSeparator + "\t\twith " + option.getTextArgs() + ", " + option.description;
                                if (option.usage != null) {
                                    result += lineSeparator + windowWrapFactory.wrap("\t\t\tUsage: " + option.usage);
                                }
                                return result;
                            })
                            .collect(Collectors.joining("")) +
                            (usage != null
                                    ? lineSeparator + windowWrapFactory.wrap("\t\tUsage: " + usage)
                                    : "")
                            + lineSeparator
                    ;
        }

        /**
         * Build the menu
         * @return the built menu
         * @throws InvalidOptionException if there are duplicate letters or names within a scope
         */
        public Menu build() throws InvalidOptionException {
            ValidationContext validationContext = new ValidationContext();
            for (Option option : menu.optionList) {
                if (option.optionName != null) {
                    List<Option> nameOptions = validationContext.nameOptionMap.computeIfAbsent(option.optionName, x -> new ArrayList<>());
                    nameOptions.add(option);
                }
                if (option.optionLetter != null) {
                    List<Option> letterOptions = validationContext.letterOptionMap.computeIfAbsent(option.optionLetter, x -> new ArrayList<>());
                    letterOptions.add(option);
                }
            }

            for (Option option : menu.optionList) {
                option.validateLetter(validationContext);
                option.validateName(validationContext);
            }
            return menu;
        }

        /**
         * Check if an argument matches this options letter or name.  The arg must start with at least one hyphen for this to evaluate true
         * @param arg the name,letter to test
         * @return true if the option matches
         */
        public boolean matches(String arg) {
            return arg.equals("-" + optionLetter)
                    || arg.equals("--" + optionName);
        }


        /**
         * Validate a menu item prior to transitioning to another menu item
         * Notes:
         * - if {@link #requireInput(boolean)} has not been set manually, the condition requiring input defaults to true if there is an {@link #onArgument} handler
         * - if {@link #requireContext(boolean)} has not been set manually, the condition requiring submenu options defaults to false
         * - If {@link #validation(Validator)} has been set, the validation will be applied if there are no children or this validation has been called from a sub menu item
         *  and we're switching away from this menu item's context
         * @param option the option about to be transitioned to.  This will be checked against expected child options
         * @throws InvalidUsageException if no argument has been supplied and the menu item requires one, or if a menu item requires
         * additional submenu options and has not received them
         */
        public void validate(Option option) throws InvalidUsageException {
            if (isRequireInput() && !paramSupplied) {
                throw new InvalidUsageException("Menu item [" + getTextArgs() + "] requires a value");
            }
            if (subOptionList.isEmpty() && validator != null) {
                validator.validate();
            }
            if (isRequireContext()) {
                if (option == null) {
                    if (!contextSupplied) {
                        throw new InvalidUsageException("Menu item [" + getTextArgs() + "] requires additional context.  See --help for available options");
                    }
                } else {
                    //option not null
                    if (subOptionList.contains(option)) {
                        contextSupplied = true;
                    } else {
                        throw new InvalidUsageException("Menu item [" + getTextArgs() + "] requires additional context.  See --help for available options");
                    }
                }
                //apply validator if we are exiting the menu or context of the menu option
                if (validator != null && (option == null || !subOptionList.contains(option))) {
                    validator.validate();
                }
            }
        }
    }

    /**
     * Sub-menu options augmenting the functionality of top-level menu options
     */
    public static class SubOption extends Option {
        private Option parent;
        private SubOption(Menu menu) {
            super(menu);
        }

        @Override
        public String toString() {
            return description;
        }

        @Override
        public Optional<? extends Option> getBestMatchByDistance(String arg, double epsilon) throws InvalidOptionException {
            Map<Double, WeightedOption> matchMap = new HashMap<>();
            String noDash = stripLeadingHyphens(arg);
            WeightedOption bestSubMatch = calculateBestMatch(noDash, true, false);
            if (bestSubMatch != null) {
                if (bestSubMatch.weight == 0) {
                    return Optional.of(bestSubMatch.option);
                } else {
                    matchMap.put(bestSubMatch.weight, bestSubMatch);
                }
            }
            WeightedOption bestSiblingMatch = parent.calculateBestMatch(noDash, true, false);
            if (bestSiblingMatch != null) {
                if (bestSiblingMatch.weight == 0) {
                    return Optional.of(bestSiblingMatch.option);
                } else {
                    matchMap.putIfAbsent(bestSiblingMatch.weight, bestSiblingMatch);
                }
            }

            WeightedOption bestMenuMatch = parent.calculateBestMatch(noDash, false, true);
            if (bestMenuMatch != null) {
                if (bestMenuMatch.weight == 0) {
                    return Optional.of(bestMenuMatch.option);
                } else {
                    matchMap.putIfAbsent(bestMenuMatch.weight, bestMenuMatch);
                }
            }
            WeightedOption bestOverall = bestInMap(matchMap);
            return bestOverall == null ? Optional.empty()
                    : bestOverall.weight <= epsilon ? Optional.of(bestOverall.option)
                    : Optional.empty();
        }

        @Override
        public void validate(Option option) throws InvalidUsageException {
            super.validate(option);
            parent.validate(option);
        }
    }

    private static class ValidationContext {
        private final Map<String, List<Option>> letterOptionMap = new HashMap<>();
        private final Map<String, List<Option>> nameOptionMap = new HashMap<>();

    }

    private static class WeightedOption {
        private final Menu.Option option;
        private final Double weight;

        private WeightedOption(Menu.Option option, Double weight) {
            this.option = option;
            this.weight = weight;
        }
    }

    /**
     * Validator for validating menu options
     */
    @FunctionalInterface
    public interface Validator {
        /**
         * Validate menu input
         * @throws InvalidUsageException if the usage is invalid
         */
        void validate() throws InvalidUsageException;
    }
}
