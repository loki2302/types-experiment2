package me.loki2302.syntax.expectations.element.statement;

import static org.junit.Assert.assertTrue;
import me.loki2302.syntax.dom.statements.DOMStatement;
import me.loki2302.syntax.dom.statements.DOMVariableDefinitionStatement;
import me.loki2302.syntax.expectations.element.statement.variabledefinition.VariableDefinitionStatementExpectation;

public class StatementIsVariableDefinitionStatementExpectation implements StatementExpectation {
    private final VariableDefinitionStatementExpectation[] expectations;
    
    public StatementIsVariableDefinitionStatementExpectation(VariableDefinitionStatementExpectation[] expectations) {
        this.expectations = expectations;
    }

    @Override
    public void check(DOMStatement domStatement) {
        assertTrue(domStatement instanceof DOMVariableDefinitionStatement);
        DOMVariableDefinitionStatement domVariableDefinitionStatement = (DOMVariableDefinitionStatement)domStatement;
        for(VariableDefinitionStatementExpectation expectation : expectations) {
            expectation.check(domVariableDefinitionStatement);
        }               
    }
}