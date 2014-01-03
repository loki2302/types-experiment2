package me.loki2302.syntax.expectations.element.expression.functioncall;

import static org.junit.Assert.assertEquals;

import java.util.List;

import me.loki2302.syntax.dom.DOMExpression;
import me.loki2302.syntax.dom.DOMFunctionCallExpression;

public class FunctionCallHasArgumentsExpectation implements FunctionCallExpressionExpectation {
    private final int argumentCount;
    
    public FunctionCallHasArgumentsExpectation(int parameterCount) {
        this.argumentCount = parameterCount;
    }
    
    @Override
    public void check(DOMFunctionCallExpression domFunctionCallExpression) {
        List<DOMExpression> arguments = domFunctionCallExpression.getArguments(); 
        assertEquals(argumentCount, arguments.size());
    }
}