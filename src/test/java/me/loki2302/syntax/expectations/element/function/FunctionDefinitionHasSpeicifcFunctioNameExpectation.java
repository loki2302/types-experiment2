package me.loki2302.syntax.expectations.element.function;

import static org.junit.Assert.assertEquals;
import me.loki2302.syntax.dom.functions.DOMFunctionDefinition;

public class FunctionDefinitionHasSpeicifcFunctioNameExpectation implements FunctionDefinitionExpectation {
    private final String functionName;
    
    public FunctionDefinitionHasSpeicifcFunctioNameExpectation(String functionName) {
        this.functionName = functionName;
    }
    
    @Override
    public void check(DOMFunctionDefinition domFunctionDefinition) {
        assertEquals(functionName, domFunctionDefinition.getFunctionName());
    }	    
}