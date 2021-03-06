package me.loki2302.syntax.expectations.element.expression.unary;

import me.loki2302.syntax.dom.expressions.DOMExpression;
import me.loki2302.syntax.dom.expressions.DOMUnaryExpression;
import me.loki2302.syntax.expectations.element.expression.ExpressionExpectation;

public class UnaryExpressionHasSpecificInnerExpressionExpectation implements UnaryExpressionExpectation {
    private final ExpressionExpectation[] expectations;
    
    public UnaryExpressionHasSpecificInnerExpressionExpectation(ExpressionExpectation[] expectations) {
        this.expectations = expectations;
    }
    
    @Override
    public void check(DOMUnaryExpression domUnaryExpression) {
        DOMExpression innerExpression = domUnaryExpression.getInnerExpression();
        for(ExpressionExpectation expectation : expectations) {
            expectation.check(innerExpression);
        }
    }	    
}