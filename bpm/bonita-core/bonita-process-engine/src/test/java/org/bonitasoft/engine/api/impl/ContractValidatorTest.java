package org.bonitasoft.engine.api.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bonitasoft.engine.core.process.definition.model.SContractDefinition;
import org.bonitasoft.engine.core.process.definition.model.SType;
import org.bonitasoft.engine.core.process.definition.model.impl.SContractDefinitionImpl;
import org.bonitasoft.engine.core.process.definition.model.impl.SInputDefinitionImpl;
import org.bonitasoft.engine.core.process.definition.model.impl.SRuleDefinitionImpl;
import org.bonitasoft.engine.log.technical.TechnicalLogSeverity;
import org.bonitasoft.engine.log.technical.TechnicalLoggerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ContractValidatorTest {

    private static final String BOOLEAN_INPUT = "booleanInput";

    private static final String NUMBER_INPUT = "numberInput";

    private static final String NICE_COMMENT = "no way!";

    private static final String COMMENT = "comment";

    private static final String IS_VALID = "isValid";

    private static final String DATE_INPUT = "dateInput";

    private static final String DECIMAL_INPUT = "decimalInput";

    @Mock
    private TechnicalLoggerService loggerService;

    @InjectMocks
    private ContractValidator validator;

    private SContractDefinition buildContractWithInputsAndRules() {
        final SContractDefinitionImpl contract = buildEmptyContract();
        addInputsToContract(contract);
        addRulesToContractWithInputs(contract);
        return contract;
    }

    private void addRulesToContractWithInputs(final SContractDefinition contract) {
        final SRuleDefinitionImpl rule1 = new SRuleDefinitionImpl("Mandatory", "isValid != null", "isValid must be set");
        rule1.addInputName(IS_VALID);
        contract.getRules().add(rule1);
        final SRuleDefinitionImpl rule2 = new SRuleDefinitionImpl("Comment_Needed_If_Not_Valid", "isValid || !isValid && comment != null",
                "A comment is required when no validation");
        rule2.addInputName(IS_VALID);
        rule2.addInputName(COMMENT);
        contract.getRules().add(rule2);
    }

    private SContractDefinition addInputsToContract(final SContractDefinition contract) {
        final SInputDefinitionImpl input1 = new SInputDefinitionImpl(IS_VALID);
        input1.setType(SType.BOOLEAN);
        contract.getInputs().add(input1);
        final SInputDefinitionImpl input2 = new SInputDefinitionImpl(COMMENT);
        input2.setType(SType.TEXT);
        contract.getInputs().add(input2);
        return contract;
    }

    private SContractDefinitionImpl buildEmptyContract() {
        return new SContractDefinitionImpl();
    }

    @Test
    public void isValid_should_be_true_an_return_an_empty_list() throws Exception {
        //given
        final Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(IS_VALID, true);
        variables.put(COMMENT, null);
        final SContractDefinition contract = buildContractWithInputsAndRules();

        //when
        final boolean valid = validator.isValid(contract, variables);

        //then
        assertThat(valid).isTrue();
        assertThat(validator.getComments()).isEmpty();
    }

    @Test
    public void isValid_should_be_false_an_return_explanations() throws Exception {
        //given
        final Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(IS_VALID, false);
        variables.put(COMMENT, null);
        final SContractDefinition contract = buildContractWithInputsAndRules();

        //when
        final boolean valid = validator.isValid(contract, variables);

        //then
        assertThat(valid).isFalse();
        final List<String> explanations = validator.getComments();
        assertThat(explanations).hasSize(1);
        assertThat(explanations.get(0)).isEqualTo("A comment is required when no validation");
    }

    @Test
    public void isValid_should_log_all_rules_in_debug_mode() throws Exception {
        //given
        final Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(IS_VALID, false);
        variables.put(COMMENT, NICE_COMMENT);
        final SContractDefinition contract = buildContractWithInputsAndRules();
        when(loggerService.isLoggable(ContractValidator.class, TechnicalLogSeverity.DEBUG)).thenReturn(true);
        when(loggerService.isLoggable(ContractValidator.class, TechnicalLogSeverity.WARNING)).thenReturn(true);

        //when
        validator.isValid(contract, variables);

        //then
        verify(loggerService).log(ContractValidator.class, TechnicalLogSeverity.DEBUG, "Evaluating rule [Mandatory] on input(s) [isValid]");
        verify(loggerService).log(ContractValidator.class, TechnicalLogSeverity.DEBUG,
                "Evaluating rule [Comment_Needed_If_Not_Valid] on input(s) [isValid, comment]");
        verify(loggerService, never()).log(eq(ContractValidator.class), eq(TechnicalLogSeverity.WARNING), anyString());
    }

    @Test
    public void isValid_should_log_invalid_rules_in_warning_mode() throws Exception {
        //given
        final Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(IS_VALID, false);
        variables.put(COMMENT, null);
        final SContractDefinition contract = buildContractWithInputsAndRules();
        when(loggerService.isLoggable(ContractValidator.class, TechnicalLogSeverity.DEBUG)).thenReturn(false);
        when(loggerService.isLoggable(ContractValidator.class, TechnicalLogSeverity.WARNING)).thenReturn(true);

        //when
        validator.isValid(contract, variables);

        //then
        verify(loggerService).log(ContractValidator.class, TechnicalLogSeverity.WARNING,
                "Rule [Comment_Needed_If_Not_Valid] on input(s) [isValid, comment] is not valid");
        verify(loggerService, never()).log(eq(ContractValidator.class), eq(TechnicalLogSeverity.DEBUG), anyString());
    }

    @Test
    public void isValid_should_be_false_when_inputs_are_unexpected() throws Exception {
        //given
        final Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(IS_VALID, false);
        variables.put(COMMENT, null);
        final SContractDefinition contract = new SContractDefinitionImpl();
        SInputDefinitionImpl sInputDefinition = new SInputDefinitionImpl(IS_VALID);
        sInputDefinition.setType(SType.BOOLEAN);
        contract.getInputs().add(sInputDefinition);

        //when
        final boolean valid = validator.isValid(contract, variables);

        //then
        assertThat(valid).as("should refuse when inputs are unexpected").isFalse();
        assertThat(validator.getComments()).hasSize(1).containsExactly("variable " + COMMENT + " is not expected");
    }

    @Test
    public void isValid_should_be_false_when_inputs_are_missing() throws Exception {
        //given
        final Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(COMMENT, NICE_COMMENT);
        final SContractDefinition contract = new SContractDefinitionImpl();
        SInputDefinitionImpl sInputDefinition = new SInputDefinitionImpl(IS_VALID);
        sInputDefinition.setType(SType.BOOLEAN);
        contract.getInputs().add(sInputDefinition);
        SInputDefinitionImpl sInputDefinition2 = new SInputDefinitionImpl(COMMENT);
        sInputDefinition2.setType(SType.TEXT);
        contract.getInputs().add(sInputDefinition2);

        //when
        final boolean valid = validator.isValid(contract, variables);

        //then
        assertThat(valid).as("should refuse when inputs are unexpected").isFalse();
        assertThat(validator.getComments()).isNotEmpty().contains(IS_VALID + " is not defined");
    }

    @Test
    public void isValid_should_be_false_when_inputs_are_missing_and_contract_has_no_rules() throws Exception {
        //given
        final Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(IS_VALID, false);
        final SContractDefinition contract = addInputsToContract(buildEmptyContract());

        //when
        final boolean valid = validator.isValid(contract, variables);

        //then
        assertThat(valid).as("should refuse when inputs are unexpected").isFalse();
        assertThat(validator.getComments()).hasSize(1).containsExactly(COMMENT + " is not defined");
    }

    @Test
    public void isValid_should_be_true_when_no_contract_and_no_inputs() throws Exception {
        //given
        final Map<String, Object> variables = new HashMap<String, Object>();
        final SContractDefinition contract = buildEmptyContract();

        //when
        final boolean valid = validator.isValid(contract, variables);

        //then
        assertThat(valid).as("should validate when contract is empty and no inputs are provided").isTrue();
    }

    @Test
    public void isValid_should_be_true_when_inputs_meets_contract() throws Exception {
        //given
        final Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(IS_VALID, false);
        variables.put(COMMENT, NICE_COMMENT);
        final SContractDefinition contract = buildContractWithInputsAndRules();

        //when
        final boolean valid = validator.isValid(contract, variables);

        //then
        assertThat(validator.getComments()).as("should have no comments").isEmpty();
        assertThat(valid).as("should validate contract").isTrue();
    }

    @Test
    public void isValid_should_be_true_when_inputs_meets_contract_without_rules() throws Exception {
        //given
        final Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(IS_VALID, false);
        variables.put(COMMENT, NICE_COMMENT);
        final SContractDefinition contract = addInputsToContract(buildEmptyContract());

        //when
        final boolean valid = validator.isValid(contract, variables);

        //then
        assertThat(valid).as("should validate contract without rules").isTrue();
    }

    @Test
    public void isValid_should_be_false_when_rule_fails_to_evaluate() throws Exception {
        //given
        final Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(IS_VALID, false);
        variables.put(COMMENT, NICE_COMMENT);
        final SContractDefinition contract = buildContractWithInputsAndRules();
        final SRuleDefinitionImpl badRule = new SRuleDefinitionImpl("bad rule", "a == b", "failing rule");
        contract.getRules().add(badRule);

        //when
        final boolean valid = validator.isValid(contract, variables);

        //then
        assertThat(valid).as("should validate contract without rules").isFalse();
        assertThat(validator.getComments()).hasSize(1).containsExactly(badRule.getExplanation());
    }

    @Test
    public void isValid_should_check_input_type() throws Exception {
        //given
        final SContractDefinition contract = buildEmptyContract();
        final SInputDefinitionImpl sInputDefinition = new SInputDefinitionImpl(NUMBER_INPUT);
        sInputDefinition.setType(SType.INTEGER);
        contract.getInputs().add(sInputDefinition);

        final SInputDefinitionImpl sInputDefinition2 = new SInputDefinitionImpl(BOOLEAN_INPUT);
        sInputDefinition2.setType(SType.BOOLEAN);
        contract.getInputs().add(sInputDefinition2);

        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(sInputDefinition2.getName());
        stringBuilder.append(".class.isInstance(");
        stringBuilder.append(sInputDefinition2.getType());
        stringBuilder.append(")");

        final Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(NUMBER_INPUT, "abc");
        variables.put(BOOLEAN_INPUT, true);

        //when
        final boolean valid = validator.isValid(contract, variables);

        //then
        assertThat(valid).as("should validate contract without rules").isFalse();
        assertThat(validator.getComments()).isNotEmpty().contains("unable to bind input numberInput to type " + BigInteger.class.getName());
    }

    @Test
    public void isValid_should_validate_input_type() throws Exception {
        //given
        final SContractDefinition contract = buildEmptyContract();
        final SInputDefinitionImpl sInputDefinition = new SInputDefinitionImpl(NUMBER_INPUT);
        sInputDefinition.setType(SType.INTEGER);
        contract.getInputs().add(sInputDefinition);

        final SInputDefinitionImpl sInputDefinition2 = new SInputDefinitionImpl(BOOLEAN_INPUT);
        sInputDefinition2.setType(SType.BOOLEAN);
        contract.getInputs().add(sInputDefinition2);

        final SInputDefinitionImpl sInputDefinition3 = new SInputDefinitionImpl(DATE_INPUT);
        sInputDefinition3.setType(SType.DATE);
        contract.getInputs().add(sInputDefinition3);

        final SInputDefinitionImpl sInputDefinition4 = new SInputDefinitionImpl(DECIMAL_INPUT);
        sInputDefinition4.setType(SType.DECIMAL);
        contract.getInputs().add(sInputDefinition4);

        final Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(NUMBER_INPUT, BigInteger.valueOf(123));
        variables.put(BOOLEAN_INPUT, false);
        variables.put(DATE_INPUT, new Date());
        variables.put(DECIMAL_INPUT, BigDecimal.valueOf(1.5f));

        //when
        final boolean valid = validator.isValid(contract, variables);

        //then
        assertThat(validator.getComments()).isEmpty();
        assertThat(valid).as("should validate contract without rules").isTrue();
    }

}
