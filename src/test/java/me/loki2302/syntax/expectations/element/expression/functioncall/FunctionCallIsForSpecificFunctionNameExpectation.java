package me.loki2302.syntax.expectations.element.expression.functioncall;


import me.loki2302.syntax.dom.expressions.DOMFunctionCallExpression;
import static org.junit.Assert.*;

public class FunctionCallIsForSpecificFunctionNameExpectation implements FunctionCallExpressionExpectation {
    private final String functionName;
    
    public FunctionCallIsForSpecificFunctionNameExpectation(String functionName) {
        this.functionName = functionName;
    }

    @Override
    public void check(DOMFunctionCallExpression domFunctionCallExpression) {
        assertEquals(functionName, domFunctionCallExpression.getFunctionName());            
    }
}