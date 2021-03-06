package me.loki2302.syntax.expectations.element.expression.functioncall;

import static org.junit.Assert.*;

import java.util.List;

import me.loki2302.syntax.dom.expressions.DOMExpression;
import me.loki2302.syntax.dom.expressions.DOMFunctionCallExpression;
import me.loki2302.syntax.expectations.element.expression.ExpressionExpectation;

public class FunctionCallHasArgument implements FunctionCallExpressionExpectation {
    private final int argumentIndex;
    private final ExpressionExpectation[] expectations;
    
    public FunctionCallHasArgument(
            int argumentIndex, 
            ExpressionExpectation[] expressionExpectations) {
        this.argumentIndex = argumentIndex;
        this.expectations = expressionExpectations;
    }

    @Override
    public void check(DOMFunctionCallExpression domFunctionCallExpression) {
        List<DOMExpression> arguments = domFunctionCallExpression.getArguments(); 
        assertTrue(arguments.size() > argumentIndex - 1);
        DOMExpression argumentExpression = arguments.get(argumentIndex);
        for(ExpressionExpectation expectation : expectations) {
            expectation.check(argumentExpression);
        }
    }
}