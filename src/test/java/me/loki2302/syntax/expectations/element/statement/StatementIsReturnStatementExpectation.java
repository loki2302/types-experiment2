package me.loki2302.syntax.expectations.element.statement;

import static org.junit.Assert.assertTrue;
import me.loki2302.syntax.dom.statements.DOMReturnStatement;
import me.loki2302.syntax.dom.statements.DOMStatement;
import me.loki2302.syntax.expectations.element.statement.returnstatement.ReturnStatementExpectation;

public class StatementIsReturnStatementExpectation implements StatementExpectation {
    private final ReturnStatementExpectation[] expectations;
    
    public StatementIsReturnStatementExpectation(ReturnStatementExpectation[] expectations) {
        this.expectations = expectations;
    }
    
    @Override
    public void check(DOMStatement domStatement) {
        assertTrue(domStatement instanceof DOMReturnStatement);
        DOMReturnStatement domReturnStatement = (DOMReturnStatement)domStatement;
        for(ReturnStatementExpectation expectation : expectations) {
            expectation.check(domReturnStatement);
        }
    }
}