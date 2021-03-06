package me.loki2302.syntax.expectations.element.statement.composite;

import static org.junit.Assert.assertTrue;

import java.util.List;

import me.loki2302.syntax.dom.statements.DOMCompositeStatement;
import me.loki2302.syntax.dom.statements.DOMStatement;
import me.loki2302.syntax.expectations.element.statement.StatementExpectation;

public class CompositeStatementHasChildExpectation implements CompositeStatementExpectation {
    private final int childIndex;
    private final StatementExpectation[] expectations;
    
    public CompositeStatementHasChildExpectation(
            int childIndex, 
            StatementExpectation[] expectations) {
        
        this.childIndex = childIndex;
        this.expectations = expectations;
    }

    @Override
    public void check(DOMCompositeStatement domCompositeStatement) {
        List<DOMStatement> childStatements = domCompositeStatement.getStatements();
        assertTrue(childStatements.size() > childIndex);
        DOMStatement domStatement = childStatements.get(childIndex);
        for(StatementExpectation expectation : expectations) {
            expectation.check(domStatement);
        }
    }
}